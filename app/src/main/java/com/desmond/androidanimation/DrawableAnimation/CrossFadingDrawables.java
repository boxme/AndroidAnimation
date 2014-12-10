package com.desmond.androidanimation.DrawableAnimation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;

import com.desmond.androidanimation.R;

/**
 * This example shows how to use TransitionDrawable to perform a simple cross-fade effect
 * between two drawables.
 */
public class CrossFadingDrawables extends ActionBarActivity {

    int mCurrentDrawable = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cross_fading_drawables);

        final ImageView imageView = (ImageView) findViewById(R.id.imageview);

        // Create red and green bitmaps to cross-fade between
        Bitmap bitmap0 = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        Bitmap bitmap1 = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap0);
        canvas.drawColor(Color.RED);
        canvas = new Canvas(bitmap1);
        canvas.drawColor(Color.GREEN);
        BitmapDrawable drawables[] = new BitmapDrawable[2];
        drawables[0] = new BitmapDrawable(getResources(), bitmap0); // Red
        drawables[1] = new BitmapDrawable(getResources(), bitmap1); // Green

        // Add the red/green bitmap drawables to a TransitionDrawable. They are layered
        // in the transition drawable. The cross-fade effect happens by fading one out and the
        // other in.
        final TransitionDrawable crossfader = new TransitionDrawable(drawables);
        imageView.setImageDrawable(crossfader);


        // Clicking on the drawable will cause the cross-fade effect to run. Depending on
        // which drawable is currently being shown, we either 'start' or 'reverse' the
        // transition, which determines which drawable is faded out/in during the transition.
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentDrawable == 0) {
                    crossfader.startTransition(500);
                    mCurrentDrawable = 1;
                }
                else {
                    // Not startTransition because startTransition will always fade out the
                    // top drawable in the drawable array and fade in the bottom drawable
                    // So remember to use reverseTransition()
                    crossfader.reverseTransition(500);
                    mCurrentDrawable = 0;
                }
            }
        });
    }
}
