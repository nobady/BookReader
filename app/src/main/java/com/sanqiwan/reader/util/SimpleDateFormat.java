package com.sanqiwan.reader.util;

import android.text.format.DateFormat;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 11/14/13
 * Time: 10:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleDateFormat {

    public static CharSequence formatUnixTimestamp(String format, long timestamp) {
        return DateFormat.format(format, timestamp * 1000);
    }
}
