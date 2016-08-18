package com.sanqiwan.reader.model;

/**
 * Created with IntelliJ IDEA.
 * User: jwb
 * Date: 13-11-8
 * Time: 下午2:23
 * To change this template use File | Settings | File Templates.
 */
public class VipVolumeItem {

    private long mChapterId;
    private String mName;
    private int mPrice;

    public long getChapterId() {
        return mChapterId;
    }

    public void setChapterId(long chapterId) {
        mChapterId = chapterId;
    }

    public String getChapterName() {
        return mName;
    }

    public void setChapterName(String chapterName) {
        mName = chapterName;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int price) {
        mPrice = price;
    }
}
