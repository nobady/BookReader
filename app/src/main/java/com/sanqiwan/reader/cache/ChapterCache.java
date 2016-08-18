package com.sanqiwan.reader.cache;

import com.sanqiwan.reader.model.Chapter;
import com.sanqiwan.reader.preference.Settings;
import com.sanqiwan.reader.util.IOUtil;
import com.sanqiwan.reader.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/19/13
 * Time: 9:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class ChapterCache {

    private static final String CACHE_FILE_EXTENSION = ".cache";
    private static final String RECORDS = "records";
    private static final long LIMIT = 5 * 1024 * 1024; // 5M

    private static volatile ChapterCache sInstance;

    private List<CacheRecord> mCacheRecords;
    private long mLimitSize;
    private long mPurgeSize;

    public static ChapterCache getInstance() {

        if (sInstance == null) {
            synchronized (ChapterCache.class) {
                if (sInstance == null) {
                    sInstance = new ChapterCache();
                }
            }
        }
        return sInstance;
    }

    private ChapterCache() {
        mCacheRecords = loadCacheRecordFromFile();
        if (mCacheRecords == null) {
            mCacheRecords = new Vector<CacheRecord>();
        }
        setCacheLimitSize(LIMIT);
    }

    public Chapter getChapter(long bookId, long chapterId) {

        // load chapter from file
        File chapterCacheFile = getChapterCacheFile(bookId, chapterId);

        if (chapterCacheFile.exists()) {
            return Chapter.fromJsonString(StringUtil.loadStringFromFile(chapterCacheFile));
        }

        return null;
    }

    public void addChapter(Chapter chapter) {
        if (chapter == null) {
            return;
        }
        // write chapter to file
        File chapterCacheFile = getChapterCacheFile(chapter.getBookId(), chapter.getChapterId());
        if (!chapterCacheFile.exists()) {
            StringUtil.saveStringToFile(chapter.toJsonString(), chapterCacheFile);
            CacheRecord cacheRecord = new CacheRecord();
            cacheRecord.setBookId(chapter.getBookId());
            cacheRecord.setChapterId(chapter.getChapterId());
            cacheRecord.setSize(chapterCacheFile.length());
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

    public void removeChapter(long bookId, long chapterId) {

        removeChapterFile(bookId, chapterId);
        removeCacheRecord(bookId, chapterId);
    }

    private void removeChapterFile(long bookId, long chapterId) {
        File chapterCacheFile = getChapterCacheFile(bookId, chapterId);
        if (chapterCacheFile.exists()) {
            chapterCacheFile.delete();
        }
    }

    private void removeCacheRecord(long bookId, long chapterId) {
        for (int i = 0; i < mCacheRecords.size(); i++) {
            CacheRecord cacheRecord = mCacheRecords.get(i);
            if (bookId == cacheRecord.getBookId() && chapterId == cacheRecord.getChapterId()) {
                mCacheRecords.remove(i);
                saveCacheRecordToFile();
                break;
            }
        }
    }

    public void clearAllChapters() {

        mCacheRecords.clear();
        getCacheRecordFile().delete();
        // remove chapter cache directory
        File cacheDir = Settings.getInstance().getChapterCacheDir();
        IOUtil.deleteFile(cacheDir);
    }

    private void purgeIfNeeded() {

        long cacheSize = getCacheSize();
        if (cacheSize > mLimitSize) {
            for (int i = 0; i < mCacheRecords.size() && getCacheSize() > mPurgeSize;) {
                CacheRecord record = mCacheRecords.get(i);
                removeChapterFile(record.getBookId(), record.getChapterId());
                mCacheRecords.remove(i);
            }
            saveCacheRecordToFile();
        }
    }

    public long getCacheSize() {
        long size = 0;
        for (CacheRecord record : new ArrayList<CacheRecord>(mCacheRecords)) {
            if(record != null) {
                size += record.getSize();
            }
        }
        return size;
    }

    public static File getChapterCacheFile(long bookId,  long chapterId) {
        File cacheDir = Settings.getInstance().getChapterCacheDir();
        File bookDir = new File(cacheDir, String.valueOf(bookId));
        File chapterFile = new File(bookDir, String.valueOf(chapterId) + CACHE_FILE_EXTENSION);
        return chapterFile;
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
        String jsonArrayString = CacheRecord.toJsonArrayString(new ArrayList<CacheRecord>(mCacheRecords));
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
