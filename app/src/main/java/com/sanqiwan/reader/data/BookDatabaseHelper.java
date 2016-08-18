package com.sanqiwan.reader.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.sanqiwan.reader.AppContext;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/22/13
 * Time: 2:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class BookDatabaseHelper extends SQLiteOpenHelper {

    private static volatile BookDatabaseHelper sBookDatabaseHelper;
    // 1 -> 2  add column 'has_update' in table 'books' ,Add table 'bookrecommends'
    // 2 -> 3  add column 'banner' in table 'topic',add column 'cate_id' in table 'bookrecommends',
    // 3 -> 4  add column 'cover' in table 'read_history'
    // 4 -> 5  add table 'AnalysisTable' in reader_books.db
    private static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "reader_books.db";

    public static BookDatabaseHelper getInstance() {
        if (sBookDatabaseHelper == null) {
            synchronized (BookDatabaseHelper.class) {
                if (sBookDatabaseHelper == null) {
                    sBookDatabaseHelper = new BookDatabaseHelper(AppContext.getInstance());
                }
            }
        }
        return sBookDatabaseHelper;
    }

    public BookDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        onUpgrade(db, 0, DATABASE_VERSION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int version = oldVersion + 1; version <= newVersion; version++) {
            upgradeTo(db, version);
        }
    }

    private void upgradeTo(SQLiteDatabase db, int version) {
        switch (version) {
            case 1:
                db.execSQL(BookTable.CREATE_TABLE_BOOKS);
                db.execSQL(OnlineBookTable.CREATE_TABLE_ONLINE_BOOKS);
                db.execSQL(TopicTable.CREATE_TABLE_TOPIC);    //创建专题表
                db.execSQL(SplashTable.CREATE_TABLE_SPLASH);
                db.execSQL(HistoryTable.CREAT_HISTORY_TABLE);
                break;

            case 2:
                addColumn(db, BookTable.TABLE_NAME, BookTable.HAS_UPDATE,
                        "INTEGER NOT NULL DEFAULT 0");
                db.execSQL(BookRecommendTable.CREATE_TABLE_BOOKRECOMMEND);
                break;
            case 3:
                addColumn(db, TopicTable.TOPIC_TABLE, TopicTable.COL_TOPIC_BANNER_URL," text");
                addColumn(db, BookRecommendTable.BOOKRECOMMEND_TABLE, BookRecommendTable.CATEGORY_ID,
                        "INTEGER NOT NULL DEFAULT 0");
                break;
            case 4:
                addColumn(db, HistoryTable.TABLE_NAME, HistoryTable.COVER, " text");
                break;
            case 5:
                db.execSQL(AnalysisTable.CREATE_ANALYSIS_TABLE);
                break;
            default:
                throw new IllegalStateException("Don't know how to upgrade to " + version);
        }
    }

    private void addColumn(SQLiteDatabase db, String dbTable, String columnName,
                           String columnDefinition) {
        db.execSQL("ALTER TABLE " + dbTable + " ADD COLUMN " + columnName + " "
                + columnDefinition);
    }

}
