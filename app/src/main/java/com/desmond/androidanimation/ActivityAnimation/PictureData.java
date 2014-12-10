package com.desmond.androidanimation.ActivityAnimation;

import android.graphics.Bitmap;

/**
 * Created by desmond on 26/10/14.
 */
public class PictureData {
    int resourceID;
    String description;
    Bitmap thumbnail;

    public PictureData(int resourceId, String description, Bitmap thumbnail) {
        this.resourceID = resourceId;
        this.description = description;
        this.thumbnail = thumbnail;
    }
}
