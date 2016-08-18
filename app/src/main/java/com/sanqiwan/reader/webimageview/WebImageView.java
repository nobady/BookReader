package com.sanqiwan.reader.webimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 8/30/13
 * Time: 9:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class WebImageView extends ImageView {

    private static ImageLoader sImageLoader;
    private String mImageUrl;
    private Drawable mDefaultImage;
    private Bitmap mImageBitmap;
    private boolean mLoadImageOnSizeChanged;
    private IImageHandler mImageHandler;

    public WebImageView(Context context) {
        this(context, null);
    }

    public WebImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (sImageLoader == null) {
            sImageLoader = new ImageLoader(context);
        }
    }

    public void setImageHandler(IImageHandler imageHandler) {
        mImageHandler = imageHandler;
    }

    public void setImageUrl(String imageUrl) {
        if (TextUtils.equals(mImageUrl, imageUrl) && mImageBitmap != null) {
            setImageBitmap(mImageBitmap);
            return;
        }
        mImageUrl = imageUrl;
        resetImage();
        if (getWidth() > 0 && getHeight() > 0) {
            mLoadImageOnSizeChanged = false;
            sImageLoader.loadImage(mImageUrl, mCallback);
        } else {
            mLoadImageOnSizeChanged = true;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mLoadImageOnSizeChanged) {
            mLoadImageOnSizeChanged = false;
            sImageLoader.loadImage(mImageUrl, mCallback);
        }
    }

    private ImageLoader.Callback mCallback = new ImageLoader.Callback() {
        @Override
        public void onImageLoad(String url, Bitmap bitmap) {
            if (TextUtils.equals(mImageUrl, url)) {
                mImageBitmap = bitmap;
                post(new Runnable() {
                    @Override
                    public void run() {
                        setImageBitmap(mImageBitmap);
                        if (mImageHandler != null) {
                            mImageHandler.onHandleImage(WebImageView.this, mImageBitmap);
                        }
                    }
                });
            }
        }

        @Override
        public void onError(String url) {
        }

        @Override
        public int getViewWidth() {
            return getWidth();
        }

        @Override
        public int getViewHeight() {
            return getHeight();
        }
    };

    public void resetImage() {
        mImageBitmap = null;
        setImageDrawable(mDefaultImage);
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setDefaultImageDrawable(Drawable image) {
        mDefaultImage = image;
        setImageDrawable(mDefaultImage);
    }

    public void setDefaultImageResource(int resId) {
        setDefaultImageDrawable(getResources().getDrawable(resId));
    }

    public void setDefaultImageBitmap(Bitmap bitmap) {
        setDefaultImageDrawable(new BitmapDrawable(getResources(), bitmap));
    }

    public static void pauseLoading() {
        if (sImageLoader != null) {
            sImageLoader.pauseLoading();
        }
    }

    public static void resumeLoading() {
        if (sImageLoader != null) {
            sImageLoader.resumeLoading();
        }
    }
}
