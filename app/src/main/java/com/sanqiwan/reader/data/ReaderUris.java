package com.sanqiwan.reader.data;

import android.content.UriMatcher;
import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/23/13
 * Time: 6:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReaderUris {
    private static volatile ReaderUris sInstance;
    private static final String AUTHORITY = "com.sanqiwan.reader";
    private static final String CHAPTER_CACHE_PATH = "chapter_cache";
    private static final String CHAPTER_CACHE_ITEM_PATH = CHAPTER_CACHE_PATH + "/#";
    protected static final int CHAPTER_CACHE_CODE = 11;
    protected static final int CHAPTER_CACHE_ITEM_CODE = 12;
    private static final String ONLINE_BOOKS_PATH = "online_books";
    private static final String ONLINE_BOOKS_ITEM_PATH = ONLINE_BOOKS_PATH + "/#";
    protected static final int ONLINE_BOOKS_CODE = 21;
    protected static final int ONLINE_BOOKS_ITEM_CODE = 22;
    private static final String OPERATION_PATH = "operation";
    private static final String OPERATION_ITEM_PATH = OPERATION_PATH + "/#";
    protected static final int OPERATION_CODE = 31;
    protected static final int OPERATION_ITEM_CODE = 32;
    private static final String SPLASH_PATH = "splash";
    private static final String SPLASH_ITEM_PATH = SPLASH_PATH + "/#";
    protected static final int SPLASH_CODE = 41;
    protected static final int SPLASH_ITEM_CODE = 42;
    private static final String TOPIC_PATH = "topic";
    private static final String TOPIC_ITEM_PATH = TOPIC_PATH + "/#";
    protected  static final int TOPIC_CODE = 61;
    protected  static final int TOPIC_ITEM_CODE = 62;
    protected static final String BOOKS_PATH = "books";
    protected static final String BOOKS_ITEM_PATH = BOOKS_PATH + "/#";
    protected static final int BOOKS_CODE = 51;
    protected static final int BOOKS_ITEM_CODE = 52;
    protected static final String HISTORY_PATH = "read_history";
    protected static final String HISTORY_ITEM_PATH = HISTORY_PATH + "/#";
    protected static final int HISTORY_CODE = 71;
    protected static final int HISTORY_ITEM_CODE = 72;
    protected static final String BOOKRECOMMEND_PATH = "book_recommends";
    protected static final String BOOKRECOMMEND_ITEM_PATH = BOOKRECOMMEND_PATH + "/#";
    protected static final int BOOKRECOMMEND_CODE = 81;
    protected static final int BOOKRECOMMEND_ITEM_CODE = 82;
    protected static final String ANALYSIS_PATH = "analysis";
    protected static final String ANALYSIS_ITEM_PATH = ANALYSIS_PATH + "/#";
    protected static final int ANALYSIS_CODE = 91;
    protected static final int ANALYSIS_ITEM_CODE = 92;

    public static final Uri CHAPTER_CACHE_URI = Uri.parse("content://" + AUTHORITY +"/" + CHAPTER_CACHE_PATH);
    public static final Uri ONLINE_BOOKS_URI = Uri.parse("content://" + AUTHORITY +"/" + ONLINE_BOOKS_PATH);
    public static final Uri OPERATION_URI = Uri.parse("content://" + AUTHORITY +"/" + OPERATION_PATH);
    public static final Uri SPLASH_URI = Uri.parse("content://" + AUTHORITY +"/" + SPLASH_PATH);
    public static final Uri TOPIC_URI = Uri.parse("content://" + AUTHORITY +"/" + TOPIC_PATH);
    public static final Uri BOOKS_URI = Uri.parse("content://" + AUTHORITY + "/" + BOOKS_PATH);
    public static final Uri HISTORY_URI = Uri.parse("content://" + AUTHORITY +"/" + HISTORY_PATH);
    public static final Uri BOOKRECOMMEND_URI = Uri.parse("content://" + AUTHORITY +"/" + BOOKRECOMMEND_PATH);
    public static final Uri ANALYSIS_URI = Uri.parse("content://" + AUTHORITY +"/" + ANALYSIS_PATH);
    private UriMatcher mUriMatcher;

    public static ReaderUris getInstance() {
        if (sInstance == null) {
            synchronized (ReaderUris.class) {
                if (sInstance == null) {
                    sInstance = new ReaderUris();
                }
            }
        }
        return sInstance;
    }
    private ReaderUris() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, CHAPTER_CACHE_PATH, CHAPTER_CACHE_CODE);
        mUriMatcher.addURI(AUTHORITY, CHAPTER_CACHE_ITEM_PATH, CHAPTER_CACHE_ITEM_CODE);
        mUriMatcher.addURI(AUTHORITY, ONLINE_BOOKS_PATH, ONLINE_BOOKS_CODE);
        mUriMatcher.addURI(AUTHORITY, ONLINE_BOOKS_ITEM_PATH, ONLINE_BOOKS_ITEM_CODE);
        mUriMatcher.addURI(AUTHORITY, OPERATION_PATH, OPERATION_CODE);
        mUriMatcher.addURI(AUTHORITY, OPERATION_ITEM_PATH, OPERATION_ITEM_CODE);
        mUriMatcher.addURI(AUTHORITY, SPLASH_PATH, SPLASH_CODE);
        mUriMatcher.addURI(AUTHORITY, SPLASH_ITEM_PATH, SPLASH_ITEM_CODE);
        mUriMatcher.addURI(AUTHORITY, TOPIC_PATH,TOPIC_CODE);
        mUriMatcher.addURI(AUTHORITY, TOPIC_ITEM_PATH, TOPIC_ITEM_CODE);
        mUriMatcher.addURI(AUTHORITY, BOOKS_PATH, BOOKS_CODE);
        mUriMatcher.addURI(AUTHORITY, BOOKS_ITEM_PATH, BOOKS_ITEM_CODE);
        mUriMatcher.addURI(AUTHORITY, HISTORY_PATH, HISTORY_CODE);
        mUriMatcher.addURI(AUTHORITY, HISTORY_ITEM_PATH, HISTORY_ITEM_CODE);
        mUriMatcher.addURI(AUTHORITY, BOOKRECOMMEND_PATH, BOOKRECOMMEND_CODE);
        mUriMatcher.addURI(AUTHORITY, BOOKRECOMMEND_ITEM_PATH, BOOKRECOMMEND_ITEM_CODE);
        mUriMatcher.addURI(AUTHORITY, ANALYSIS_PATH, ANALYSIS_CODE);
        mUriMatcher.addURI(AUTHORITY, ANALYSIS_ITEM_PATH, ANALYSIS_ITEM_CODE);
    }

    public int match(Uri uri) {
        return mUriMatcher.match(uri);
    }
}
