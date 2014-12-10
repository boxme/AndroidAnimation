package com.desmond.androidanimation.DrawableAnimation;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.desmond.androidanimation.R;

public class DrawableAnimation extends ActionBarActivity {

    AnimationDrawable mTutorialAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawable_animation);

        ImageView tutorialImageView = (ImageView) findViewById(R.id.tutorial);
        tutorialImageView.setBackgroundResource(R.drawable.tutorial_list);
        mTutorialAnimation = (AnimationDrawable) tutorialImageView.getBackground();
    }

    /**
     * If you want the drawable animation to start when it's visible to the user
     * @param hasFocus
     */
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            mTutorialAnimation.start();
//        }
//    }

    /**
     * If you want the drawable animation to start after the screen is touched
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mTutorialAnimation.start();
            return  true;
        }
        return super.onTouchEvent(event);
    }
}
