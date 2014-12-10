package com.desmond.androidanimation.CardFlipAnimation;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.desmond.androidanimation.R;

/**
 * Created by desmond on 10/12/14.
 */
public class CardView extends ImageView {

    enum Corner {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    private final int CAMERA_DISTANCE = 8000;
    private final int MIN_FLIP_DURATION = 300;
    private final int VELOCITY_TO_DURATION_CONSTANT = 15;
    private final int MAX_FLIP_DURATION = 700;
    private final int ROTATION_PER_CARD = 2;
    private final int ROTATION_DELAY_PER_CARD = 50;
    private final int ROTATION_DURATION = 2000;
    private final int ANTIALIAS_BORDER = 1;

    private BitmapDrawable mFrontBitmapDrawable, mBackBitmapDrawable, mCurrentBitmapDrawable;

    private boolean mIsFrontShowing = true;
    private boolean mIsHorizontallyFlipped = false;

    private Matrix mHorizontalFlipMatrix;

    private CardFlipListener mCardFlipListener;

    public CardView(Context context) {
        super(context);
        init();
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Loads the bitmap drawables used for the front and back for this card
     */
    @TargetApi(12)
    public void init() {
        mHorizontalFlipMatrix = new Matrix();

        // Set the Z-axis
        setCameraDistance(CAMERA_DISTANCE);

        mFrontBitmapDrawable = bitmapWithBorder((BitmapDrawable) getResources()
                .getDrawable(R.drawable.red));
        mBackBitmapDrawable = bitmapWithBorder((BitmapDrawable) getResources()
                .getDrawable(R.drawable.blue));

        updateDrawableBitmap();
    }

    /**
     * Adding a 1 pixel transparent border around the bitmap can be used to
     * anti-alias the image as it rotates
     */
    private BitmapDrawable bitmapWithBorder(BitmapDrawable bitmapDrawable) {
        Bitmap bitmapWithBorder = Bitmap.createBitmap(bitmapDrawable.getIntrinsicWidth() +
                ANTIALIAS_BORDER * 2, bitmapDrawable.getIntrinsicHeight() + ANTIALIAS_BORDER * 2,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithBorder);
        canvas.drawBitmap(bitmapDrawable.getBitmap(), ANTIALIAS_BORDER, ANTIALIAS_BORDER, null);
        return new BitmapDrawable(getResources(), bitmapWithBorder);
    }

    /**
     * Initiates a horizontal flip from right to left
     */
    public void flipRightToLeft(int numberInPile, int velocity) {
        setPivotX(0);
        flipHorizontally(numberInPile, false, velocity);
    }

    /**
     * Initiates a horizontal flip from left to right
     */
    public void flipLeftToRight(int numerInPile, int velocity) {
        setPivotX(getWidth());
        flipHorizontally(numerInPile, true, velocity);
    }

    /**
     * Animates a horizontal (about the y-axis) flip of this card.
     * @param numberInPile Specifies how many cards are underneath this card in the new
     *                     pile so as to properly adjust its position offset in the stack.
     *
     * @param clockwise Specifies whether the horizontal animation is 180 degrees
     */
    public void flipHorizontally(int numberInPile, boolean clockwise, int velocity) {
        toggleFrontShowing();


    }

    /**
     * Darkens this ImageView's image by applying a shadow color filter over it
     */
    public void setShadow(float value) {
        int colorValue = (int) (255 - 200 * value);
        setColorFilter(Color.rgb(colorValue, colorValue, colorValue), PorterDuff.Mode.MULTIPLY);
    }

    public void toggleFrontShowing() {
        mIsFrontShowing = !mIsFrontShowing;
    }

    public void toggleIsHorizontallyFlipped() {
        mIsHorizontallyFlipped = !mIsHorizontallyFlipped;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHorizontalFlipMatrix.setScale(-1, 1, w/2, h/2);
    }

    /**
     * Scale the canvas horizontally about its midpoint in the case that the card
     * is in a horizontally flipped state
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (mIsHorizontallyFlipped) {
            canvas.concat(mHorizontalFlipMatrix);
        }
        super.onDraw(canvas);
    }

    /**
     * Updates the layout parameters of this view so as to reset the rotationX &
     * rotationY parameters, and remain independent of its previous position, while
     * also maintaining its current position in the layout
     */

    /**
     * Toggles the visible bitmap of this view between its front and back drawables respectively
     */
    public void updateDrawableBitmap() {
        mCurrentBitmapDrawable = mIsFrontShowing ? mFrontBitmapDrawable : mBackBitmapDrawable;
        setImageDrawable(mCurrentBitmapDrawable);
    }
}
