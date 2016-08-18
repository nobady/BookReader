package com.sanqiwan.reader.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * User: Sam
 * Date: 13-8-1
 * Time: 上午10:54
 */
public class TopicDatabase implements IDatabase, IAcceptCode {

    private BookDatabaseHelper mBookDatabaseHelper = BookDatabaseHelper.getInstance();
    private ReaderUris mReaderUris = ReaderUris.getInstance();
    private int mCode;

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        mCode = mReaderUris.match(uri);
        switch (mCode) {
            case ReaderUris.TOPIC_ITEM_CODE:
                selection = DatabaseUtils.concatenateWhere(selection, TopicTable.COL_TOPIC_ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{Long.toString(ContentUris.parseId(uri))});
            case ReaderUris.TOPIC_CODE:
                Cursor cursor = mBookDatabaseHelper.getReadableDatabase().query(TopicTable.TOPIC_TABLE,
                        projection, selection, selectionArgs, null, null, sortOrder);
                return cursor;
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        int code = mReaderUris.match(uri);
        switch (code) {
            case ReaderUris.TOPIC_CODE:
                return "vnd.android.cursor.dir/vnd.com.sanqiwan.reader.topics";
            case ReaderUris.TOPIC_ITEM_CODE:
                return "vnd.android.cursor.dir/vnd.com.sanqiwan.reader.topics/#";
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (mReaderUris.match(uri) != ReaderUris.TOPIC_CODE) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        long rowId = mBookDatabaseHelper.getWritableDatabase().insert(TopicTable.TOPIC_TABLE, "", values);
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
            case ReaderUris.TOPIC_ITEM_CODE:
                selection = DatabaseUtils.concatenateWhere(selection, TopicTable.COL_TOPIC_ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{Long.toString(ContentUris.parseId(uri))});
            case ReaderUris.TOPIC_CODE:
                int rowId = mBookDatabaseHelper.getWritableDatabase().delete(TopicTable.TOPIC_TABLE, selection, selectionArgs);
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
            case ReaderUris.TOPIC_ITEM_CODE:
                selection = DatabaseUtils.concatenateWhere(selection, TopicTable.COL_TOPIC_ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{Long.toString(ContentUris.parseId(uri))});
            case ReaderUris.TOPIC_CODE:
                int rowId = mBookDatabaseHelper.getWritableDatabase().update(TopicTable.TOPIC_TABLE, values, selection, selectionArgs);
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
        if (code == ReaderUris.TOPIC_ITEM_CODE || code == ReaderUris.TOPIC_CODE) {
            return true;
        }
        return false;
    }
}
