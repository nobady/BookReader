package com.sanqiwan.reader.cache;

import com.sanqiwan.reader.model.TOC;
import com.sanqiwan.reader.preference.Settings;
import com.sanqiwan.reader.util.IOUtil;
import com.sanqiwan.reader.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 8/15/13
 * Time: 9:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class TOCCache {
    private static final String CACHE_FILE_EXTENSION = ".cache";
    private static final String RECORDS = "records";
    private static final long LIMIT = 5 * 1024 * 1024; // 5M

    private static volatile TOCCache sInstance;

    private List<CacheRecord> mCacheRecords;
    private long mLimitSize;
    private long mPurgeSize;

    public static TOCCache getInstance() {

        if (sInstance == null) {
            synchronized (TOCCache.class) {
                if (sInstance == null) {
                    sInstance = new TOCCache();
                }
            }
        }
        return sInstance;
    }

    private TOCCache() {
        mCacheRecords = loadCacheRecordFromFile();
        if (mCacheRecords == null) {
            mCacheRecords = new ArrayList<CacheRecord>();
        }
        setCacheLimitSize(LIMIT);
    }

    public TOC getTOC(long bookId, int pageIndex) {

        File chapterCacheFile = getTOCCacheFile(bookId, pageIndex);

        if (chapterCacheFile.exists()) {
            return TOC.fromJsonString(StringUtil.loadStringFromFile(chapterCacheFile));
        }

        return null;
    }

    public void addTOC(TOC toc, int pageIndex) {
        if (toc == null) {
            return;
        }
        // write toc to file
        File tocCacheFile = getTOCCacheFile(toc.getBookId(), pageIndex);
        if (!tocCacheFile.exists()) {
            StringUtil.saveStringToFile(toc.toJsonString(), tocCacheFile);
            CacheRecord cacheRecord = new CacheRecord();
            cacheRecord.setBookId(toc.getBookId());
            cacheRecord.setChapterId(0);
            cacheRecord.setSize(tocCacheFile.length());
            cacheRecord.setTime(System.currentTimeMillis());
            addCacheRecord(cacheRecord);

            purgeIfNeeded();
        }
    }

    private void addCacheRecord(CacheRecord cacheRecord) {
        mCacheRecords.add(cacheRecord);
        saveCacheRecordToFile();
    }

    public int getCount() {
        return mCacheRecords.size();
    }

    public void removeTOC(long bookId, int pageIndex) {
        // remove chapter file
        removeTOCFile(bookId, pageIndex);
        removeCacheRecord(bookId, pageIndex);
    }

    private void removeTOCFile(long bookId, int pageIndex) {
        File chapterCacheFile = getTOCCacheFile(bookId, pageIndex);
        if (chapterCacheFile.exists()) {
            chapterCacheFile.delete();
        }
    }

    private void removeCacheRecord(long bookId, int pageIndex) {
        for (int i = 0; i < mCacheRecords.size(); i++) {
            CacheRecord cacheRecord = mCacheRecords.get(i);
            if (bookId == cacheRecord.getBookId() && pageIndex == cacheRecord.getChapterId()) {
                mCacheRecords.remove(i);
                saveCacheRecordToFile();
                break;
            }
        }
    }

    public void clearAllTOCs() {
        // TODO: clear all records from database

        mCacheRecords.clear();
        getCacheRecordFile().delete();
        // remove chapter cache directory
        File cacheDir = Settings.getInstance().getTOCCacheDir();
        IOUtil.deleteFile(cacheDir);
    }

    private void purgeIfNeeded() {

        long cacheSize = getCacheSize();
        if (cacheSize > mLimitSize) {
            for (int i = 0; i < mCacheRecords.size() && getCacheSize() > mPurgeSize;) {
                CacheRecord record = mCacheRecords.get(i);
                removeTOCFile(record.getBookId(), (int) record.getChapterId());
                mCacheRecords.remove(i);
            }
            saveCacheRecordToFile();
        }
    }

    public long getCacheSize() {
        long size = 0;
        for (CacheRecord record : mCacheRecords) {
            size += record.getSize();
        }
        return size;
    }

    public static File getTOCCacheFile(long bookId, int pageIndex) {
        File cacheDir = Settings.getInstance().getTOCCacheDir();
        File tocDir = new File(cacheDir, String.valueOf(bookId));
        File tocFile = new File(tocDir, String.valueOf(pageIndex) + CACHE_FILE_EXTENSION);
        return tocFile;
    }

    private File getCacheRecordFile() {
        File cacheDir = Settings.getInstance().getChapterCacheDir();
        return new File(cacheDir, RECORDS);
    }

    private List<CacheRecord> loadCacheRecordFromFile() {

        File recordFile = getCacheRecordFile();
        if (!recordFile.exists()) {
            return null;
        }
        String jsonArrayString = StringUtil.loadStringFromFile(recordFile);

        try {
            JSONArray jsonArray = new JSONArray(jsonArrayString);
            return CacheRecord.fromJsonArray(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void saveCacheRecordToFile() {
        if (mCacheRecords == null || mCacheRecords.isEmpty()) {
            return;
        }

        File recordFile = getCacheRecordFile();
        String jsonArrayString = CacheRecord.toJsonArrayString(mCacheRecords);
        StringUtil.saveStringToFile(jsonArrayString, recordFile);
    }

    public void setCacheLimitSize(long limitSize) {
        mLimitSize = limitSize;
        mPurgeSize = limitSize * 2 / 3;
    }

    public long getCacheLimitSize() {
        return mLimitSize;
    }

    public long getPurgeSize() {
        return mPurgeSize;
    }
}
