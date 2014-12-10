package com.desmond.androidanimation.CartoonAnimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import com.desmond.androidanimation.R;

/**
 * This application shows various cartoon animation techniques in the context of
 * a larger application, to show how such animations might be used to create a more
 * interactive, fun, and engaging experience.
 *
 * This main activity launches a sub-activity when the Play button is clicked. The
 * main action in this master activity is bouncing the Play button in, randomly
 * bouncing it while waiting for input, and animating its press and click behaviors
 * when the user interacts with it.
 *
 * Watch the associated video for this demo on the DevBytes channel of developer.android.com
 * or on the DevBytes playlist in the androiddevelopers channel on YouTube at
 * https://www.youtube.com/playlist?list=PLWz5rJ2EKKc_XOgcRukSoKKjewFJZrKV0.
 */
public class ToonGameActivity extends ActionBarActivity {

    Button mStarter;
    ViewGroup mContainer;
    private static final AccelerateInterpolator sAccelerator = new AccelerateInterpolator();
    private static final DecelerateInterpolator sDecelerator = new DecelerateInterpolator();
    private static final LinearInterpolator sLinearInterpolator = new LinearInterpolator();
    static long SHORT_DURATION = 100;
    static long MEDIUM_DURATION = 200;
    static long LONG_DURATION = 300;

    private static float sDurationScale = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toon_game);

        mStarter = (Button) findViewById(R.id.startBtn);
        mContainer = (ViewGroup) findViewById(R.id.container);
        mStarter.setOnTouchListener(funButtonListener);
        ViewCompat.animate(mStarter).setDuration(SHORT_DURATION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mContainer.setScaleX(1);
        mContainer.setScaleY(1);
        mContainer.setAlpha(1);
        mStarter.setVisibility(View.INVISIBLE);
        mContainer.getViewTreeObserver().addOnPreDrawListener(mOnPreDrawListener);
    }

    @Override
    protected void onPause() {
        mStarter.removeCallbacks(mSquishRunnable);
        super.onPause();
    }

    public void play(View view) {
        ViewCompat.animate(mContainer).scaleX(5).scaleY(5).alpha(0).setDuration(LONG_DURATION)
                .setInterpolator(sLinearInterpolator)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(ToonGameActivity.this, PlayerSetupActivity.class);
                        startActivity(intent);

                        // Override default window animation
                        overridePendingTransition(0, 0);
                    }
                });
        view.removeCallbacks(mSquishRunnable);
    }

    private void squishyBounce(final View view, final float startTY, final float bottomTY,
                               final float endTY, final float squash, final float stretch) {
        view.setPivotX(view.getWidth() / 2);
        view.setPivotY(view.getHeight());

        // Dropping down
        PropertyValuesHolder pvhTY = PropertyValuesHolder.ofFloat("translationY", startTY, bottomTY);
        PropertyValuesHolder pvhSX = PropertyValuesHolder.ofFloat("scaleX", .7f);
        PropertyValuesHolder pvhSY = PropertyValuesHolder.ofFloat("scaleY", 1.2f);
        ObjectAnimator downAnim = ObjectAnimator.ofPropertyValuesHolder(view, pvhTY, pvhSX, pvhSY);
        downAnim.setInterpolator(sAccelerator);

        // Bounce back up
        pvhTY = PropertyValuesHolder.ofFloat("translationY", bottomTY, endTY);
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 1);
        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 1);
        ObjectAnimator upAnim = ObjectAnimator.ofPropertyValuesHolder(view, pvhTY, pvhSX, pvhSY);
        upAnim.setInterpolator(sDecelerator);

        // Squash & Stretch & return to original shape
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", stretch);
        pvhSY = PropertyValuesHolder.ofFloat("scaleY", squash);
        ObjectAnimator stretchAnim = ObjectAnimator.ofPropertyValuesHolder(view, pvhSX, pvhSY);
        stretchAnim.setRepeatCount(1);
        stretchAnim.setRepeatMode(ValueAnimator.REVERSE);
        stretchAnim.setInterpolator(sDecelerator);

        // Play animations in sequence
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(downAnim, stretchAnim, upAnim);
        set.setDuration(SHORT_DURATION);
        set.start();
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Repeat the animation
                view.postDelayed(mSquishRunnable, (long) (500 + Math.random() * 2000));
            }
        });
    }

    private ViewTreeObserver.OnPreDrawListener mOnPreDrawListener =
            new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    mContainer.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Drop in the button from off the top of the screen
                            mStarter.setVisibility(View.VISIBLE);
                            mStarter.setY(-mStarter.getHeight());
                            squishyBounce(mStarter, -(mStarter.getTop() + mStarter.getHeight()),
                                    mContainer.getHeight() - mStarter.getTop() - mStarter.getHeight(),
                                    0, .5f, 1.5f);
                        }
                    }, 500);
                    return true;
                }
            };

    private View.OnTouchListener funButtonListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    ViewCompat.animate(mStarter).scaleX(.8f).scaleY(.8f).setInterpolator(sDecelerator);
                    mStarter.setTextColor(Color.CYAN);
                    mStarter.removeCallbacks(mSquishRunnable);
                    mStarter.setPressed(true);
                    break;

                case MotionEvent.ACTION_MOVE:
                    float x = event.getX();
                    float y = event.getY();
                    boolean isInside = (x > 0 && x < mStarter.getWidth() &&
                            y > 0 && y < mStarter.getHeight());
                    if (mStarter.isPressed() != isInside) {
                        mStarter.setPressed(isInside);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if (mStarter.isPressed()) {
                        mStarter.performClick();
                        mStarter.setPressed(false);
                    }
                    else {
                        ViewCompat.animate(mStarter).scaleX(1).scaleY(1).setInterpolator(sAccelerator);
                    }
                    mStarter.setTextColor(Color.BLUE);
                    break;
            }
            return true;
        }
    };

    private Runnable mSquishRunnable = new Runnable() {
        @Override
        public void run() {
            squishyBounce(
                    mStarter, 0,
                    mContainer.getHeight() - mStarter.getTop() - mStarter.getHeight(),
                    0, .5f, 1.5f);
        }
    };
}
