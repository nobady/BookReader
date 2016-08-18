package com.sanqiwan.reader.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/22/13
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReaderProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        IDatabase database = getDatabase(uri);
        if (database == null) {
            return null;
        }

        Cursor cursor = database.query(uri, projection, selection, selectionArgs, sortOrder);
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        IDatabase database = getDatabase(uri);
        if (database == null) {
            return null;
        }
        String type = database.getType(uri);
        return type;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        IDatabase database = getDatabase(uri);
        if (database == null) {
            return null;
        }
        Uri result = database.insert(uri, values);
        if (result != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        IDatabase database = getDatabase(uri);
        if (database == null) {
            return 0;
        }
        int result = database.delete(uri, selection, selectionArgs);
        if (result > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        IDatabase database = getDatabase(uri);
        if (database == null) {
            return 0;
        }
        int result = database.update(uri, values, selection, selectionArgs);
        if (result > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

    private IDatabase getDatabase(Uri uri) {
        return Databases.getDatabase(uri);
    }
}
