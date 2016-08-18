package com.sanqiwan.reader.data;

import android.content.UriMatcher;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/23/13
 * Time: 6:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class Databases {

    private static Map<Integer, IDatabase> sDatabasesMap = new HashMap<Integer, IDatabase>();
    private static ReaderUris sReaderUris = ReaderUris.getInstance();

    public static IDatabase getDatabase(Uri uri) {
        int code = sReaderUris.match(uri);

        if (code == UriMatcher.NO_MATCH) {
            return null;
        }

        IDatabase database = sDatabasesMap.get(code);
        if (database != null) {
            return database;
        }

        switch (code) {
            case ReaderUris.CHAPTER_CACHE_CODE:
            case ReaderUris.CHAPTER_CACHE_ITEM_CODE:
                database = new ChapterCacheDatabase();
                sDatabasesMap.put(ReaderUris.CHAPTER_CACHE_CODE, database);
                sDatabasesMap.put(ReaderUris.CHAPTER_CACHE_ITEM_CODE, database);
                return database;

            case ReaderUris.ONLINE_BOOKS_CODE:
            case ReaderUris.ONLINE_BOOKS_ITEM_CODE:
                database = new OnlineBooksDatabase();
                sDatabasesMap.put(ReaderUris.ONLINE_BOOKS_CODE, database);
                sDatabasesMap.put(ReaderUris.ONLINE_BOOKS_ITEM_CODE, database);
                return database;

            case ReaderUris.OPERATION_CODE:
            case ReaderUris.OPERATION_ITEM_CODE:
                database = new OperationDatabase();
                sDatabasesMap.put(ReaderUris.OPERATION_CODE, database);
                sDatabasesMap.put(ReaderUris.OPERATION_ITEM_CODE, database);
                return database;

            case ReaderUris.SPLASH_CODE:
            case ReaderUris.SPLASH_ITEM_CODE:
                database = new SplashDatabase();
                sDatabasesMap.put(ReaderUris.SPLASH_CODE, database);
                sDatabasesMap.put(ReaderUris.SPLASH_ITEM_CODE, database);
                return database;
            case ReaderUris.BOOKS_CODE:
            case ReaderUris.BOOKS_ITEM_CODE:
                database = new BooksDatabase();
                sDatabasesMap.put(ReaderUris.BOOKS_CODE, database);
                sDatabasesMap.put(ReaderUris.BOOKS_ITEM_CODE, database);
                return database;
            case ReaderUris.TOPIC_CODE:
            case ReaderUris.TOPIC_ITEM_CODE:
                database = new TopicDatabase();
                sDatabasesMap.put(ReaderUris.TOPIC_CODE,database);
                sDatabasesMap.put(ReaderUris.TOPIC_ITEM_CODE,database);
                return database;
            case ReaderUris.HISTORY_CODE:
            case ReaderUris.HISTORY_ITEM_CODE:
                database = new HistoryDatabase();
                sDatabasesMap.put(ReaderUris.HISTORY_CODE,database);
                sDatabasesMap.put(ReaderUris.HISTORY_ITEM_CODE,database);
                return database;
            case ReaderUris.BOOKRECOMMEND_CODE:
            case ReaderUris.BOOKRECOMMEND_ITEM_CODE:
                database = new BookRecommendDatabase();
                sDatabasesMap.put(ReaderUris.BOOKRECOMMEND_CODE,database);
                sDatabasesMap.put(ReaderUris.BOOKRECOMMEND_ITEM_CODE,database);
                return database;
            case ReaderUris.ANALYSIS_CODE:
            case ReaderUris.ANALYSIS_ITEM_CODE:
                database = new AnalysisDatabase();
                sDatabasesMap.put(ReaderUris.ANALYSIS_CODE,database);
                sDatabasesMap.put(ReaderUris.ANALYSIS_ITEM_CODE,database);
                return database;

        }
        return null;
    }

    private Databases() {

    }
}
