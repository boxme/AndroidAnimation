package com.desmond.androidanimation.AndroidCurvedAnimation;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.desmond.androidanimation.R;

/**
 * This app shows how to move a view in a curved path between two endpoints.
 * The real work is done by PathEvaluator, which interpolates along a path
 * using Bezier control and anchor points in the path.
 *
 * Watch the associated video for this demo on the DevBytes channel of developer.android.com
 * or on the DevBytes playlist in the android developers channel on YouTube at
 * https://www.youtube.com/playlist?list=PLWz5rJ2EKKc_XOgcRukSoKKjewFJZrKV0.
 */
public class CurvedMotionActivity extends ActionBarActivity {

    private static final DecelerateInterpolator sDecelerateInterpolator =
            new DecelerateInterpolator();

    boolean mIsTopLeft = true;
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curved_motion);

        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Capture current location of button
                final int oldLeft = mButton.getLeft();
                final int oldTop = mButton.getTop();

                // Change layout parameters of button to move it
                moveButton();

                // Add OnPreDrawListener to catch button after layout but before drawing
                mButton.getViewTreeObserver().addOnPreDrawListener(
                        new ViewTreeObserver.OnPreDrawListener() {
                            @Override
                            public boolean onPreDraw() {
                                mButton.getViewTreeObserver().removeOnPreDrawListener(this);

                                // Capture new location
                                int left = mButton.getLeft();
                                int top = mButton.getTop();
                                int deltaX = left - oldLeft;
                                int deltaY = top - oldTop;

                                // Set up path to new location using a Bozier spline curve
                                AnimatorPath path = new AnimatorPath();

                                // Move back to the old position from the new position, it's the starting point
                                path.moveTo(-deltaX, -deltaY);

                                // Animate to the new location. which is (0,0). It means 0 deltaX
                                // & 0 deltaY from the final position
                                path.curveTo(-(deltaX/2), -deltaY, 0, -deltaY/2, 0, 0);
//                                path.lineTo(0, 0); // For moving in a straight line rather than curve

                                // Set up animation
                                final ObjectAnimator anim = ObjectAnimator.ofObject(
                                        CurvedMotionActivity.this,
                                        "buttonLoc",
                                        new PathEvaluator(),
                                        path.getPoints().toArray()
                                );
                                anim.setInterpolator(sDecelerateInterpolator);
                                anim.start();
                                return true;
                            }
                        }
                );
            }
        });
    }

    /**
     * Toggles button location on click between top-left and bottom-right
     */
    private void moveButton() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mButton.getLayoutParams();

        if (mIsTopLeft) {
            // addRule(rule, 0) == removeRule(rule)
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }
        else {
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        }

        mButton.setLayoutParams(params);
        mIsTopLeft = !mIsTopLeft;
    }

    /**
     * We need this setter to translate between the information the animator
     * produces (a new "PathPoint" describing the current animated location)
     * and the information that the button requires (an xy location). The
     * setter will be called by the ObjectAnimator given the 'buttonLoc'
     * property string.
     */
    public void setButtonLoc(PathPoint newLoc) {
        mButton.setTranslationX(newLoc.mX);
        mButton.setTranslationY(newLoc.mY);
    }
}
