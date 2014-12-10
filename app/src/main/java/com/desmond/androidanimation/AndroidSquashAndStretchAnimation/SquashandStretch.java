package com.desmond.androidanimation.AndroidSquashAndStretchAnimation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.desmond.androidanimation.R;

/**
 * This example shows how to add some life to a view during animation by deforming the shape.
 * As the button "falls", it stretches along the line of travel. When it hits the bottom, it
 * squashes, like a real object when hitting a surface. Then the button reverses these actions
 * to bounce back up to the start.
 *
 * Watch the associated video for this demo on the DevBytes channel of developer.android.com
 * or on the DevBytes playlist in the androiddevelopers channel on YouTube at
 * https://www.youtube.com/playlist?list=PLWz5rJ2EKKc_XOgcRukSoKKjewFJZrKV0.
 */
public class SquashAndStretch extends ActionBarActivity {

    private static final AccelerateInterpolator sAccelerator = new AccelerateInterpolator();
    private static final DecelerateInterpolator sDecelerator = new DecelerateInterpolator();

    ViewGroup mContainer = null;
    private static final long BASE_DURATION = 300;
    private long sAnimatorScale = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squash_and_stretch);

        mContainer = (ViewGroup) findViewById(R.id.container);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_squash_and_stretch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (item.getItemId() == R.id.menu_slow) {
            sAnimatorScale = item.isChecked() ? 1 : 5;
            item.setChecked(!item.isChecked());
        }

        return super.onOptionsItemSelected(item);
    }

    public void onButtonClick(View view) {
        long animationDuration = (BASE_DURATION * sAnimatorScale);

        // Scale around bottom/middle to simplify squash against the window button
        view.setPivotX(view.getWidth() / 2);
        view.setPivotY(view.getHeight());

        // Animate the button down, accelerating, while also stretching in Y and squashing in X
        PropertyValuesHolder pvhTY = PropertyValuesHolder.ofFloat("translationY",
                mContainer.getHeight() - view.getHeight());
        PropertyValuesHolder pvhSX = PropertyValuesHolder.ofFloat("scaleX", .7f);
        PropertyValuesHolder pvhSY = PropertyValuesHolder.ofFloat("scaleY", 1.2f);
        ObjectAnimator downAnim = ObjectAnimator.ofPropertyValuesHolder(view, pvhTY, pvhSX, pvhSY);
        downAnim.setInterpolator(sAccelerator);
        downAnim.setDuration(animationDuration * 2);

        // Stretch in X, squash in Y, then reverse
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 2f);
        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 0.5f);
        ObjectAnimator stretchAnim = ObjectAnimator.ofPropertyValuesHolder(view, pvhSX, pvhSY);
        stretchAnim.setRepeatCount(1);
        stretchAnim.setRepeatMode(ValueAnimator.REVERSE);
        stretchAnim.setInterpolator(sDecelerator);
        stretchAnim.setDuration(animationDuration);

        // Animate back to the start
        pvhTY = PropertyValuesHolder.ofFloat("translationY", 0);
        pvhSX = PropertyValuesHolder.ofFloat("scaleX", 1);
        pvhSY = PropertyValuesHolder.ofFloat("scaleY", 1);
        ObjectAnimator upAnim =
                ObjectAnimator.ofPropertyValuesHolder(view, pvhTY, pvhSX, pvhSY);
        upAnim.setDuration(animationDuration * 2);
        upAnim.setInterpolator(sDecelerator);

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(downAnim, stretchAnim, upAnim);
        set.start();
    }
}
