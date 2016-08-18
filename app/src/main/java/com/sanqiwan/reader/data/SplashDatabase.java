package com.sanqiwan.reader.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/23/13
 * Time: 6:52 PM
 * To change this template use File | Settings | File Templates.
 */
class SplashDatabase implements IDatabase, IAcceptCode {
    private BookDatabaseHelper mBookDatabaseHelper = BookDatabaseHelper.getInstance();
    private ReaderUris mReaderUris = ReaderUris.getInstance();
    private int mCode;

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        mCode = mReaderUris.match(uri);
        switch (mCode) {
            case ReaderUris.SPLASH_ITEM_CODE:
                selection = DatabaseUtils.concatenateWhere(selection, SplashTable.SPLASH_ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{Long.toString(ContentUris.parseId(uri))});
            case ReaderUris.SPLASH_CODE:
                Cursor cursor = mBookDatabaseHelper.getReadableDatabase().query(SplashTable.SPLASH_TABLE,
                        projection, selection, selectionArgs, null, null, sortOrder);
                return cursor;
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        mCode = mReaderUris.match(uri);
        switch (mCode) {
            case ReaderUris.SPLASH_CODE:
                return "vnd.android.cursor.dir/vnd.com.sanqiwan.reader.splash";
            case ReaderUris.SPLASH_ITEM_CODE:
                return "vnd.android.cursor.dir/vnd.com.sanqiwan.reader.splash/#";
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowId = mBookDatabaseHelper.getWritableDatabase().insert(SplashTable.SPLASH_TABLE, "", values);
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
            case ReaderUris.SPLASH_ITEM_CODE:
                selection = DatabaseUtils.concatenateWhere(selection, SplashTable.SPLASH_ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{Long.toString(ContentUris.parseId(uri))});
            case ReaderUris.SPLASH_CODE:
                int rowId = mBookDatabaseHelper.getWritableDatabase().delete(SplashTable.SPLASH_TABLE, selection, selectionArgs);
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
            case ReaderUris.SPLASH_ITEM_CODE:
                selection = DatabaseUtils.concatenateWhere(selection, SplashTable.SPLASH_ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{Long.toString(ContentUris.parseId(uri))});
            case ReaderUris.SPLASH_CODE:
                int rowId = mBookDatabaseHelper.getWritableDatabase().update(SplashTable.SPLASH_TABLE, values, selection, selectionArgs);
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
        if (code == ReaderUris.SPLASH_CODE || code == ReaderUris.SPLASH_ITEM_CODE) {
            return true;
        }
        return false;
    }
}
