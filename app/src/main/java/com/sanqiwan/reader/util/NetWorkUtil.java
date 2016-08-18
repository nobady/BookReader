package com.sanqiwan.reader.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * User: sam
 * Date: 8/21/13
 * Time: 5:35 PM
 */
public class NetWorkUtil {

    private static final String LOG_TAG = "NetWorkUtil";

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity == null) {
            Log.w(LOG_TAG, "couldn't get connectivity manager");
        } else {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null) {
                return info.isConnected();
            }
        }
        Log.d(LOG_TAG, "network is not available");
        return false;
    }
}
