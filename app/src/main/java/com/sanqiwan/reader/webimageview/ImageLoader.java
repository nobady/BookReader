package com.sanqiwan.reader.webimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import com.sanqiwan.reader.util.BitmapUtil;
import com.sanqiwan.reader.util.UIUtil;
import com.sanqiwan.reader.webimageview.impl.ImageFileCache;
import com.sanqiwan.reader.webimageview.impl.ImageMemoryCache;
import com.sanqiwan.reader.webimageview.impl.ImageNetworkClient;

import java.io.File;

public class ImageLoader {

    public interface Callback {
        public void onImageLoad(String url, Bitmap bitmap);

        public void onError(String url);

        public int getViewWidth();

        public int getViewHeight();
    }

    private IMemoryCache mImageMemoryCache;
    private IFileCache mImageFileCache;
    private INetworkClient mImageNetworkClient;

    private Object lock = new Object();
    private boolean mAllowedLoading = true;

    public ImageLoader(Context context) {
        mImageMemoryCache = new ImageMemoryCache(context);
        mImageFileCache = new ImageFileCache(context);
        mImageNetworkClient = new ImageNetworkClient();
    }

    public void loadImage(String imageUrl, Callback callback) {
        Bitmap bitmapFromCache = mImageMemoryCache.get(imageUrl);
        if (bitmapFromCache == null) {
            ImageLoadTask task = new ImageLoadTask(imageUrl, callback);
            ThreadPoolManager.getInstance().addTask(task);
        } else {
            callback.onImageLoad(imageUrl, bitmapFromCache);
        }
    }

    private static final class Result {
        private Result(String url, Bitmap bitmap) {
            this.bitmap = bitmap;
            this.url = url;
        }

        public String url;
        public Bitmap bitmap;
    }

    private final class ImageLoadTask implements Runnable {
        private String mImageUrl;
        private Callback mCallback;

        public ImageLoadTask(String imageUrl, Callback callback) {
            mImageUrl = imageUrl;
            mCallback = callback;
        }

        @Override
        public void run() {
            try {
                while (!mAllowedLoading) {
                    synchronized (lock) {
                        lock.wait();
                        Log.i("pauseLoading", "locked");
                    }
                }

                int viewWidth = 0;
                int viewHeight = 0;
                if (mCallback != null) {
                    viewWidth = mCallback.getViewWidth();
                    viewHeight = mCallback.getViewHeight();
                }

                Bitmap bitmap = mImageFileCache.get(mImageUrl, viewWidth, viewHeight);
                if (bitmap == null) {
                    File file = mImageFileCache.getImageFile(mImageUrl);
                    mImageNetworkClient.loadBitmap(mImageUrl, file);
                    bitmap = BitmapUtil.decodeSampledBitmapFromResource(file.getAbsolutePath(), viewWidth, viewHeight);
                }
                if (bitmap != null) {
                    mImageMemoryCache.put(mImageUrl, bitmap);
                    notifyImageLoaded(bitmap);
                } else {
                    notifyError();
                }
            } catch (Exception e) {
                notifyError();
            }
        }

        private void notifyImageLoaded(final Bitmap bitmap) {
            UIUtil.getHandler().post(new Runnable() {

                @Override
                public void run() {
                    mCallback.onImageLoad(mImageUrl, bitmap);
                }
            });
        }

        private void notifyError() {
            UIUtil.getHandler().post(new Runnable() {

                @Override
                public void run() {
                    mCallback.onError(mImageUrl);
                }
            });
        }
    }

    public void pauseLoading() {
        mAllowedLoading = false;
    }

    public void resumeLoading() {
        mAllowedLoading = true;
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
