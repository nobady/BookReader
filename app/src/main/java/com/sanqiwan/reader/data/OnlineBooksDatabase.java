package com.sanqiwan.reader.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/23/13
 * Time: 6:51 PM
 * To change this template use File | Settings | File Templates.
 */
class OnlineBooksDatabase implements IDatabase, IAcceptCode {
    private BookDatabaseHelper mBookDatabaseHelper = BookDatabaseHelper.getInstance();
    private ReaderUris mReaderUris = ReaderUris.getInstance();
    private int mCode;

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        mCode = mReaderUris.match(uri);
        switch (mCode) {
            case ReaderUris.ONLINE_BOOKS_ITEM_CODE:
                selection = DatabaseUtils.concatenateWhere(selection, OnlineBookTable.COL_BOOK_ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{Long.toString(ContentUris.parseId(uri))});
            case ReaderUris.ONLINE_BOOKS_CODE:
                Cursor cursor = mBookDatabaseHelper.getReadableDatabase().query(OnlineBookTable.ONLINE_BOOKS_TABLE,
                        projection, selection, selectionArgs, null, null, sortOrder);
                return cursor;
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        mCode = mReaderUris.match(uri);
        switch (mCode) {
            case ReaderUris.ONLINE_BOOKS_CODE:
                return "vnd.android.cursor.dir/vnd.com.sanqiwan.reader.online_books";
            case ReaderUris.ONLINE_BOOKS_ITEM_CODE:
                return "vnd.android.cursor.dir/vnd.com.sanqiwan.reader.online_books/#";
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (mReaderUris.match(uri) != ReaderUris.ONLINE_BOOKS_CODE) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        long rowId = mBookDatabaseHelper.getWritableDatabase().insert(OnlineBookTable.ONLINE_BOOKS_TABLE, "", values);
        // If the insert succeeded, the row ID exists.
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
            case ReaderUris.ONLINE_BOOKS_ITEM_CODE:
                selection = DatabaseUtils.concatenateWhere(selection, OnlineBookTable.COL_BOOK_ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{Long.toString(ContentUris.parseId(uri))});
            case ReaderUris.ONLINE_BOOKS_CODE:
                int rowId = mBookDatabaseHelper.getWritableDatabase().delete(OnlineBookTable.ONLINE_BOOKS_TABLE, selection, selectionArgs);
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
            case ReaderUris.ONLINE_BOOKS_ITEM_CODE:
                selection = DatabaseUtils.concatenateWhere(selection, OnlineBookTable.COL_BOOK_ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{Long.toString(ContentUris.parseId(uri))});
            case ReaderUris.ONLINE_BOOKS_CODE:
                int rowId = mBookDatabaseHelper.getWritableDatabase().update(OnlineBookTable.ONLINE_BOOKS_TABLE, values, selection, selectionArgs);
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
        if (code == ReaderUris.ONLINE_BOOKS_CODE || code == ReaderUris.ONLINE_BOOKS_ITEM_CODE) {
            return true;
        }
        return false;
    }
}
