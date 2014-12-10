package com.desmond.androidanimation.CardFlipAnimation;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
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
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Loads the bitmap drawables used for the front and back for this card
     */
    @TargetApi(12)
    public void init(Context context) {
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
     * Toggles the visible bitmap of this view between its front and back drawables respectively
     */
    public void updateDrawableBitmap() {
        mCurrentBitmapDrawable = mIsFrontShowing ? mFrontBitmapDrawable : mBackBitmapDrawable;
        setImageDrawable(mCurrentBitmapDrawable);
    }
}
