package com.sanqiwan.reader.webimageview;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 8/30/13
 * Time: 3:56 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IFileCache {
    public Bitmap get(String key, int reqWidth, int reqHeight);

    public File getImageFile(String key);
}
