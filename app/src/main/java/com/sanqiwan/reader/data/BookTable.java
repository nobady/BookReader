package com.sanqiwan.reader.data;

import android.provider.BaseColumns;

/**
 * Created with IntelliJ IDEA.
 * User: lenovo
 * Date: 13-7-31
 * Time: 上午10:28
 * To change this template use File | Settings | File Templates.
 */
public class BookTable implements BaseColumns {

    public static final String TABLE_NAME = "books";

    public static final String BOOK_ID = "book_id";
    public static final String NAME = "name";
    public static final String COVER = "cover";
    public static final String AUTHOR_NAME = "author_name";
    public static final String CREATE_TIME = "create_time";
    public static final String DESCRIPTION = "description";
    public static final String SIZE = "size";
    public static final String PATH = "path";
    public static final String FINISH = "finish";
    public static final String HAS_UPDATE = "has_update";

    public static final String CREATE_TABLE_BOOKS = " create table " +
            TABLE_NAME + " (" +
            _ID + " Integer primary key autoincrement, " +
            BOOK_ID + " long, " +
            NAME + " text, " +
            COVER + " text, " +
            AUTHOR_NAME + " text, " +
            CREATE_TIME + " long, " +
            DESCRIPTION + " text, " +
            SIZE + " long, " +
            PATH + " text, " +
            FINISH + " boolean" +
            ")";

}
