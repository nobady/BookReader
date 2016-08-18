package com.sanqiwan.reader.model;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/25/13
 * Time: 2:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchItem {
    private long mBookId;
    private String mBookName;

    public long getBookId() {
        return mBookId;
    }

    public void setBookId(long bookId) {
        mBookId = bookId;
    }

    public String getBookName() {
        return mBookName;
    }

    public void setBookName(String bookName) {
        mBookName = bookName;
    }

}
