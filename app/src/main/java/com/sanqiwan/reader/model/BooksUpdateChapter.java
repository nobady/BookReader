package com.sanqiwan.reader.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lenovo
 * Date: 13-8-6
 * Time: 下午4:14
 * To change this template use File | Settings | File Templates.
 */
public class BooksUpdateChapter {
    private int mCode;
    private List<VolumeItem> mItems;

    public BooksUpdateChapter() {
        mItems = new ArrayList<VolumeItem>();
    }

    public void setCode(int code) {
        mCode = code;
    }

    public int getCode() {
        return mCode;
    }

    public void setItems(List<VolumeItem> items) {
        mItems = items;
    }

    public List<VolumeItem> getItems() {
        return new ArrayList<VolumeItem>(mItems);
    }

    public boolean isEmpty() {
        return mItems == null || mItems.isEmpty();
    }
}
