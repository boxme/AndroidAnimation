package com.desmond.androidanimation.CartoonAnimation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * This custom TextView can be skewed to the left or right to enable anticipation and
 * follow-through effects
 */
public class SkewableTextView extends TextView {

    private float mSkewX;
    RectF mTempRect = new RectF();

    public SkewableTextView(Context context) {
        super(context);
    }

    public SkewableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SkewableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mSkewX != 0) {
            canvas.translate(0, getHeight());
            canvas.skew(mSkewX, 0);
            canvas.translate(0, -getHeight());
        }
        super.onDraw(canvas);
    }

    public float getSkewX() {
        return mSkewX;
    }

    public void setSkewX(float value) {
        if (value != mSkewX) {
            mSkewX = value;
            invalidate();       // Forces redraw with new skew value
            invalidateSkewedBound();
        }
    }

    /**
     * Need to invalidate the proper area of parent for skewed bounds
     */
    private void invalidateSkewedBound() {
        if (mSkewX != 0) {
            Matrix matrix = new Matrix();
            matrix.setSkew(-mSkewX, 0);
            mTempRect.set(0, 0, getRight(), getBottom());
            matrix.mapRect(mTempRect);
            mTempRect.offset(getLeft() + getTranslationX(), getTop() + getTranslationY());
            ((View) getParent()).invalidate((int) mTempRect.left, (int) mTempRect.top,
                    (int) (mTempRect.right + .5f), (int) (mTempRect.bottom + .5f));
        }
    }
}
