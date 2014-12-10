package com.desmond.androidanimation.FragmentAnimation;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.desmond.androidanimation.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TextFragment extends Fragment {

    public static final String TAG = TextFragment.class.getSimpleName();

    View.OnClickListener mClickListener;
    OnTextFragmentAnimationEndListener mListener;

    /**
     * This interface is used to inform the main activity when the entry
     * animation of the text fragment has completed in order to avoid the
     * start of a new animation before the current one has completed.
     */
    public interface OnTextFragmentAnimationEndListener {
        public void onAnimationEnd();
    }

    public static TextFragment newInstance() {
        return new TextFragment();
    }

    public TextFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_text, container, false);
        view.setOnClickListener(mClickListener);
        return view;
    }

    public void setClickListener(View.OnClickListener clickListener) {
        mClickListener = clickListener;
    }

    public void setOnTextFragmentAnimationEnd(OnTextFragmentAnimationEndListener listener) {
        mListener = listener;
    }

    /**
     * Called when a fragment loads an animation
     */
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        int id = enter ? R.anim.slide_fragment_in : R.anim.slide_fragment_out;
        // nextAnim == id so we can ignore the above
        final Animation anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);

        if (enter) {
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    mListener.onAnimationEnd();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        }

        return anim;
    }

//    @Override
//    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
//        int id = enter ? R.animator.slide_fragment_in : R.animator.slide_fragment_out;
//        final Animator anim = AnimatorInflater.loadAnimator(getActivity(), id);
//        if (enter) {
//            anim.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mListener.onAnimationEnd();
//                }
//            });
//        }
//        return anim;
//    }
}
