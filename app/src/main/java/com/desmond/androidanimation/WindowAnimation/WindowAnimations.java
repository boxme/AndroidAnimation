package com.desmond.androidanimation.WindowAnimation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.desmond.androidanimation.R;

/**
 * Shows how to create custom Window animation to animate between different
 * sub-activities
 */
public class WindowAnimations extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_animations);

        final Button defaultButton = (Button) findViewById(R.id.defaultButton);
        final Button translateButton = (Button) findViewById(R.id.translateButton);
        final Button scaleButton = (Button) findViewById(R.id.scaleButton);
        final ImageView thumbnail = (ImageView) findViewById(R.id.thumbnail);

        // By default, launching a sub-activity uses the system default for window animation
        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent subActivity = new Intent(WindowAnimations.this, SubActivity.class);
                startActivity(subActivity);
            }
        });

        // Custom animations allow us to do things like slide the next activity in as we
        // slide this activity out
        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Using the AnimatedSubActivity also allows us to animate exiting that
                // activity - see that activity for details
                Intent subActivity = new Intent(WindowAnimations.this, AnimatedSubActivity.class);

                // The enter/exit animations for the two activities are specified by xml resources
                Bundle translateBundle =
                        ActivityOptionsCompat.makeCustomAnimation(
                                WindowAnimations.this, R.anim.slide_in_left, R.anim.slide_out_left
                        ).toBundle();

                startActivity(subActivity, translateBundle);
            }
        });

        // Starting in JellyBean, you can provide an animation that scales up the new activity
        // from a given source rectangle
        scaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent subActivity = new Intent(WindowAnimations.this, AnimatedSubActivity.class);
                Bundle scaleBundle = ActivityOptionsCompat.makeScaleUpAnimation(
                        v, 0, 0, v.getWidth(), v.getHeight()
                ).toBundle();
                startActivity(subActivity, scaleBundle);
            }
        });

        // Starting in JellyBean, you can also provide an animation that scales up the new activity
        // from a given bitmap, cross-fading between the starting and ending representations.
        // Here, we scale up from a thumbnail image of the final sub-activity
        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable drawable = (BitmapDrawable) thumbnail.getDrawable();
                Bitmap bm = drawable.getBitmap();

                Intent subActivity = new Intent(WindowAnimations.this, AnimatedSubActivity.class);

                Bundle scaleBundle = ActivityOptionsCompat.makeThumbnailScaleUpAnimation(
                        thumbnail, bm, 0, 0
                ).toBundle();

                startActivity(subActivity, scaleBundle);
            }
        });
    }
}
