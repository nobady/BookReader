package com.sanqiwan.reader.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.sanqiwan.reader.model.Splash;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/23/13
 * Time: 4:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class SplashManager {

    private List<Splash> mSplashList;
    private Context mContext;
    String[] projection = new String[]{SplashTable.SPLASH_ID, SplashTable.SPLASH_COVER_URL,
            SplashTable.SPLASH_DESCRIBE_TEXT, SplashTable.SPLASH_REFRESH_TIME, SplashTable.SPLASH_PAST_TIME};

    public SplashManager(Context context) {
        mSplashList = new ArrayList<Splash>();
        mContext = context;
    }

    public void addSplash(Splash splash) {
        ContentValues values = new ContentValues();
        values.put(SplashTable.SPLASH_ID, splash.getId());
        values.put(SplashTable.SPLASH_COVER_URL, splash.getPictureUrl());
        values.put(SplashTable.SPLASH_DESCRIBE_TEXT, splash.getDescribeText());
        values.put(SplashTable.SPLASH_REFRESH_TIME, splash.getRefreshTime());
        values.put(SplashTable.SPLASH_PAST_TIME, splash.getPastTime());
        mContext.getContentResolver().insert(ReaderUris.SPLASH_URI, values);
    }

    public void addSplashlist(List<Splash> SplashList) {
        for (Splash splash : SplashList) {
            addSplash(splash);
        }
    }

    public Splash getSplash(int splashId) {
        Splash mSplash = new Splash();
        Cursor cursor = mContext.getContentResolver().query(ReaderUris.SPLASH_URI, projection, SplashTable.SPLASH_ID + "=" + splashId, null, null);
        try {
            if (cursor != null) {
                cursor.moveToFirst();
                mSplash.setId(cursor.getInt(0));
                mSplash.setPictureUrl(cursor.getString(1));
                mSplash.setDescribeText(cursor.getString(2));
                mSplash.setRefreshTime(cursor.getLong(3));
                mSplash.setPastTime(cursor.getLong(4));
                return mSplash;
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public List<Splash> getAllSplash() {
        Cursor cursor = mContext.getContentResolver().query(ReaderUris.SPLASH_URI, projection, null, null, null);
        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Splash splash = new Splash();
                    splash.setId(cursor.getInt(0));
                    splash.setId(cursor.getInt(0));
                    splash.setPictureUrl(cursor.getString(1));
                    splash.setDescribeText(cursor.getString(2));
                    splash.setRefreshTime(cursor.getLong(3));
                    splash.setPastTime(cursor.getLong(4));
                    mSplashList.add(splash);
                }
            }
        } finally {
            cursor.close();
        }
        return mSplashList;
    }

    public int deleteSplash(int id) {
        return mContext.getContentResolver().delete(ReaderUris.SPLASH_URI, SplashTable.SPLASH_ID + "=" + String.valueOf(id), null);
    }

    public int deleteAllSplash() {
        return mContext.getContentResolver().delete(ReaderUris.SPLASH_URI, null, null);
    }

}
