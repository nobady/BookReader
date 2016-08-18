package com.sanqiwan.reader.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: lenovo
 * Date: 13-8-8
 * Time: 上午9:29
 * To change this template use File | Settings | File Templates.
 */
public class HistoryDatabase implements IDatabase, IAcceptCode{
    private BookDatabaseHelper mBookDatabaseHelper = BookDatabaseHelper.getInstance();
    private ReaderUris mReaderUris = ReaderUris.getInstance();
    private int mCode;

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        mCode = mReaderUris.match(uri);
        switch (mCode) {
            case ReaderUris.HISTORY_ITEM_CODE:
                selection = DatabaseUtils.concatenateWhere(selection, HistoryTable.BOOK_ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{Long.toString(ContentUris.parseId(uri))});
            case ReaderUris.HISTORY_CODE:
                Cursor cursor = mBookDatabaseHelper.getReadableDatabase().query(HistoryTable.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                return cursor;
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        mCode = mReaderUris.match(uri);
        switch (mCode) {
            case ReaderUris.HISTORY_CODE:
                return "vnd.android.cursor.dir/vnd.com.sanqiwan.reader.read_history";
            case ReaderUris.HISTORY_ITEM_CODE:
                return "vnd.android.cursor.dir/vnd.com.sanqiwan.reader.read_history/#";
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowId = mBookDatabaseHelper.getWritableDatabase().insert(HistoryTable.TABLE_NAME, "", values);
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
            case ReaderUris.HISTORY_ITEM_CODE:
                selection = DatabaseUtils.concatenateWhere(selection, HistoryTable.BOOK_ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{Long.toString(ContentUris.parseId(uri))});
            case ReaderUris.HISTORY_CODE:
                int rowId = mBookDatabaseHelper.getWritableDatabase().delete(HistoryTable.TABLE_NAME, selection, selectionArgs);
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
            case ReaderUris.HISTORY_ITEM_CODE:
                selection = DatabaseUtils.concatenateWhere(selection, HistoryTable.BOOK_ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{Long.toString(ContentUris.parseId(uri))});
            case ReaderUris.HISTORY_CODE:
                int rowId = mBookDatabaseHelper.getWritableDatabase().update(HistoryTable.TABLE_NAME, values, selection, selectionArgs);
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
        if (code == ReaderUris.HISTORY_CODE || code == ReaderUris.HISTORY_ITEM_CODE) {
            return true;
        }
        return false;
    }
}
