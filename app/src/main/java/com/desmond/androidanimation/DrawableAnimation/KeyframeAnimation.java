package com.desmond.androidanimation.DrawableAnimation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;

import com.desmond.androidanimation.R;

/**
 * Shows how to use AnimationDrawable to construct a keyframe animation where each
 * frame is shown for a specified duration
 */
public class KeyframeAnimation extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyframe_animation);

        ImageView imageView = (ImageView) findViewById(R.id.imageview);

        // Create the AnimationDrawable in which we will store all frames of the animation
        final AnimationDrawable animationDrawable = new AnimationDrawable();
        for (int i = 0; i < 10; ++i) {
            animationDrawable.addFrame(getDrawableForFrameNumber(i), 300);
        }
        // Run until we say stop
        animationDrawable.setOneShot(false);

        imageView.setImageDrawable(animationDrawable);

        // When the user clicks on the image, toggle the animation on/off
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (animationDrawable.isRunning()) {
                    animationDrawable.stop();
                }
                else {
                    animationDrawable.start();
                }
            }
        });
    }

    /**
     * The 'frames' in this app are nothing more than a gray background with text indicating
     * the number of the frame.
     */
    private BitmapDrawable getDrawableForFrameNumber(int frameNumber) {
        Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.GRAY);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(80);
        paint.setColor(Color.BLACK);
        canvas.drawText("Frame " + frameNumber, 40, 200, paint);
        return new BitmapDrawable(getResources(), bitmap);
    }
}
