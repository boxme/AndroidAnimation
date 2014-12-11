package com.desmond.androidanimation.CardFlipAnimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.desmond.androidanimation.R;

import java.util.ArrayList;
import java.util.List;

public class CardFlipActivity extends ActionBarActivity implements CardFlipListener {

    final static int CARD_PILE_OFFSET = 3;
    final static int STARTING_NUMBER_CARDS = 15;
    final static int RIGHT_STACK = 0;
    final static int LEFT_STACK = 1;

    int mCardWidth = 0;
    int mCardHeight = 0;

    int mVerticalPadding;
    int mHorizontalPadding;

    boolean mTouchEventsEnabled = true;
    boolean[] mIsStackEnabled;

    RelativeLayout mLayout;

    List<ArrayList<CardView>> mStackCards;

    GestureDetector gDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_flip);

        mStackCards = new ArrayList<ArrayList<CardView>>();

        // Left & Right stacks
        mStackCards.add(new ArrayList<CardView>());
        mStackCards.add(new ArrayList<CardView>());

        mIsStackEnabled = new boolean[2];
        mIsStackEnabled[0] = true;
        mIsStackEnabled[1] = true;

        mVerticalPadding = getResources().getInteger(R.integer.vertical_card_magin);
        mHorizontalPadding = getResources().getInteger(R.integer.horizontal_card_magin);

        gDetector = new GestureDetector(this, mGestureListener);

        mLayout = (RelativeLayout)findViewById(R.id.main_relative_layout);
        ViewTreeObserver observer = mLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                // Get the measured card width and height before onDraw()
                mCardHeight = mLayout.getHeight();
                mCardWidth = mLayout.getWidth() / 2;

                for (int x = 0; x < STARTING_NUMBER_CARDS; x++) {
                    addNewCard(RIGHT_STACK);
                }
            }
        });
    }

    /**
     * Adds a new card to the specified stack. Also performs all the necessary layout setup
     * to place the card in the correct position.
     */
    public void addNewCard(int stack) {
        CardView view = new CardView(this);
        view.updateTranslation(mStackCards.get(stack).size());
        view.setCardFlipListener(this);
        view.setPadding(mHorizontalPadding, mVerticalPadding, mHorizontalPadding, mVerticalPadding);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mCardWidth,
                mCardHeight);
        params.topMargin = 0;
        params.leftMargin = (stack == RIGHT_STACK ? mCardWidth : 0);

        mStackCards.get(stack).add(view);
        mLayout.addView(view, params);
    }

    /**
     * Returns the appropriate stack corresponding to the MotionEvent.
     */
    public int getStack(MotionEvent ev) {
        boolean isLeft = ev.getX() <= mCardWidth;
        return isLeft ? LEFT_STACK : RIGHT_STACK;
    }

    /**
     * Uses the stack parameter, along with the velocity values of the fling event
     * to determine in what direction the card must be flipped. By the same logic, the
     * new stack that the card belongs to after the animation is also determined
     * and updated.
     */
    public void rotateCardView(final CardView cardView, int stack, float velocityX,
                               float velocityY) {

        // xGreaterThanY is true if it's a horizontal swipe
        boolean xGreaterThanY = Math.abs(velocityX) > Math.abs(velocityY);

        boolean bothStacksEnabled = mIsStackEnabled[RIGHT_STACK] && mIsStackEnabled[LEFT_STACK];

        ArrayList<CardView>leftStack = mStackCards.get(LEFT_STACK);
        ArrayList<CardView>rightStack = mStackCards.get(RIGHT_STACK);

        switch (stack) {
            case RIGHT_STACK:
                // Flip from right to left
                if (velocityX < 0 &&  xGreaterThanY) {
                    if (!bothStacksEnabled) {
                        break;
                    }

                    mLayout.bringChildToFront(cardView);
                    mLayout.requestLayout();

                    rightStack.remove(rightStack.size() - 1);
                    leftStack.add(cardView);

                    // Give the original stack size, hence minus by 1
                    cardView.flipRightToLeft(leftStack.size() - 1, (int)velocityX);
                    break;
                }
                else if (!xGreaterThanY) {
                    // Rotate the cards out
                    boolean rotateCardsOut = velocityY > 0;
                    rotateCards(RIGHT_STACK, CardView.Corner.BOTTOM_LEFT, rotateCardsOut);
                }
                break;

            case LEFT_STACK:
                // Flip from left to right
                if (velocityX > 0 && xGreaterThanY) {
                    if (!bothStacksEnabled) {
                        break;
                    }

                    mLayout.bringChildToFront(cardView);
                    mLayout.requestLayout();

                    leftStack.remove(leftStack.size() - 1);
                    rightStack.add(cardView);

                    // Give the original stack size, hence minus by 1
                    cardView.flipLeftToRight(rightStack.size() - 1, (int)velocityX);
                    break;
                }
                else if (!xGreaterThanY) {
                    // Rotate the cards out
                    boolean rotateCardsOut = velocityY > 0;
                    rotateCards(LEFT_STACK, CardView.Corner.BOTTOM_LEFT, rotateCardsOut);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCardFlipEnd() {
        mTouchEventsEnabled = true;
    }

    @Override
    public void onCardFlipStart() {
        mTouchEventsEnabled = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {

        // If the custom touch event is locked, pass it on to the super class
        if (mTouchEventsEnabled) {
            return gDetector.onTouchEvent(me);
        }
        else {
            return super.onTouchEvent(me);
        }
    }

    /**
     * Retrieves an animator object for each card in the specified stack that either
     * rotates it in or out depending on its current state. All of these animations
     * are then played together.
     */
    public void rotateCards (final int stack, CardView.Corner corner,
                             final boolean isRotatingOut) {
        List<Animator> animations = new ArrayList<Animator>();

        ArrayList<CardView> cards = mStackCards.get(stack);

        for (int i = 0; i < cards.size(); i++) {
            CardView cardView = cards.get(i);
            animations.add(cardView.getRotationAnimator(i, corner, isRotatingOut, false));
            mLayout.bringChildToFront(cardView);
        }
        /** All the cards are being brought to the front in order to guarantee that
         * the cards being rotated in the current stack will overlay the cards in the
         * other stack. After the z-ordering of all the cards is updated, a layout must
         * be requested in order to apply the changes made.*/
        mLayout.requestLayout();

        AnimatorSet set = new AnimatorSet();
        set.playTogether(animations);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // If the stack of cards has been rotated out, then it should not be
                // allowed for the flipping animation
                mIsStackEnabled[stack] = !isRotatingOut;
            }
        });
        set.start();
    }

    /**
     * Retrieves an animator object for each card in the specified stack to complete a
     * full revolution around one of its corners, and plays all of them together.
     */
    public void rotateCardsFullRotation (int stack, CardView.Corner corner) {
        List<Animator> animations = new ArrayList<Animator>();

        ArrayList <CardView> cards = mStackCards.get(stack);
        for (int i = 0; i < cards.size(); i++) {
            CardView cardView = cards.get(i);
            animations.add(cardView.getFullRotationAnimator(i, corner, false));

            // Change the Z-order of the child so it's on top of all other children
            mLayout.bringChildToFront(cardView);
        }
        /** Same reasoning for bringing cards to front as in rotateCards().*/
        mLayout.requestLayout();

        mTouchEventsEnabled = false;
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animations);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mTouchEventsEnabled = true;
            }
        });
        set.start();
    }


    /**
     * Gesture Detector listens for fling events in order to potentially initiate
     * a card flip event when a fling event occurs. Also listens for tap events in
     * order to potentially initiate a full rotation animation.
     */
    private GestureDetector.SimpleOnGestureListener mGestureListener =
            new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            int stack = getStack(motionEvent);
            rotateCardsFullRotation(stack, CardView.Corner.BOTTOM_LEFT);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float velocityX,
                               float velocityY) {

            // Get Left or Right stack
            int stack = getStack(motionEvent);
            ArrayList<CardView> cardStack = mStackCards.get(stack);
            int size = cardStack.size();
            if (size > 0) {
                rotateCardView(cardStack.get(size - 1), stack, velocityX, velocityY);
            }
            return true;
        }
    };

}
