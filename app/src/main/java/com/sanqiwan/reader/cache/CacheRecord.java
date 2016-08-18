package com.sanqiwan.reader.cache;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CacheRecord implements Comparable<CacheRecord> {

    private static final String KEY_BOOK_ID = "book_id";
    private static final String KEY_CHAPTER_ID = "chapter_id";
    private static final String KEY_SIZE = "size";
    private static final String KEY_TIME = "time";

    private long mBookId;
    private long mChapterId;
    private long mSize;
    private long mTime;

    public long getBookId() {
        return mBookId;
    }

    public void setBookId(long bookId) {
        mBookId = bookId;
    }

    public long getChapterId() {
        return mChapterId;
    }

    public void setChapterId(long chapterId) {
        mChapterId = chapterId;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long size) {
        mSize = size;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_BOOK_ID, mBookId);
            jsonObject.put(KEY_CHAPTER_ID, mChapterId);
            jsonObject.put(KEY_SIZE, mSize);
            jsonObject.put(KEY_TIME, mTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static CacheRecord fromJsonObject(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        CacheRecord cacheRecord = new CacheRecord();
        cacheRecord.setBookId(jsonObject.optLong(KEY_BOOK_ID));
        cacheRecord.setChapterId(jsonObject.optLong(KEY_CHAPTER_ID));
        cacheRecord.setSize(jsonObject.optInt(KEY_SIZE));
        cacheRecord.setTime(jsonObject.optInt(KEY_TIME));
        return cacheRecord;
    }

    public static List<CacheRecord> fromJsonArray(JSONArray jsonArray) {
        List<CacheRecord> cacheRecords = new ArrayList<CacheRecord>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            CacheRecord cacheRecord = fromJsonObject(jsonArray.optJSONObject(i));
            if (cacheRecord != null) {
                cacheRecords.add(cacheRecord);
            }
        }

        Collections.sort(cacheRecords);
        return cacheRecords;
    }

    public static String toJsonArrayString(List<CacheRecord> cacheRecords) {
        JSONArray jsonArray = new JSONArray();
        if (cacheRecords != null) {
            for (CacheRecord record : new ArrayList<CacheRecord>(cacheRecords)) {
                if (record != null) {
                    jsonArray.put(record.toJsonObject());
                }
            }
        }
        return jsonArray.toString();
    }

    @Override
    public int compareTo(CacheRecord another) {

        if (mTime > another.mTime) {
            return 1;
        } else if (mTime < another.mTime) {
            return -1;
        } else {
            return 0;
        }
    }

}