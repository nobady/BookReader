package com.sanqiwan.reader.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.sanqiwan.reader.AppContext;
import com.sanqiwan.reader.model.HistoryItem;

/**
 * Created with IntelliJ IDEA.
 * User: lenovo
 * Date: 13-8-8
 * Time: 上午9:30
 * To change this template use File | Settings | File Templates.
 */
public class HistoryManager {
    private Context mContext;
    private static final String[] PROJECTION = new String[] {
            HistoryTable._ID,
            HistoryTable.BOOK_ID,
            HistoryTable.NAME,
            HistoryTable.TIME,
            HistoryTable.CHAPTER_ID,
            HistoryTable.CHAPTER_NAME,
            HistoryTable.COVER
    };

    private static final String[] ID_PROJECTION = new String[] { HistoryTable._ID};

    public static final int ID_INDEX = 0;
    public static final int BOOK_ID_INDEX = 1;
    public static final int NAME_INDEX = 2;
    public static final int TIME_INDEX = 3;
    public static final int CHAPTER_ID_INDEX = 4;
    public static final int CHAPTER_NAME_INDEX = 5;
    public static final int COVER_INDEX = 6;

    public HistoryManager() {
        mContext = AppContext.getInstance();
    }

    public void addHistoryItem(HistoryItem historyItem) {
        ContentValues values = new ContentValues();
        values.put(HistoryTable.BOOK_ID, historyItem.getBookId());
        values.put(HistoryTable.NAME, historyItem.getBookName());
        values.put(HistoryTable.TIME, historyItem.getTime());
        values.put(HistoryTable.CHAPTER_ID, historyItem.getChapterId());
        values.put(HistoryTable.CHAPTER_NAME, historyItem.getChapterName());
        values.put(HistoryTable.COVER, historyItem.getCover());
        mContext.getContentResolver().insert(ReaderUris.HISTORY_URI, values);
    }

    public int updateHistoryItem(HistoryItem historyItem) {
        ContentValues values = new ContentValues();
        values.put(HistoryTable.NAME, historyItem.getBookName());
        values.put(HistoryTable.TIME, historyItem.getTime());
        values.put(HistoryTable.CHAPTER_ID, historyItem.getChapterId());
        values.put(HistoryTable.CHAPTER_NAME, historyItem.getChapterName());
        values.put(HistoryTable.COVER, historyItem.getCover());
        String[] id = new String[]{String.valueOf(historyItem.getBookId())};
        String where = HistoryTable.BOOK_ID + "=?";
        return mContext.getContentResolver().update(ReaderUris.HISTORY_URI, values, where, id);
    }

    public int deleteHistoryItem(long id) {
        return mContext.getContentResolver().delete(ReaderUris.HISTORY_URI,
                HistoryTable.BOOK_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int deleteAllHistoryItem() {
        return mContext.getContentResolver().delete(ReaderUris.HISTORY_URI, null, null);
    }

    public HistoryItem getHistoryItem(long bookId) {
        Cursor cursor = mContext.getContentResolver().query(ReaderUris.HISTORY_URI, PROJECTION, HistoryTable.BOOK_ID + "=" + bookId, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    HistoryItem historyItem = new HistoryItem();
                    historyItem.setBookId(cursor.getInt(BOOK_ID_INDEX));
                    historyItem.setBookName(cursor.getString(NAME_INDEX));
                    historyItem.setTime(cursor.getInt(TIME_INDEX));
                    historyItem.setChapterId(cursor.getInt(CHAPTER_ID_INDEX));
                    historyItem.setChapterName(cursor.getString(CHAPTER_NAME_INDEX));
                    historyItem.setCover(cursor.getString(COVER_INDEX));
                    return historyItem;
                }
            } finally {
                cursor.close();
            }
        }

        return null;
    }
    public Cursor getAllHistory() {
        return  mContext.getContentResolver().query(ReaderUris.HISTORY_URI, PROJECTION, null, null, HistoryTable.TIME+" desc");
    }

    public int getHistoryCount() {
        Cursor c =  mContext.getContentResolver().query(ReaderUris.HISTORY_URI,
                ID_PROJECTION, null, null, null);
        if (c != null) {
            try {
                return c.getCount();
            }finally {
                c.close();
            }
        }
        return 0;
    }
}
