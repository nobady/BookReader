package com.sanqiwan.reader.format.xbook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.sanqiwan.reader.model.*;
import com.sanqiwan.reader.util.StringUtil;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 8/8/13
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class XBookReader {

    private File mBookFile;

    public XBookReader(File bookFile) {
        mBookFile = bookFile;
    }

    public XBook read() {
        File bookFile = mBookFile;
        if (!bookFile.exists() || bookFile.isFile())  {
            return null;
        }

        File[] children = bookFile.listFiles();
        if (children == null) {
            return null;
        }

        BookDetail bookDetail = readDetail();
        if (bookDetail == null) {
            return null;
        }
        XBook book = new XBook();
        book.setBookDetail(bookDetail);
        book.setTOC(readTOC());
        book.setBookContent(readContent());
        book.setBookStyle(readBookStyle());
        book.setBookResource(readBookResource());
        book.setBookId(bookDetail.getBookId());
        return book;
    }

    public BookDetail readDetail() {
        File file = new File(mBookFile, Constants.DETAIL);
        String content = StringUtil.loadStringFromFile(file);
        return BookDetail.fromJsonString(content);
    }

    public TOC readTOC() {
        File file = new File(mBookFile, Constants.TOC);
        String content = StringUtil.loadStringFromFile(file);
        return TOC.fromJsonString(content);
    }

    public BookContent readContent() {
        File file = new File(mBookFile, Constants.CONTENT);
        if (file.isFile()) {
            return null;
        }

        BookContent bookContent =  new BookContent();

        File[] children = file.listFiles();
        if (children == null) {
            return null;
        }

        for (File child : children) {
            Chapter chapter = readChapter(child);
            bookContent.addChapter(chapter);
            // TODO: use file name as a key
        }
        return bookContent;
    }

    private Chapter readChapter(File chapterFile) {
        String name = chapterFile.getName();
        String content = StringUtil.loadStringFromFile(chapterFile);
        Chapter chapter = Chapter.fromJsonString(content);
        return chapter;
    }

    public BookStyle readBookStyle() {
        return new BookStyle();
    }

    public BookResource readBookResource() {
        File resource = new File(mBookFile, Constants.RESOURCE);
        File cover = new File(resource, Constants.COVER);
        BookResource bookResource = new BookResource();
        Bitmap bitmap = BitmapFactory.decodeFile(cover.getPath());
        bookResource.setCover(bitmap);
        return bookResource;
    }
}
