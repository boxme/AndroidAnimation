package com.desmond.androidanimation.ActivityAnimation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.desmond.androidanimation.R;

/**
 * This sub-activity shows a zoomed-in view of a specific photo, along with the
 * picture's text description. Most of the logic is for the animations that will
 * be run when the activity is being launched and exited. When launching,
 * the large version of the picture will resize from the thumbnail version in the
 * main activity, colorizing it from the thumbnail's grayscale version at the
 * same time. Meanwhile, the black background of the activity will fade in and
 * the description will eventually slide into place. The exit animation runs all
 * of this in reverse.
 */
public class PictureDetailsActivity extends ActionBarActivity {

    private static final String PACKAGE = "com.desmond.androidanimation";
    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
    private static final int ANIM_DURATION = 500;

    private BitmapDrawable mBitmapDrawable;
    private ColorMatrix mColorizerMatrix = new ColorMatrix();
    int mLeftDelta;
    int mTopDelta;
    float mWidthScale;
    float mHeightScale;
    private ImageView mImageView;
    private TextView mTextView;
    private FrameLayout mTopLevelLayout;
    private ShadowLayout mShadowLayout;
    private int mOriginalOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_picture_details);

        mImageView = (ImageView) findViewById(R.id.imageView);
        mTopLevelLayout = (FrameLayout) findViewById(R.id.topLevelLayout);
        mShadowLayout = (ShadowLayout) findViewById(R.id.shadowLayout);
        mTextView = (TextView) findViewById(R.id.description);

        // Retrieve the data we need for the picture/description to display and
        // the thumbnail to animate it from
        Bundle bundle = getIntent().getExtras();
        Bitmap bitmap = BitmapUtils.getBitmap(getResources(),
                bundle.getInt(PACKAGE + ".resourceId"));
        String description = bundle.getString(PACKAGE + ".description");
        final int thumbnailTop = bundle.getInt(PACKAGE + ".top");
        final int thumbnailLeft = bundle.getInt(PACKAGE + ".left");
        final int thumbnailWidth = bundle.getInt(PACKAGE + ".width");
        final int thumbnailHeight = bundle.getInt(PACKAGE + ".height");
        mOriginalOrientation = bundle.getInt(PACKAGE + ".orientation");

        mBitmapDrawable = new BitmapDrawable(getResources(), bitmap);
        mImageView.setImageDrawable(mBitmapDrawable);
        mTextView.setText(description);

        mTopLevelLayout.setBackgroundColor(Color.BLACK);

        // Only run the animation if we're coming from the parent activity, not if
        // we're recreated automatically by the window manager
        if (savedInstanceState == null) {
            ViewTreeObserver observer = mImageView.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    mImageView.getViewTreeObserver().removeOnPreDrawListener(this);

                    // Figure out where the thumbnail and full size versions are, relative
                    // to the screen and each other
                    int[] screenLocation = new int[2];
                    mImageView.getLocationOnScreen(screenLocation);
                    mLeftDelta = thumbnailLeft - screenLocation[0];
                    mTopDelta = thumbnailTop - screenLocation[1];

                    // Scale factors to make the large version the same size as the thumbnail
                    mWidthScale = (float) thumbnailWidth / mImageView.getWidth();
                    mHeightScale = (float) thumbnailHeight / mImageView.getHeight();

                    runEnterAnimation();

                    return true;
                }
            });
        }
    }

    /**
     * The enter animation scales the picture in from its previous thumbnail
     * size/location, colorizing it in parallel. In parallel, the background of the
     * activity is fading in. When the picture is in place, the text description
     * drops down.
     */
    @TargetApi(12)
    public void runEnterAnimation() {
        final long duration = (long) (ANIM_DURATION * ActivityAnimations.sAnimatorScale);

        // Set starting values for properties we're going to animate. These values
        // scale and position the full size version down to the thumbnail size/location,
        // from which we'll animate it up
        mImageView.setPivotX(0);
        mImageView.setPivotY(0);
        mImageView.setScaleX(mWidthScale);
        mImageView.setScaleY(mHeightScale);
        mImageView.setTranslationX(mLeftDelta);
        mImageView.setTranslationY(mTopDelta);

        // We'll fade the text in later
        mTextView.setAlpha(0);

        // Animate scale and translation to go from thumbnail to full size
        mImageView.animate().setDuration(duration)
                .scaleX(1).scaleY(1)
                .translationX(0).translationY(0)
                .setInterpolator(sDecelerator)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // Animate the description in after image animation
                        // is done. Slide and fade the text in from underneath
                        // the picture
                        mTextView.setTranslationY(-mTextView.getHeight());
                        mTextView.animate().setDuration(duration / 2)
                                .translationY(0)
                                .alpha(1)
                                .setInterpolator(sDecelerator);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {}

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });


        // Fade in the black background
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mTopLevelLayout.getBackground(), "alpha", 0, 255);
        bgAnim.setDuration(duration);
        bgAnim.start();

        // Animate a color filter to take the image from grayscale to full color.
        // This happens in parallel with the image scaling and moving into place.
        ObjectAnimator colorizer = ObjectAnimator.ofFloat(PictureDetailsActivity.this,
                "saturation", 0, 1);
        colorizer.setDuration(duration);
        colorizer.start();

        // Animate a drop-shadow of the image
        ObjectAnimator shadowAnim = ObjectAnimator.ofFloat(mShadowLayout, "shadowDepth", 0, 1);
        shadowAnim.setDuration(duration);
        shadowAnim.start();
    }

    /**
     * The exit animation is basically a reverse of the enter animation, except that if
     * the orientation has changed we simply scale the picture back into the center of
     * the screen.
     *
     * @param endAction This action gets run after the animation completes (this is
     * when we actually switch activities)
     */
    @TargetApi(12)
    public void runExitAnimation(final Runnable endAction) {
        final long duration = (long) (ANIM_DURATION * ActivityAnimations.sAnimatorScale);

        // No need to set initial values for the reverse animation; the image is at the
        // starting size/location that we want to start from. Just animate to the
        // thumbnail size/location that we retrieved earlier

        // Caveat: configuration change invalidates thumbnail positions; just animate
        // the scale around the center. Also, fade it out since it won't match up with
        // whatever is actually in the center
        final boolean fadeOut;
        if (getResources().getConfiguration().orientation != mOriginalOrientation) {
            mImageView.setPivotX(mImageView.getWidth() / 2);
            mImageView.setPivotY(mImageView.getHeight() / 2);
            mLeftDelta = 0;
            mTopDelta = 0;
            fadeOut = true;
        }
        else {
            fadeOut = false;
        }

        // First, slide/fade text out of the way
        mTextView.animate().translationY(-mTextView.getHeight()).alpha(0)
                .setDuration(duration/2).setInterpolator(sAccelerator)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mImageView.animate().setDuration(duration)
                                .scaleX(mWidthScale).scaleY(mHeightScale)
                                .translationX(mLeftDelta).translationY(mTopDelta)
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        mImageView.post(endAction);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {
                                    }
                                });

                        if (fadeOut) {
                            mImageView.animate().alpha(0);
                        }

                        // Fade out background
                        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mTopLevelLayout.getBackground(),
                                "alpha", 0);
                        bgAnim.setDuration(duration);
                        bgAnim.start();

                        // Animate the shadow of the image
                        ObjectAnimator shadowAnim = ObjectAnimator.ofFloat(mShadowLayout,
                                "shadowDepth", 1, 0);
                        shadowAnim.setDuration(duration);
                        shadowAnim.start();

                        // Animate a color filter to take the image back to grayscale,
                        // in parallel with the image scaling and moving into place.
                        ObjectAnimator colorizer =
                                ObjectAnimator.ofFloat(PictureDetailsActivity.this,
                                        "saturation", 1, 0);
                        colorizer.setDuration(duration);
                        colorizer.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {}

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });
    }

    /**
     * This is called by the colorizing animator. It sets a saturation factor that is then
     * passed onto a filter on the picture's drawable.
     * @param value
     */
    public void setSaturation(float value) {
        mColorizerMatrix.setSaturation(value);
        ColorMatrixColorFilter colorizerFilter = new ColorMatrixColorFilter(mColorizerMatrix);
        mBitmapDrawable.setColorFilter(colorizerFilter);
    }

    /**
     * Overriding this method allows us to run our exit animation first, then exiting
     * the activity when it is complete.
     */
    @Override
    public void onBackPressed() {
        runExitAnimation(new Runnable() {
            public void run() {
                // *Now* go ahead and exit the activity
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        // override transitions to skip the standard window animations
        overridePendingTransition(0, 0);
    }
}
