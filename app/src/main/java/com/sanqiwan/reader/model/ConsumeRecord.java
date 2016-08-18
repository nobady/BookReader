package com.sanqiwan.reader.model;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 11/11/13
 * Time: 9:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConsumeRecord {
    private String mBookName;
    private String mChapter;
    private String mCover;
    private long mTime;
    private int mAmount;

    public String getBookName() {
        return mBookName;
    }

    public void setBookName(String bookName) {
        mBookName = bookName;
    }

    public String getChapter() {
        return mChapter;
    }

    public void setChapter(String chapter) {
        mChapter = chapter;
    }

    public String getCover() {
        return mCover;
    }

    public void setCover(String cover) {
        mCover = cover;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public int getAmount() {
        return mAmount;
    }

    public void setAmount(int amount) {
        mAmount = amount;
    }
}
