package com.sanqiwan.reader.data;

import android.provider.BaseColumns;

/**
 * User: Sam
 * Date: 13-9-23
 * Time: 上午10:44
 */
public class BookRecommendTable  implements BaseColumns {

    public static final String BOOKRECOMMEND_TABLE = "bookrecommends";
    public static final String BOOK_ID = "id";
    public static final String COVER_URL = "picurl";
    public static final String BOOKRECOMMEND_CREATETIME = "createtime";
    public static final String CATEGORY_ID = "cate_id";


    public static final String CREATE_TABLE_BOOKRECOMMEND = "create table if not exists  " + BOOKRECOMMEND_TABLE + " ("
            + _ID + "  integer primary key autoincrement,"
            + BOOK_ID + "  integer,"
            + COVER_URL + "  varchar(100),"
            + BOOKRECOMMEND_CREATETIME + " integer)";

    public static final String DROP_TABLE_BOOKRECOMMEND = "drop table if exists  " + BOOKRECOMMEND_TABLE;
}
