package com.sanqiwan.reader.data;

import android.text.TextUtils;

/**
 * Created with IntelliJ IDEA.
 * User: lenovo
 * Date: 13-8-5
 * Time: 下午2:55
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseUtils {
    public static String concatenateWhere(String a, String b) {
        if (TextUtils.isEmpty(a)) {
            return b;
        }
        if (TextUtils.isEmpty(b)) {
            return a;
        }

        return "(" + a + ") AND (" + b + ")";
    }

    public static String[] appendSelectionArgs(String[] originalValues, String[] newValues) {
        if (originalValues == null || originalValues.length == 0) {
            return newValues;
        }
        String[] result = new String[originalValues.length + newValues.length];
        System.arraycopy(originalValues, 0, result, 0, originalValues.length);
        System.arraycopy(newValues, 0, result, originalValues.length, newValues.length);
        return result;
    }
}
