package com.sanqiwan.reader.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/23/13
 * Time: 6:47 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IDatabase {
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder);

    public String getType(Uri uri);

    public Uri insert(Uri uri, ContentValues values);

    public int delete(Uri uri, String selection, String[] selectionArgs);

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs);
}
