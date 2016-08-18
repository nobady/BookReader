package com.sanqiwan.reader.util;

import android.text.TextUtils;

/**
 * Created by chen on 1/4/14.
 */
public class CoverUtil {

    private final static String NO_COVER_NAME = "noBookPic.jpg";
    public static boolean hasNoCover(String cover) {
        return TextUtils.isEmpty(cover) || cover.endsWith(NO_COVER_NAME);
    }
}
