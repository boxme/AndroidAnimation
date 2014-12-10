package com.desmond.androidanimation.WindowAnimation;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.desmond.androidanimation.R;

public class AnimatedSubActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animated_sub);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
