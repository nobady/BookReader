package com.sanqiwan.reader.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/23/13
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 */
class ChapterCacheDatabase implements IDatabase, IAcceptCode {
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getType(Uri uri) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean acceptCode(int code) {
        if (code == ReaderUris.CHAPTER_CACHE_CODE || code == ReaderUris.CHAPTER_CACHE_ITEM_CODE) {
            return true;
        }
        return false;
    }
}
