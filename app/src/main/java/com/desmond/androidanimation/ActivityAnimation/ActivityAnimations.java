package com.desmond.androidanimation.ActivityAnimation;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.desmond.androidanimation.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivityAnimations extends ActionBarActivity {

    private static final String PACKAGE = "com.desmond.androidanimation";
    static float sAnimatorScale = 1;

    GridLayout mGridLayout;
    HashMap<ImageView, PictureData> mPicturesData = new HashMap<ImageView, PictureData>();
    BitmapUtils mBitmapUtils = new BitmapUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_animations);

        // Greyscale filter used on all thumbnails
        ColorMatrix grayMatrix = new ColorMatrix();
        grayMatrix.setSaturation(0);
        ColorMatrixColorFilter grayscaleFilter = new ColorMatrixColorFilter(grayMatrix);

        mGridLayout = (GridLayout) findViewById(R.id.gridlayout);
        mGridLayout.setColumnCount(3);
        mGridLayout.setUseDefaultMargins(true);

        // Add all photo thumbnails to layout
        Resources resources = getResources();
        ArrayList<PictureData> pictures = mBitmapUtils.loadPhotos(resources);
        for (int i = 0; i < pictures.size(); ++i) {
            PictureData pictureData = pictures.get(i);
            BitmapDrawable thumbnailDrawable =
                    new BitmapDrawable(resources, pictureData.thumbnail);
            thumbnailDrawable.setColorFilter(grayscaleFilter);
            ImageView imageView = new ImageView(this);
            imageView.setOnClickListener(thumbnailClickListener);
            imageView.setImageDrawable(thumbnailDrawable);
            mPicturesData.put(imageView, pictureData);
            mGridLayout.addView(imageView);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_animations, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_slow) {
            sAnimatorScale = item.isChecked() ? 1 : 5;
            item.setChecked(!item.isChecked());
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * When the user clicks a thumbnail, bundle up information about it and launch the
     * details activity.
     */
    private View.OnClickListener thumbnailClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Interesting data to pass across are the thumbnail size/location, the
            // resourceID of the source bitmap, the picture description, and the
            // orientation (to avoid returning back to an obsolete configuration if
            // the device rotate again in the meantime)
            int[] screenLocation = new int[2];
            v.getLocationOnScreen(screenLocation);
            PictureData info = mPicturesData.get(v);
            int orientation = getResources().getConfiguration().orientation;

            Intent subActivity = new Intent(ActivityAnimations.this,
                    PictureDetailsActivity.class);
            subActivity.
                    putExtra(PACKAGE + ".orientation", orientation).
                    putExtra(PACKAGE + ".resourceId", info.resourceID).
                    putExtra(PACKAGE + ".left", screenLocation[0]).
                    putExtra(PACKAGE + ".top", screenLocation[1]).
                    putExtra(PACKAGE + ".width", v.getWidth()).
                    putExtra(PACKAGE + ".height", v.getHeight()).
                    putExtra(PACKAGE + ".description", info.description);
            startActivity(subActivity);

            // Override transitions: we don't want the normal window animation in addition
            // to our custom one
            overridePendingTransition(0, 0);
        }
    };
}
