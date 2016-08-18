package com.sanqiwan.reader.data;

import android.provider.BaseColumns;

/**
 * Created with IntelliJ IDEA.
 * User: lenovo
 * Date: 13-8-8
 * Time: 上午9:30
 * To change this template use File | Settings | File Templates.
 */
public class HistoryTable implements BaseColumns {
    public static final String TABLE_NAME = "read_history";

    public static final String BOOK_ID = "book_id";
    public static final String NAME = "book_name";
    public static final String TIME = "read_time";
    public static final String CHAPTER_ID = "chapter_id";
    public static final String CHAPTER_NAME = "chapter_name";
    public static final String COVER = "cover";

    public static final String CREAT_HISTORY_TABLE = " create table " +
            TABLE_NAME + " (" +
            _ID + " Integer primary key autoincrement, " +
            BOOK_ID + " long, " +
            NAME + " text, " +
            TIME + " long, " +
            CHAPTER_ID + " long, " +
            CHAPTER_NAME + " text" +
            ")";

    public static final String DROP_HISTORY_TABLE = "drop table if exists " + TABLE_NAME;
}
