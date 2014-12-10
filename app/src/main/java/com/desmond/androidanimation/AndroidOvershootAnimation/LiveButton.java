package com.desmond.androidanimation.AndroidOvershootAnimation;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;

import com.desmond.androidanimation.R;

/**
 * This app shows a simple application of anticipation and follow-through techniques as
 * the button animates into its pressed state and animates back out of it, overshooting
 * end state before resolving.
 *
 * Watch the associated video for this demo on the DevBytes channel of developer.android.com
 * or on the DevBytes playlist in the android developers channel on YouTube at
 * https://www.youtube.com/playlist?list=PLWz5rJ2EKKc_XOgcRukSoKKjewFJZrKV0.
 */
public class LiveButton extends ActionBarActivity {

    DecelerateInterpolator sDecelerator = new DecelerateInterpolator();
    OvershootInterpolator sOvershooter = new OvershootInterpolator(10f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_button);

        final Button clickMeButton = (Button) findViewById(R.id.clickMe);
        ViewCompat.animate(clickMeButton).setDuration(200);

        clickMeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN) {
                    ViewCompat.animate(v).setInterpolator(sDecelerator)
                            .scaleX(0.7f).scaleY(0.7f);
                }
                else if (action == MotionEvent.ACTION_UP) {
                    ViewCompat.animate(v).setInterpolator(sOvershooter)
                            .scaleX(1f).scaleY(1f);
                }

                // return True if the listener has consumed the event, false otherwise
                return false;
            }
        });
    }

}
