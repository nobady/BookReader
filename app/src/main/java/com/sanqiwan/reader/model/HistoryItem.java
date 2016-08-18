package com.sanqiwan.reader.model;

/**
 * Created with IntelliJ IDEA.
 * User: lenovo
 * Date: 13-8-8
 * Time: 上午9:31
 * To change this template use File | Settings | File Templates.
 */
public class HistoryItem {
    private long mBookId;
    private String mBookName;
    private long mTime;
    private long mChapterId;
    private String mChapterName;
    private String mCover;

    public HistoryItem() {

    }
    public HistoryItem(long bookId) {
        mBookId = bookId;
    }
    public void setBookId(long bookId) {
        mBookId = bookId;
    }
    public long getBookId() {
        return mBookId;
    }
    public void setBookName(String bookName) {
        mBookName = bookName;
    }
    public String getBookName() {
        return mBookName;
    }
    public void setTime(long time) {
        mTime = time;
    }
    public long getTime() {
        return mTime;
    }
    public void setChapterId(long chapterId) {
        mChapterId = chapterId;
    }
    public long getChapterId() {
        return mChapterId;
    }
    public void setChapterName(String chapterName) {
        mChapterName = chapterName;
    }
    public String getChapterName() {
        return mChapterName;
    }

    public String getCover() {
        return mCover;
    }

    public void setCover(String cover) {
        mCover = cover;
    }
}
