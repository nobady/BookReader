package com.sanqiwan.reader.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.sanqiwan.reader.AppContext;
import com.sanqiwan.reader.format.xbook.XBookReader;
import com.sanqiwan.reader.format.xbook.XBookWriter;
import com.sanqiwan.reader.model.*;
import com.sanqiwan.reader.preference.Settings;
import com.sanqiwan.reader.webservice.BookWebService;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 8/14/13
 * Time: 8:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class XBookManager {

    private static final String[] PROJECTION = new String[] {
            BookTable._ID,
            BookTable.BOOK_ID,
            BookTable.NAME,
            BookTable.COVER,
            BookTable.AUTHOR_NAME,
            BookTable.CREATE_TIME,
            BookTable.DESCRIPTION,
            BookTable.SIZE,
            BookTable.PATH,
            BookTable.FINISH,
            BookTable.HAS_UPDATE
    };

    public static final int ID_INDEX = 0;
    public static final int BOOK_ID_INDEX = 1;
    public static final int NAME_INDEX = 2;
    public static final int COVER_INDEX = 3;
    public static final int AUTHOR_NAME_INDEX = 4;
    public static final int CREATE_TIME_INDEX = 5;
    public static final int DESCRIPTION_INDEX = 6;
    public static final int SIZE_INDEX = 7;
    public static final int PATH_INDEX = 8;
    public static final int FINISH_INDEX = 9;
    public static final int HAS_UPDATE_INDEX = 10;

    private Context mContext;
    private ContentResolver mContentResolver;

    public XBookManager() {
        mContext = AppContext.getInstance();
        mContentResolver = mContext.getContentResolver();
    }

    public void addBook(XBook book) {

        File bookFile = Settings.getInstance().getBookDownloadFile(book.getBookId());
        new XBookWriter().write(book, bookFile);
        BookDetail bookDetail = book.getBookDetail();
        ContentValues values = new ContentValues();
        values.put(BookTable.BOOK_ID, book.getBookId());
        values.put(BookTable.NAME, bookDetail.getBookName());
        values.put(BookTable.COVER, bookDetail.getBookCover());
        values.put(BookTable.AUTHOR_NAME, bookDetail.getAuthorName());
        values.put(BookTable.CREATE_TIME, bookDetail.getCreateTime());
        values.put(BookTable.DESCRIPTION, bookDetail.getDescription());
        values.put(BookTable.SIZE, bookDetail.getBookSize());
        values.put(BookTable.PATH, bookFile.getPath());
        values.put(BookTable.FINISH, bookDetail.getFinish());
        mContentResolver.insert(ReaderUris.BOOKS_URI, values);
    }

    public void deleteBookWithoutFile(long bookId) {
        mContentResolver.delete(ContentUris.withAppendedId(ReaderUris.BOOKS_URI, bookId), null, null);
    }

    public void deleteBookWithFile(long bookId) {
        Uri uri = ContentUris.withAppendedId(ReaderUris.BOOKS_URI, bookId);
        Cursor cursor = mContentResolver.query(uri, new String[]{BookTable.PATH}, null, null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    new File(cursor.getString(0)).delete();
                    mContentResolver.delete(uri, null, null);
                }
            } finally {
                cursor.close();
            }
        }
    }

    public void deleteAllBooks() {
        mContentResolver.delete(ReaderUris.BOOKS_URI, null, null);
    }

    public Cursor getAllBooks() {

        return mContentResolver.query(ReaderUris.BOOKS_URI, PROJECTION, null, null, BookTable._ID+" desc");
    }

    public Cursor getBook(long bookId, String[] projection) {
        Uri uri = ContentUris.withAppendedId(ReaderUris.BOOKS_URI, bookId);
        return mContentResolver.query(uri, projection, null, null, null);
    }

    public int getBookCount() {
        Cursor cursor = mContentResolver.query(ReaderUris.BOOKS_URI, new String[]{BookTable.BOOK_ID}, null, null, null);
        if (cursor != null) {
            try {
                return cursor.getCount();
            } finally {
                cursor.close();
            }
        }

        return 0;
    }

    public long getLastChapterId(long bookId) {
        XBookReader reader = new XBookReader(Settings.getInstance().getBookDownloadFile(bookId));
        TOC toc = reader.readTOC();
        if (toc != null) {
            VolumeItem item = toc.getLastItem();
            if (item != null) {
                return item.getId();
            }
        }
        return -1;
    }

    public boolean hasBook(long bookId) {
        String[] projection = new String[]{BookTable.BOOK_ID};
        Uri uri = ContentUris.withAppendedId(ReaderUris.BOOKS_URI, bookId);
        Cursor cursor = mContentResolver.query(uri, projection, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.getCount() > 0) {
                    return true;
                }
            } finally {
                cursor.close();
            }
        }
        return false;
    }

    public String getBookPath(long bookId) {
        String[] projection = new String[]{BookTable.PATH};
        Uri uri = ContentUris.withAppendedId(ReaderUris.BOOKS_URI, bookId);
        Cursor cursor = mContentResolver.query(uri, projection, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursor.getString(0);
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    public String getBookCover(long bookId) {
        String[] projection = new String[]{BookTable.COVER};
        Uri uri = ContentUris.withAppendedId(ReaderUris.BOOKS_URI, bookId);
        Cursor cursor = mContentResolver.query(uri, projection, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursor.getString(0);
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    public String getBookName(long bookId) {
        String[] projection = new String[]{BookTable.NAME};
        Uri uri = ContentUris.withAppendedId(ReaderUris.BOOKS_URI, bookId);
        Cursor cursor = mContentResolver.query(uri, projection, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursor.getString(0);
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    public void setBookHasUpdate(long bookId, boolean hasUpdate) {
        Uri uri = ContentUris.withAppendedId(ReaderUris.BOOKS_URI, bookId);
        ContentValues values = new ContentValues();
        int intHasUpdate = hasUpdate ? 1 : 0;
        values.put(BookTable.HAS_UPDATE, intHasUpdate);
        mContentResolver.update(uri, values, null, null);
    }

    public void checkBooksUpdate() {
        Cursor cursor = getAllBooks();
        BookWebService bookWebService = new BookWebService();
        if (cursor == null) {
            return;
        }
        while (cursor.moveToNext()) {
            long bookId = cursor.getInt(BOOK_ID_INDEX);
            long chapterId = getLastChapterId(bookId);
            BooksUpdateChapter booksUpdateChapter = null;
            try {
                booksUpdateChapter = bookWebService.getBooksUpdateChapterId(bookId, chapterId);
            } catch (ZLNetworkException e) {
            }
            if(booksUpdateChapter != null && booksUpdateChapter.getItems() != null && booksUpdateChapter.getItems().size() > 0) {
                setBookHasUpdate(bookId, true);
            }
        }
        cursor.close();
    }
}
