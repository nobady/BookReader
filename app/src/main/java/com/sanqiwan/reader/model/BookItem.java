package com.sanqiwan.reader.model;

/**
 * User: Sam
 * Date: 13-7-26
 * Time: 下午4:07
 */
public class BookItem {
    private long mBookId;
    private String mBookName;
    private boolean mIsFinish;
    private int mCommendNumber;
    private String mPicture;
    private String mAuthor;

    private boolean mDownload;
    private long mUpdateDate = 0;
    private long mCommends = 0;
    private String mCategory;
    private String mDescribe;
    private int mColumnName;

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
        this.mBookName = bookName;
    }

    public boolean isFinish() {
        return mIsFinish;
    }

    public void setIsFinish(boolean isFinish) {
        this.mIsFinish = isFinish;
    }

    public int getCommendNumber() {
        return mCommendNumber;
    }

    public void setCommendNumber(int commendNumber) {
        this.mCommendNumber = commendNumber;
    }

    public String getCover() {
        return mPicture;
    }

    public void setCover(String picture) {
        this.mPicture = picture;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        this.mAuthor = author;
    }

    public boolean getDownload() {
        return mDownload;
    }

    public void setDownload(boolean download) {
        this.mDownload = download;
    }

    public long getUpdateDate() {
        return mUpdateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.mUpdateDate = updateDate;
    }

    public long getCommends() {
        return mCommends;
    }

    public void setCommends(long commends) {
        this.mCommends = commends;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        this.mCategory = category;
    }

    public void setDescribe(String describe) {
        mDescribe = describe;
    }

    public String getDescribe() {
        return mDescribe;
    }

    public void setColumnName(int columnName) {
        mColumnName = columnName;
    }

    public int getColumnName() {
        return mColumnName;
    }

    public static BookItem toBookItem(BookDetail bookDetail) {
        BookItem bookItem = new BookItem();
        bookItem.setBookId(bookDetail.getBookId());
        bookItem.setBookName(bookDetail.getBookName());
        bookItem.setAuthor(bookDetail.getAuthorName());
        bookItem.setCover(bookDetail.getBookCover());
        bookItem.setIsFinish(bookDetail.getFinish());
        bookItem.setDescribe(bookDetail.getDescription());
        bookItem.setCommendNumber(bookDetail.getAllVisit());
        bookItem.setDownload(false);
        return bookItem;
    }
}
