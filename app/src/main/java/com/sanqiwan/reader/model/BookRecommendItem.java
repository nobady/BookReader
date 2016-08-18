package com.sanqiwan.reader.model;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/23/13
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class BookRecommendItem {
    private int mBookId;
    private String mPictureUrl;
    private long mRefreshTime;
    private int mCategoryId;

    public void setBookId(int id) {
        mBookId = id;
    }

    public int getBookId() {
        return mBookId;
    }

    public String getPictureUrl() {
        return mPictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        mPictureUrl = pictureUrl;
    }

    public long getRefreshTime() {
        return mRefreshTime;
    }

    public void setRefreshTime(long refreshTime) {
        mRefreshTime = refreshTime;
    }

    public void setCategoryId(int categoryId) {
        mCategoryId = categoryId;
    }

    public int getCategoryId() {
        return mCategoryId;
    }

}
