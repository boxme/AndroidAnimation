package com.desmond.androidanimation.FragmentAnimation;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.desmond.androidanimation.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends Fragment {

    public static final String TAG = ImageFragment.class.getSimpleName();

    View.OnClickListener mClickListener;

    public static ImageFragment newInstance() {
        return new ImageFragment();
    }

    public ImageFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        view.setOnClickListener(mClickListener);
        return view;
    }

    public void setClickListener(View.OnClickListener clickListener) {
        mClickListener = clickListener;
    }
}
