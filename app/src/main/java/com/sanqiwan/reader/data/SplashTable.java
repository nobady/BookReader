package com.sanqiwan.reader.data;

/**
 * Created with IntelliJ IDEA.
 * User: lenovo
 * Date: 13-8-1
 * Time: 下午5:15
 * To change this template use File | Settings | File Templates.
 */
public class SplashTable {
    public static final String SPLASH_TABLE = "splash";

    public static final String SPLASH_ID = "id";
    public static final String SPLASH_COVER_URL = "cover_url";
    public static final String SPLASH_DESCRIBE_TEXT = "describe_text";
    public static final String SPLASH_REFRESH_TIME = "refresh_time";
    public static final String SPLASH_PAST_TIME = "past_time";

    public static final String CREATE_TABLE_SPLASH = " create table "
            + " splash(id long primary key, cover_url text, describe_text text, refresh_time long, past_time long)";
}
