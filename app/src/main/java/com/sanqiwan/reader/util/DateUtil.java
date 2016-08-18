package com.sanqiwan.reader.util;

import android.os.SystemClock;

/**
 * Created with IntelliJ IDEA.
 * User: jwb
 * Date: 13-11-5
 * Time: 上午9:29
 * To change this template use File | Settings | File Templates.
 */
public class DateUtil {

    private static final long MILLISECOND = 1000;

    public static long getTime(long unixTime) {
        return unixTime * MILLISECOND;
    }

    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    public static long getElapsedRealTime() {
        return SystemClock.elapsedRealtime();
    }

    public static long getUnixTime() {
        return System.currentTimeMillis() / 1000;
    }
}
