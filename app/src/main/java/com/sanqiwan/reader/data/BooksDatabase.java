package com.sanqiwan.reader.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: lenovo
 * Date: 13-7-31
 * Time: 上午10:44
 * To change this template use File | Settings | File Templates.
 */
public class BooksDatabase implements IDatabase, IAcceptCode {

    private BookDatabaseHelper mBookDatabaseHelper = BookDatabaseHelper.getInstance();
    private ReaderUris mReaderUris = ReaderUris.getInstance();
    private int mCode;

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        mCode = mReaderUris.match(uri);
        switch (mCode) {
            case ReaderUris.BOOKS_ITEM_CODE:
                selection = DatabaseUtils.concatenateWhere(selection, BookTable.BOOK_ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{Long.toString(ContentUris.parseId(uri))});
            case ReaderUris.BOOKS_CODE:
                Cursor cursor = mBookDatabaseHelper.getReadableDatabase().query(BookTable.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                return cursor;
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        int code = mReaderUris.match(uri);
        switch (code) {
            case ReaderUris.BOOKS_CODE:
                return "vnd.android.cursor.dir/vnd.com.sanqiwan.reader.books";
            case ReaderUris.BOOKS_ITEM_CODE:
                return "vnd.android.cursor.dir/vnd.com.sanqiwan.reader.books/#";
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (mReaderUris.match(uri) != ReaderUris.BOOKS_CODE) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        long rowId = mBookDatabaseHelper.getWritableDatabase().insert(BookTable.TABLE_NAME, "", values);
        if (rowId > 0) {
            return ContentUris.withAppendedId(uri, rowId);
        } else {
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        mCode = mReaderUris.match(uri);
        switch (mCode) {
            case ReaderUris.BOOKS_ITEM_CODE:
                selection = DatabaseUtils.concatenateWhere(selection, BookTable.BOOK_ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{Long.toString(ContentUris.parseId(uri))});
            case ReaderUris.BOOKS_CODE:
                int rowId = mBookDatabaseHelper.getWritableDatabase().delete(BookTable.TABLE_NAME, selection, selectionArgs);
                if (rowId > 0) {
                    return rowId;
                } else {
                    return 0;
                }
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        mCode = mReaderUris.match(uri);
        switch (mCode) {
            case ReaderUris.BOOKS_ITEM_CODE:
                selection = DatabaseUtils.concatenateWhere(selection, BookTable.BOOK_ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{Long.toString(ContentUris.parseId(uri))});
            case ReaderUris.BOOKS_CODE:
                int rowId = mBookDatabaseHelper.getWritableDatabase().update(BookTable.TABLE_NAME, values, selection, selectionArgs);
                if (rowId > 0) {
                    return rowId;
                } else {
                    return 0;
                }
        }
        return 0;
    }

    @Override
    public boolean acceptCode(int code) {
        if (code == ReaderUris.BOOKS_CODE || code == ReaderUris.BOOKS_ITEM_CODE) {
            return true;
        }
        return false;
    }
}
