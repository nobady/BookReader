package com.sanqiwan.reader.model;

import android.graphics.Bitmap;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 8/9/13
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class BookResource {
    private Bitmap mCover;
    public void setCover(Bitmap bitmap) {
        mCover = bitmap;
    }
    public Bitmap getCover() {
        return mCover;
    }
}
