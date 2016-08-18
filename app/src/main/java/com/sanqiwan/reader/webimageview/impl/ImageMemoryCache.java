package com.sanqiwan.reader.webimageview.impl;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import com.sanqiwan.reader.webimageview.IMemoryCache;
/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 8/30/13
 * Time: 2:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageMemoryCache implements IMemoryCache {

    private LruCache<String, Bitmap> mMemoryCache = null;

    public ImageMemoryCache(Context context) {
        createCache(context);
    }

    @Override
    public Bitmap get(String key) {
        return mMemoryCache.get(key);
    }

    @Override
    public void put(String key, Bitmap bitmap) {
        mMemoryCache.put(key, bitmap);
    }

    private void createCache(Context context) {
        int availMemory = getAvailMemory(context);
        mMemoryCache = new LruCache<String, Bitmap>(availMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    private int getAvailMemory(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(memoryInfo);
        int availMem = (int) memoryInfo.availMem / 8;
        return availMem;
    }
}
