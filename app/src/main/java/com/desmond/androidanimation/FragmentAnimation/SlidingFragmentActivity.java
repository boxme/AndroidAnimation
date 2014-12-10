package com.desmond.androidanimation.FragmentAnimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.desmond.androidanimation.R;

/**
 * This application shows a simple technique to animate and overlay two fragments
 * on top of each other in order to provide a more immersive experience,
 * as opposed to only having full screen transitions. When additional content
 * (text) related to the currently displayed content (image) is to be shown,
 * the currently visible content can be moved into the background instead of
 * being removed from the screen entirely. This effect can therefore
 * provide a more natural way of displaying additional information to the user
 * using a different fragment.
 *
 * In this specific demo, tapping on the screen toggles between the two
 * animated states of the fragment. When the animation is called,
 * the fragment with an image animates into the background while the fragment
 * containing text slides up on top of it. When the animation is toggled once
 * more, the text fragment slides back down and the image fragment regains
 * focus.
 */
public class SlidingFragmentActivity extends ActionBarActivity implements
        TextFragment.OnTextFragmentAnimationEndListener,
        FragmentManager.OnBackStackChangedListener {

    ImageFragment mImageFragment;
    TextFragment mTextFragment;
    View mDarkHoverView;

    boolean mDidSlideOut = false;
    boolean mIsAnimating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_fragment);

        mDarkHoverView = findViewById(R.id.dark_hover_view);
        mDarkHoverView.setAlpha(0);

        if (savedInstanceState == null) {
            mImageFragment = ImageFragment.newInstance();
            mTextFragment = TextFragment.newInstance();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.move_to_back_container, mImageFragment, ImageFragment.TAG)
                    .commit();
        }
        else {
            mImageFragment = (ImageFragment) getSupportFragmentManager().findFragmentByTag(ImageFragment.TAG);

            mTextFragment = (TextFragment) getSupportFragmentManager().findFragmentByTag(TextFragment.TAG);
            if (mTextFragment == null) {
                mTextFragment = TextFragment.newInstance();
            }
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        mImageFragment.setClickListener(mClickListener);
        mTextFragment.setClickListener(mClickListener);
        mTextFragment.setOnTextFragmentAnimationEnd(this);
        mDarkHoverView.setOnClickListener(mClickListener);
    }

    /**
     * This method is used to toggle between the two fragment states by calling
     * the appropriate animations between them. The entry and exit animations of the text
     * fragment are specified in R.animator resource files. The entry and exit animations
     * of the image fragment are specified in the slideBack and slideForward methods below.
     * The reason for separating the animation logic this way is because the translucent
     * dark hover view must fade in at the same time as the image fragment animates into the
     * background, which would be difficult to time properly given that the setCustomAnimations
     * method can only modify the two fragments in the transaction.
     */
    private void switchFragments() {
        if (mIsAnimating) {
            return;
        }

        mIsAnimating = true;
        if (mDidSlideOut) {
            mDidSlideOut = false;
            getSupportFragmentManager().popBackStack();
        }
        else {
            // Sliding in
            mDidSlideOut = true;

            Animator.AnimatorListener listener = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                    /*
                    setCustomAnimations() affects all fragment transitions added to the transaction
                    after it is called.  So you need to call setCustomAnimations() before you want
                    it used, and you can actually setup multiple different custom animations for
                    each part of a transaction (with a call to setCustomAnimations() before
                    each add()/remove()/attach()/detach()/show()/hide()/replace()).
                     */
                    transaction.setCustomAnimations(
                            R.anim.slide_fragment_in, 0, 0, R.anim.slide_fragment_out
                    );
                    transaction.add(R.id.move_to_back_container, mTextFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            };

            slideBack(listener);
        }
    }

    @Override
    public void onBackStackChanged() {
        if (!mDidSlideOut) {
            slideForward(null);
        }
    }

    @Override
    public void onAnimationEnd() {
        mIsAnimating = false;
    }

    /**
     * This method animates the image fragment into the background by both scaling
     * and rotating the fragment's view, as well as adding a translucent dark hover view
     * to inform the user that it is inactive
     */
    public void slideBack(Animator.AnimatorListener listener) {
        View moveFragmentView = mImageFragment.getView();

        PropertyValuesHolder rotateX = PropertyValuesHolder.ofFloat("rotationX", 40f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 0.8f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0.8f);
        ObjectAnimator movingFragmentAnimator =
                ObjectAnimator.ofPropertyValuesHolder(moveFragmentView, rotateX, scaleX, scaleY);

        ObjectAnimator darkHoverViewAnimator =
                ObjectAnimator.ofFloat(mDarkHoverView, "alpha", 0.0f, 0.5f);

        ObjectAnimator movingFragmentRotator =
                ObjectAnimator.ofFloat(moveFragmentView, "rotationX", 0);

        // A delay is required so that this rotation will not cancel the previous rotation without
        // showing
        movingFragmentRotator.setStartDelay(getResources()
                .getInteger(R.integer.half_slide_up_down_duration));

        AnimatorSet set = new AnimatorSet();
        set.playTogether(movingFragmentAnimator, darkHoverViewAnimator, movingFragmentRotator);
        set.addListener(listener);
        set.start();
    }

    /**
     * This method animates the image fragment into the foreground by both scaling & rotating
     * the fragment's view, while also removing the previously added translucent dark hover view.
     * Upon the completion of this animation, the image fragment regains focus since this method
     * is called from the onBackStackChanged method
     */
    public void slideForward(Animator.AnimatorListener listener) {
        View movingFragmentView = mImageFragment.getView();

        PropertyValuesHolder rotateX =  PropertyValuesHolder.ofFloat("rotationX", 40f);
        PropertyValuesHolder scaleX =  PropertyValuesHolder.ofFloat("scaleX", 1.0f);
        PropertyValuesHolder scaleY =  PropertyValuesHolder.ofFloat("scaleY", 1.0f);
        ObjectAnimator movingFragmentAnimator = ObjectAnimator.
                ofPropertyValuesHolder(movingFragmentView, rotateX, scaleX, scaleY);

        ObjectAnimator darkHoverViewAnimator = ObjectAnimator.
                ofFloat(mDarkHoverView, "alpha", 0.5f, 0.0f);

        ObjectAnimator movingFragmentRotator = ObjectAnimator.
                ofFloat(movingFragmentView, "rotationX", 0);
        movingFragmentRotator.setStartDelay(
                getResources().getInteger(R.integer.half_slide_up_down_duration));

        AnimatorSet s = new AnimatorSet();
        s.playTogether(movingFragmentAnimator, movingFragmentRotator, darkHoverViewAnimator);

        // A delay to wait for the TextFragment to slide out first before animating
        s.setStartDelay(getResources().getInteger(R.integer.slide_up_down_duration));
        s.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimating = false;
            }
        });
        s.start();
    }

    View.OnClickListener mClickListener = new View.OnClickListener () {
        @Override
        public void onClick(View view) {
            switchFragments();
        }
    };
}
