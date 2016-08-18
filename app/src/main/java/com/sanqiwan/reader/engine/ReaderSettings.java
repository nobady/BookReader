package com.sanqiwan.reader.engine;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import com.sanqiwan.reader.AppContext;

import java.util.Observable;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 12/2/13
 * Time: 10:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReaderSettings extends Observable {

    // TODO: 让ReaderSettings观察Settings的变化
    private static final Drawable DEFAULT_WALLPAPER = new ColorDrawable(0xffffffff);
    private static final int DEFAULT_TEXT_COLOR = 0xff000000;
    private static final int DEFAULT_SECONDARY_TEXT_COLOR = 0xff888888;
    private static final int DEFAULT_FONT_SIZE = 20;
    private static final int DEFAULT_SECONDARY_FONT_SIZE = 14;
    private static ReaderSettings sInstance;
    private final Context mContext;
    private Drawable mWallpaper = DEFAULT_WALLPAPER;
    private Drawable mPageShadow = DEFAULT_WALLPAPER;
    private float mFontSize = DEFAULT_FONT_SIZE;
    private float mSecondaryFontSize = DEFAULT_SECONDARY_FONT_SIZE;
    private int mTextColor = DEFAULT_TEXT_COLOR;
    private int mSecondaryTextColor = DEFAULT_TEXT_COLOR;

    private ReaderSettings(Context context) {
        mContext = context;
    }

    public static ReaderSettings getInstance() {
        if (sInstance == null) {
            synchronized (ReaderSettings.class) {
                if (sInstance == null) {
                    sInstance = new ReaderSettings(AppContext.getInstance());
                }
            }
        }
        return sInstance;
    }

    public Drawable getWallpaper() {
        return mWallpaper;
    }

    public void setWallpaper(Drawable drawable) {
        mWallpaper = drawable;
        notifyChanged();
    }

    private void notifyChanged() {
        setChanged();
        notifyObservers();
    }

    public float getFontSize() {
        return mFontSize;
    }

    public void setFontSize(float fontSize) {
        mFontSize = fontSize;
        notifyChanged();
    }

    public float getSecondaryFontSize() {
        return mSecondaryFontSize;
    }

    public void setSecondaryFontSize(float fontSize) {
        mSecondaryFontSize = fontSize;
        notifyChanged();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int color) {
        mTextColor = color;
        notifyChanged();
    }

    public int getSecondaryTextColor() {
        return mSecondaryTextColor;
    }

    public void setSecondaryTextColor(int color) {
        mSecondaryTextColor = color;
        notifyChanged();
    }

    public Drawable getPageShadow() {
        return mPageShadow;
    }

    public void setPageShadow(Drawable drawable) {
        mPageShadow = drawable;
    }
}
