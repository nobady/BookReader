package com.sanqiwan.reader.webimageview.impl;

import android.content.Context;
import android.graphics.Bitmap;
import com.sanqiwan.reader.preference.Settings;
import com.sanqiwan.reader.util.BitmapUtil;
import com.sanqiwan.reader.util.SecurityUtil;
import com.sanqiwan.reader.webimageview.IFileCache;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 8/30/13
 * Time: 2:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageFileCache implements IFileCache {

    private Context mContext;

    public ImageFileCache(Context context) {
        mContext = context;
    }

    @Override
    public Bitmap get(String key, int reqWidth, int reqHeight) {
        File f = getImageFile(key);
        if (f.exists()) {
            return BitmapUtil.decodeSampledBitmapFromResource(f.getAbsolutePath(), reqWidth, reqHeight);
        }
        return null;
    }

    @Override
    public File getImageFile(String imageUrl) {
        File cacheDir = getCacheDir();
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return new File(cacheDir, SecurityUtil.md5(imageUrl));
    }

    private File getCacheDir() {
        return Settings.getInstance().getImageCacheDir();
    }
}
