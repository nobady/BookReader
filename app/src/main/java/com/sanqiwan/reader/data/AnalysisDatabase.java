package com.sanqiwan.reader.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;


class AnalysisDatabase implements IDatabase, IAcceptCode {
    private static final String TYPE_CODE = "vnd.android.cursor.dir/vnd.com.sanqiwan.reader.analysis";
    private static final String TYPE_ITEM_CODE = "vnd.android.cursor.dir/vnd.com.sanqiwan.reader.analysis/#";
    private BookDatabaseHelper mBookDatabaseHelper = BookDatabaseHelper.getInstance();
    private ReaderUris mReaderUris = ReaderUris.getInstance();
    private int mCode;

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        mCode = mReaderUris.match(uri);
        switch (mCode) {
            case ReaderUris.ANALYSIS_ITEM_CODE:
                selection = DatabaseUtils.concatenateWhere(selection, AnalysisTable._ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{Long.toString(ContentUris.parseId(uri))});
            case ReaderUris.ANALYSIS_CODE:
                Cursor cursor = mBookDatabaseHelper.getReadableDatabase().query(AnalysisTable.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                return cursor;
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        int code = mReaderUris.match(uri);
        switch (code) {
            case ReaderUris.ANALYSIS_CODE:
                return TYPE_CODE;
            case ReaderUris.ANALYSIS_ITEM_CODE:
                return TYPE_ITEM_CODE;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowId = mBookDatabaseHelper.getWritableDatabase().insert(AnalysisTable.TABLE_NAME, "", values);
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
            case ReaderUris.ANALYSIS_ITEM_CODE:
                selection = DatabaseUtils.concatenateWhere(selection, AnalysisTable._ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{Long.toString(ContentUris.parseId(uri))});
            case ReaderUris.ANALYSIS_CODE:
                int rowId = mBookDatabaseHelper.getWritableDatabase().delete(AnalysisTable.TABLE_NAME, selection, selectionArgs);
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
            case ReaderUris.ANALYSIS_ITEM_CODE:
                selection = DatabaseUtils.concatenateWhere(selection, AnalysisTable._ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{Long.toString(ContentUris.parseId(uri))});
            case ReaderUris.ANALYSIS_CODE:
                int rowId = mBookDatabaseHelper.getWritableDatabase().update(AnalysisTable.TABLE_NAME, values, selection, selectionArgs);
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
        if (code == ReaderUris.ANALYSIS_CODE || code == ReaderUris.ANALYSIS_ITEM_CODE) {
            return true;
        }
        return false;
    }
}
