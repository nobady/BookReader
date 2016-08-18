package com.sanqiwan.reader.webimageview;

import android.graphics.Bitmap;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 8/30/13
 * Time: 3:51 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IMemoryCache {

    public Bitmap get(String key);

    public void put(String key, Bitmap bitmap);
}
