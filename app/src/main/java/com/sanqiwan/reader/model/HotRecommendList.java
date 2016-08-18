package com.sanqiwan.reader.model;

import java.util.List;

/**
 * Created by jwb on 14-8-29.
 */
public class HotRecommendList {

    private List<BookItem> mHotBookItems;
    private List<BookItem> mStrongBookItems;
    private List<BookItem> mSellingBookItems;

    public void setHotBookItems(List<BookItem> bookItems) {
        mHotBookItems = bookItems;
    }

    public List<BookItem> getHotBookItems() {
        return mHotBookItems;
    }

    public void setStrongBookItems(List<BookItem> bookItems) {
        mStrongBookItems = bookItems;
    }

    public List<BookItem> getStrongBookItems() {
        return mStrongBookItems;
    }

    public void setSellingBookItems(List<BookItem> bookItems) {
        mSellingBookItems = bookItems;
    }

    public List<BookItem> getSellingBookItems() {
        return mSellingBookItems;
    }

}
