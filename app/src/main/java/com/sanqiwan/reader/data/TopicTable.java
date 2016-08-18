package com.sanqiwan.reader.data;

/**
 * User: Sam
 * Date: 13-8-2
 * Time: 上午10:44
 */
public class TopicTable {

    public static final String TOPIC_TABLE = "topics";
    public static final String COL_TOPIC_ID = "id";
    public static final String COL_TOPIC_PICURL = "picurl";
    public static final String COL_TOPIC_CREATETIME = "createtime";
    public static final String COL_TOPIC_TITLE = "title";
    public static final String COL_TOPIC_DESCRIBE = "describe";
    public static final String COL_TOPIC_BOOKIDS = "bookids";
    public static final String COL_TOPIC_BANNER_URL = "banner";

    public static final String CREATE_TABLE_TOPIC = "create table if not exists  " + TOPIC_TABLE + " ("
            + COL_TOPIC_ID + "  integer primary key,"
            + COL_TOPIC_TITLE + "  varchar(50),"
            + COL_TOPIC_PICURL + " text,"
            + COL_TOPIC_DESCRIBE + " text,"
            + COL_TOPIC_BOOKIDS + " text,"
            + COL_TOPIC_CREATETIME + " integer)";

    public static final String DROP_TABLE_TOPIC = "drop table if exists  " + TOPIC_TABLE;
}
