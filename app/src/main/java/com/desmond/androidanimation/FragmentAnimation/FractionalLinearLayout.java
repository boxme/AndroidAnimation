package com.desmond.androidanimation.FragmentAnimation;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * In order to animate the fragment containing text on/off the screen,
 * it is required that we know the height of the device being used. However,
 * this can only be determined at runtime, so we cannot specify the required
 * translation in an xml file. Since FragmentTransaction's setCustomAnimations
 * method requires an ID of an animation defined via an xml file, this linear
 * layout was built as a workaround. This custom linear layout is created to specify
 * the location of the fragment's layout as a fraction of the device's height. By
 * animating yFraction from 0 to 1, we can animate the fragment from the top of
 * the screen to the bottom of the screen, regardless of the device's specific size.
 */
public class FractionalLinearLayout extends LinearLayout {

    private float mYFraction;
    private int mScreenHeight;

    public FractionalLinearLayout(Context context) {
        super(context);
    }

    public FractionalLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FractionalLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * This is called during layout when the size of this view has changed. If
     * you were just added to the view hierarchy, you're called with the old
     * values of 0.
     *
     * @param w Current width of this view.
     * @param h Current height of this view.
     * @param oldw Old width of this view.
     * @param oldh Old height of this view.
     */
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        mScreenHeight = h;
//        setY(mScreenHeight);
//    }
//
//    public float getYFraction() {
//        return mYFraction;
//    }
//
//    public void setYFraction(float yFraction) {
//        mYFraction = yFraction;
//        setY((mScreenHeight > 0) ? (mScreenHeight - mYFraction * mScreenHeight) : 0);
//    }
}
