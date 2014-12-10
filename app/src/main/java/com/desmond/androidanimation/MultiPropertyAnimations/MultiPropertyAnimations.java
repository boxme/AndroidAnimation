package com.desmond.androidanimation.MultiPropertyAnimations;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.desmond.androidanimation.R;

public class MultiPropertyAnimations extends ActionBarActivity {

    private static final float TX_START = 0;
    private static final float TY_START = 0;
    private static final float TX_END = 400;
    private static final float TY_END = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_property_animations);
    }

    /**
     * A very manuel approach to animation uses a ValueAnimator to animate a fractional
     * value and then turns that value into the final property values which are then set
     * directly on the target object
     */
    public void runValueAnimator(final View view) {
        ValueAnimator anim = ValueAnimator.ofFloat(0, 400);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                float fraction = (Float) animator.getAnimatedValue();
                fraction /= 400;
                view.setTranslationX(TX_START + fraction * (TX_END - TX_START));
                view.setTranslationY(TY_START + fraction * (TY_END - TY_START));
            }
        });
        anim.start();
    }

    /**
     * ValuePropertyAnimator is the cleanest and most efficient way of animating View
     * properties, even when there are multiple properties to be animated in parallel.
     * This is the best way to do it.
     */
    public void runViewPropertyAnimator(View view) {
        ViewCompat.animate(view).translationX(TX_END).translationY(TY_END);
    }

    /**
     * Multiple ObjectAnimator objects can be created and run in parallel
     */
    public void runObjectAnimators(View view) {
        ObjectAnimator.ofFloat(view, "translationX", TX_END).start();
        ObjectAnimator.ofFloat(view, "translationY", TY_END).start();
        // Optional: use an AnimatorSet to run these in parallel
    }

    /**
     * Using PropertyValueHolder objects enables the use of a single ObjectAnimator
     * per target, even when there are multiple properties being animated on that target
     * Use this if you have custom property to be animated and ValuePropertyAnimator cannot
     * be used
     */
    public void runObjectAnimator(View view) {
        PropertyValuesHolder pvhTX = PropertyValuesHolder.ofFloat("translationX", TX_END);
        PropertyValuesHolder pvhTY = PropertyValuesHolder.ofFloat("translationY", TY_END);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhTX, pvhTY).start();
    }
}
