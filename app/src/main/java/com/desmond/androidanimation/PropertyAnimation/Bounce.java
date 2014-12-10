package com.desmond.androidanimation.PropertyAnimation;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.desmond.androidanimation.R;

public class Bounce extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bounce);
    }

    static class MyView extends View {

        Bitmap mBitmap;
        Paint paint = new Paint();
        int mShapeX, mShapeY;
        int mShapeW, mShapeH;

        public MyView(Context context) {
            super(context);
            setupShape();
        }

        public MyView(Context context, AttributeSet attrs) {
            super(context, attrs);
            setupShape();
        }

        public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            setupShape();
        }

        private void setupShape() {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.electricsheep);
            mShapeW = mBitmap.getWidth();
            mShapeH = mBitmap.getHeight();

            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startAnimation();
                }
            });
        }

        public void setShapeX(int shapeX) {
            int minX = mShapeX;
            // Taking account of the width of the sheep
            int maxX = mShapeX + mShapeW;

            mShapeX = shapeX;
            minX = Math.min(mShapeX, minX);
            maxX = Math.max(mShapeX + mShapeW, maxX);

            // Only invalidate the area of the sheep, instead of the entire view
            // This can save work on redrawing
            invalidate(minX, mShapeY, maxX, mShapeY + mShapeH);
        }

        public void setShapeY(int shapeY) {
            int minY = mShapeY;
            int maxY = mShapeY + mShapeH;

            mShapeY = shapeY;
            minY = Math.min(mShapeY, minY);
            maxY = Math.max(mShapeY + mShapeH, maxY);

            invalidate(mShapeX, minY, mShapeX + mShapeW, maxY);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mShapeX = (w - mBitmap.getWidth()) / 2;
            mShapeY = 0;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(mBitmap, mShapeX, mShapeY, paint);
        }

        void startAnimation() {
            // This variation has the shape bouncing, due to the use of an
            // AccelerateInterpolator, which speeds up as the animation proceed.
            // Note that when animation reverses, the interpolator acts in reverse,
            // decelerating movement
//            ValueAnimator anim = getValueAnimator();
//            anim.setRepeatMode(ValueAnimator.REVERSE);
//            anim.setRepeatCount(ValueAnimator.INFINITE);
//            anim.setInterpolator(new AccelerateInterpolator());
//            anim.start();

            // This variation uses an ObjectAnimator. The functionality is exactly the same as
            // above, but this time the boilerplate code is greatly reduced because we
            // tell ObjectAnimator to automatically animate the target object for us, so we no
            // longer need to listen for frame updates and do that work ourselves.
            ObjectAnimator animator = getObjectAnimator();
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.start();
        }

        @TargetApi(12)
        ValueAnimator getValueAnimator() {
            ValueAnimator anim = ValueAnimator.ofFloat(0, 1);

            // This listener is called at every frame of the animation
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    setShapeY((int) (animation.getAnimatedFraction() * (getHeight() - mShapeH)));
                }
            });

            return anim;
        }

        ObjectAnimator getObjectAnimator() {
            return ObjectAnimator.ofInt(this, "shapeY", (getHeight() - mShapeH));
        }
    }
}
