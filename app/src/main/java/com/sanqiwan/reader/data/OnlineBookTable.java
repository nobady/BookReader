package com.sanqiwan.reader.data;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-7-30
 * Time: 下午2:54
 * To change this template use File | Settings | File Templates.
 */
public class OnlineBookTable {

    //query columns
    public static final String ONLINE_BOOKS_TABLE = "online_books";

    public static final String COL_BOOK_ID = "book_id";
    public static final String COL_BOOK_NAME = "book_name";
    public static final String COL_FINISH = "finish";
    public static final String COL_BOOK_COVER = "book_cover";
    public static final String COL_AUTHOR = "author";
    public static final String COL_DOWNLOAD = "download";
    public static final String COL_UPDATE_DATE = "update_date";
    public static final String COL_COMMENDS = "commends";
    public static final String COL_CATEGORY = "category";

    public static final String CREATE_TABLE_ONLINE_BOOKS = " create table "
            + " online_books(book_id long primary key,book_name text, finish boolean, book_cover text, "
            + "author text, download boolean,  commends long, update_date long, category text) ";
    //finish 0:连载，1：完结；download 0：未入书库，1：加入书库.

    public static final String DROP_TABLE_ONLINE_BOOKS = "drop table if exists " + ONLINE_BOOKS_TABLE;


}

//SQLite并没有提供专门的布尔存储类型，取而代之的是存储整型1表示true，0表示false。