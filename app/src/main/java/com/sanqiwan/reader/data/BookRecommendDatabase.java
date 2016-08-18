package com.sanqiwan.reader.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * User: Sam
 * Date: 13-9-23
 * Time: 上午10:54
 */
public class BookRecommendDatabase implements IDatabase, IAcceptCode {

    private BookDatabaseHelper mBookDatabaseHelper = BookDatabaseHelper.getInstance();
    private ReaderUris mReaderUris = ReaderUris.getInstance();
    private int mCode;

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        mCode = mReaderUris.match(uri);
        switch (mCode) {
            case ReaderUris.BOOKRECOMMEND_ITEM_CODE:
                selection = DatabaseUtils.concatenateWhere(selection, BookRecommendTable.BOOK_ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{Long.toString(ContentUris.parseId(uri))});
            case ReaderUris.BOOKRECOMMEND_CODE:
                Cursor cursor = mBookDatabaseHelper.getReadableDatabase().query(BookRecommendTable.BOOKRECOMMEND_TABLE,
                        projection, selection, selectionArgs, null, null, sortOrder);
                return cursor;
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        int code = mReaderUris.match(uri);
        switch (code) {
            case ReaderUris.BOOKRECOMMEND_CODE:
                return "vnd.android.cursor.dir/vnd.com.sanqiwan.reader.book_recommends";
            case ReaderUris.BOOKRECOMMEND_ITEM_CODE:
                return "vnd.android.cursor.dir/vnd.com.sanqiwan.reader.book_recommends/#";
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (mReaderUris.match(uri) != ReaderUris.BOOKRECOMMEND_CODE) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        long rowId = mBookDatabaseHelper.getWritableDatabase().insert(BookRecommendTable.BOOKRECOMMEND_TABLE, "", values);
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
            case ReaderUris.BOOKRECOMMEND_ITEM_CODE:
                selection = DatabaseUtils.concatenateWhere(selection, BookRecommendTable.BOOK_ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{Long.toString(ContentUris.parseId(uri))});
            case ReaderUris.BOOKRECOMMEND_CODE:
                int rowId = mBookDatabaseHelper.getWritableDatabase().delete(BookRecommendTable.BOOKRECOMMEND_TABLE, selection, selectionArgs);
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
            case ReaderUris.BOOKRECOMMEND_ITEM_CODE:
                selection = DatabaseUtils.concatenateWhere(selection, BookRecommendTable.BOOK_ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{Long.toString(ContentUris.parseId(uri))});
            case ReaderUris.BOOKRECOMMEND_CODE:
                int rowId = mBookDatabaseHelper.getWritableDatabase().update(BookRecommendTable.BOOKRECOMMEND_TABLE, values, selection, selectionArgs);
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
        if (code == ReaderUris.BOOKRECOMMEND_ITEM_CODE || code == ReaderUris.BOOKRECOMMEND_CODE) {
            return true;
        }
        return false;
    }
}
