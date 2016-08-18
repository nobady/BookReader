package com.sanqiwan.reader.model;

import com.sanqiwan.reader.format.xbook.XBookReader;

import java.io.File;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/29/13
 * Time: 4:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class XBook {

    private long mBookId;
    private BookDetail mBookDetail;
    private TOC mTOC;
    private BookContent mBookContent;
    private BookStyle mBookStyle;
    private BookResource mBookResource;

    public XBook() {
    }

    public XBook(long bookId, BookDetail bookDetail, TOC toc, BookContent bookContent, BookStyle bookStyle, BookResource bookResource) {
        mBookId = bookId;
        mBookDetail = bookDetail;
        mTOC = toc;
        mBookContent = bookContent;
        mBookStyle = bookStyle;
        mBookResource = bookResource;
    }

    public long getBookId() {
        return mBookId;
    }

    public void setBookId(long id) {
        mBookId = id;
    }

    public void setBookDetail(BookDetail bookDetail) {
        mBookDetail = bookDetail;
    }

    public BookDetail getBookDetail() {
        return mBookDetail;
    }

    public TOC getTOC() {
        return mTOC;
    }

    public void setTOC(TOC toc) {
        mTOC = toc;
    }

    public BookContent getBookContent() {
        return mBookContent;
    }

    public void setBookContent(BookContent bookContent) {
        mBookContent = bookContent;
    }

    public BookStyle getBookStyle() {
        return mBookStyle;
    }

    public void setBookStyle(BookStyle bookStyle) {
        mBookStyle = bookStyle;
    }

    public BookResource getBookResource() {
        return mBookResource;
    }

    public void setBookResource(BookResource bookResource) {
        mBookResource = bookResource;
    }

    public Collection<Chapter> getAllChapters() {
        return mBookContent.getAllChapters();
    }

    public Chapter getChapterById(long chapterId) {
        return mBookContent.getChapterById(chapterId);
    }

    public Chapter getChapterByIndex(int index) {
        return mBookContent.getChapterById(mTOC.getItem(index).getId());
    }

    public int getIndexById(long chapterId) {
        return mTOC.getItemIndexById(chapterId);
    }

    public VolumeItem getTocItemByIndex(int index) {
        return mTOC.getItem(index);
    }

    public void addChapter(Chapter chapter) {
        mBookContent.addChapter(chapter);
    }

    public static XBook createBook(long id) {
        XBook xBook = new XBook();
        xBook.setBookId(id);
        xBook.setBookContent(new BookContent());
        return xBook;
    }

    public static XBook createBookFromFile(File bookFile) {
        return new XBookReader(bookFile).read();
    }

    public static XBook createBookFromFile(String bookPath) {
        return createBookFromFile(new File(bookPath));
    }
}
