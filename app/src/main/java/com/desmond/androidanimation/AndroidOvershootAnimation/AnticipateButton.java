package com.desmond.androidanimation.AndroidOvershootAnimation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;

/**
 * Custom button which can be deformed by skewing the top left and right, to simulate
 * anticipation and follow-through animation effects. Clicking on the button runs
 * an animation which moves the button left or right, applying the skew effect to the
 * button. The logic of drawing the button with a skew transform is handled in the
 * draw() override.
 */
public class AnticipateButton extends Button {

    private static final LinearInterpolator sLinearInterpolator = new LinearInterpolator();
    private static final DecelerateInterpolator sDecelerator = new DecelerateInterpolator(8);
    private static final AccelerateInterpolator sAccelerator = new AccelerateInterpolator();
    private static final OvershootInterpolator sOvershooter = new OvershootInterpolator();
    private static final DecelerateInterpolator sQuickDecelerator = new DecelerateInterpolator();

    private float mSkewX = 0;
    ObjectAnimator mDownAnim = null;
    boolean mOnLeft = true;                         // Skew to the left?
    RectF mTempRect = new RectF();

    public AnticipateButton(Context context) {
        super(context);
        init();
    }

    public AnticipateButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnticipateButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnTouchListener(mTouchListener);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                runClickAnim();
            }
        });
    }

    /**
     * The skew effect is handled by changing the transform of the Canvas
     * and then call the usual superclass draw() method (Important)
     * @param canvas
     */
    @Override
    public void draw(Canvas canvas) {
        if (mSkewX != 0) {
            canvas.translate(0, getHeight());
            canvas.skew(mSkewX, 0);
            canvas.translate(0, -getHeight());
        }
        super.draw(canvas);
    }

    /**
     * Anticipate the future animation by rearing back, away from the direction of travel
     */
    private void runPressAnim() {
        mDownAnim = ObjectAnimator.ofFloat(this, "skewX", mOnLeft ? .5f : -.5f);
        mDownAnim.setDuration(2500);
        mDownAnim.setInterpolator(sDecelerator);
        mDownAnim.start();
    }

    /**
     * Finish the "anticipation" animation (skew the button away from the direction of
     * travel), animate it to the other side of the screen, then un-skew the button with
     * an Overshoot effect
     */
    private void runClickAnim() {
        // Anticipation
        ObjectAnimator finishDownAnim = null;
        if (mDownAnim != null && mDownAnim.isRunning()) {
            // Finish the skew animation quickly
            mDownAnim.cancel();
            finishDownAnim = ObjectAnimator.ofFloat(this, "skewX", mOnLeft ? .5f : -.5f);
            finishDownAnim.setDuration(150);
            finishDownAnim.setInterpolator(sQuickDecelerator);
        }

        // Slide. Use LinearInterpolator in this rare situation where we want to start
        // and end fast (no acceleration or deceleration, since we're doing that part
        // during the anticipation and overshot phases).
        ObjectAnimator moveAnim = ObjectAnimator.ofFloat(this, "translationX", mOnLeft ? 400 : 0);
        moveAnim.setInterpolator(sLinearInterpolator);
        moveAnim.setDuration(150);

        // Then overshoot by stopping the movement but skewing the button as if it couldn't
        // all stop at once
        ObjectAnimator skewAnim = ObjectAnimator.ofFloat(this, "skewX", mOnLeft ? -.5f : .5f);
        skewAnim.setInterpolator(sQuickDecelerator);
        skewAnim.setDuration(100);

        // and wobble it
        ObjectAnimator wobbleAnim = ObjectAnimator.ofFloat(this, "skewX", 0);
        wobbleAnim.setInterpolator(sOvershooter);
        wobbleAnim.setDuration(150);
        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(moveAnim, skewAnim, wobbleAnim);
        if (finishDownAnim != null) {
            animSet.play(finishDownAnim).before(moveAnim);
        }

        animSet.start();
        mOnLeft = !mOnLeft;
    }

    /**
     * Restore the button to its un-pressed state
     */
    private void runCancelAnim() {
        if (mDownAnim != null && mDownAnim.isRunning()) {
            mDownAnim.cancel();
            ObjectAnimator reverser = ObjectAnimator.ofFloat(this, "skewX", 0);
            reverser.setDuration(200);
            reverser.setInterpolator(sAccelerator);
            reverser.start();
            mDownAnim = null;
        }
    }

    public float getSkewX() {
        return mSkewX;
    }

    /**
     * Sets the amount of left/right skew on the button, which determines how far the
     * button leans
     */
    public void setSkewX(float value) {
        if (value != mSkewX) {
            mSkewX = value;
            invalidate();                   // Force the button to redraw with new skew value
            invalidateSkewedBounds();       // Also invalidate appropriate area of parent
        }
    }

    /**
     * Need to invalidate proper area of parent for skewed bounds
     */
    private void invalidateSkewedBounds() {
        if (mSkewX != 0) {
            Matrix matrix = new Matrix();
            matrix.setSkew(-mSkewX, 0);
            mTempRect.set(0, 0, getRight(), getBottom());
            matrix.mapRect(mTempRect);
            mTempRect.offset(getLeft() + getTranslationX(), getTop() + getTranslationY());
            ((View) getParent()).invalidate(
                    (int) mTempRect.left,
                    (int) mTempRect.top,
                    (int) (mTempRect.right + .5f),
                    (int) (mTempRect.bottom + .5f)
            );
        }
    }

    /**
     * Handles touch events directly since we want to react on down/up events, not
     * just button clicks
     */
    private OnTouchListener mTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (isPressed()) {
                        // OnClickListener will be triggered
                        performClick();
                        setPressed(false);
                        break;
                    }
                    // No click: Fall through; equivalent to cancel event
                case MotionEvent.ACTION_CANCEL:
                    // Run the cancel animation in either case
                    runCancelAnim();
                    break;

                case MotionEvent.ACTION_MOVE:
                    float x = event.getX();
                    float y = event.getY();
                    boolean isInside = (x > 0 && x < getWidth() &&
                            y > 0 && y < getHeight());
                    if (isPressed() != isInside) {
                        setPressed(isInside);
                    }
                    break;

                case MotionEvent.ACTION_DOWN:
                    setPressed(true);
                    runPressAnim();
                    break;

                default:
                    break;
            }

            // Returns true because custom touch listener has consumed the touch event
            return true;
        }
    };
}
