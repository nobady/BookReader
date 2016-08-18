package com.sanqiwan.reader.util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 12/2/13
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class BatteryUtil {

    private static int sBatteryLevel;

    public static void startMonitor(Activity activity) {
        activity.registerReceiver(sBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public static void stopMonitor(Activity activity) {
        try {
            activity.unregisterReceiver(sBatteryInfoReceiver);
        } catch (IllegalArgumentException e) {
            // do nothing, this exception means sBatteryInfoReceiver was not registered
        }
    }

    public static int getBatteryLevel() {
        return sBatteryLevel;
    }

    private static BroadcastReceiver sBatteryInfoReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final int level = intent.getIntExtra("level", 100);
            sBatteryLevel = level;
        }
    };
}
