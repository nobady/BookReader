package com.sanqiwan.reader.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.sanqiwan.reader.AppContext;
import com.sanqiwan.reader.model.AnalysisInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-9-4
 * Time: 下午2:11
 * To change this template use File | Settings | File Templates.
 */
public class AnalysisManager {
    private Context mContext;
    private static final String[] ANALYSIS_PROJECTION = new String[]{AnalysisTable._ID,
            AnalysisTable.DATA, AnalysisTable.URI};
    private static final String[] ID_PROJECTION = new String[]{AnalysisTable._ID};

    public AnalysisManager() {
        mContext = AppContext.getInstance();
    }

    public List<AnalysisInfo> queryData() {
        List<AnalysisInfo> infos = new ArrayList<AnalysisInfo>();
        AnalysisInfo info;
        Cursor cursor = mContext.getContentResolver().query(ReaderUris.ANALYSIS_URI, ANALYSIS_PROJECTION,
                null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    info = new AnalysisInfo();
                    info.setID(cursor.getInt(cursor.getColumnIndex(AnalysisTable._ID)));
                    info.setData(cursor.getString(cursor.getColumnIndex(AnalysisTable.DATA)));
                    info.setUri(cursor.getString(cursor.getColumnIndex(AnalysisTable.URI)));
                    infos.add(info);
                    cursor.moveToNext();
                }
                return infos;
            }
            return null;
        } finally {
            cursor.close();
        }
    }

    public int getSize() {
        int count = 0;
        Cursor cursor = mContext.getContentResolver().query(ReaderUris.ANALYSIS_URI, ID_PROJECTION,
                null, null, null);
        if (cursor != null) {
            try {
                count = cursor.getCount();
            } finally {
                cursor.close();
            }
        }
        return count;
    }

    public Uri insert(AnalysisInfo analysisInfo) {
        Uri uri = null;
        if (analysisInfo != null) {
            ContentValues values = new ContentValues();
            values.put(AnalysisTable.DATA, analysisInfo.getData());
            values.put(AnalysisTable.URI, analysisInfo.getUri());

            uri = mContext.getContentResolver().insert(ReaderUris.ANALYSIS_URI, values);
        }
        return uri;
    }

    public int update(AnalysisInfo analysisInfo) {
        int count = 0;
        if (analysisInfo != null) {
            Uri uri = ContentUris.withAppendedId(ReaderUris.ANALYSIS_URI, analysisInfo.getID());
            ContentValues values = new ContentValues();
            values.put(AnalysisTable.DATA, analysisInfo.getData());

            count = mContext.getContentResolver().update(uri, values, null, null);
        }
        return count;
    }

    public int deleteByID(int id) {
        int count = 0;
        if (id != 0) {
            Uri uri = ContentUris.withAppendedId(ReaderUris.ANALYSIS_URI, id);
            count = mContext.getContentResolver().delete(uri, null, null);
        }
        return count;
    }

    public int deleteAll() {
        return mContext.getContentResolver().delete(ReaderUris.ANALYSIS_URI, null, null);
    }

}
