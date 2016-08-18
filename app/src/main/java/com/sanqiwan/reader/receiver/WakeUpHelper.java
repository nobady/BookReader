package com.sanqiwan.reader.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.sanqiwan.reader.preference.Settings;

import java.util.Date;

/**
 * Created by IBM on 2014/8/12.
 */
public class WakeUpHelper {

    private static final int REQUEST_CODE = 0;
    private static final int MILLISECOND = 1000;

    public static long getWakeUpTime() {
        return System.currentTimeMillis() + MILLISECOND * 60 * 60 * 24 * 3;
    }

    public static void updateAlarm(Context context) {
        long wakeUpTime = getWakeUpTime();
        Settings.setWakeUpTime(wakeUpTime);
        Settings.setElapsedRealTime();
        createAlarm(context, wakeUpTime);
    }

    private static void createAlarm(Context context, long time) {
        Date wakeTime = new Date();
        wakeTime.setTime(time);

        Intent intent = new Intent(WakeUpReceiver.ACTION);
        PendingIntent rtcIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, wakeTime.getTime(), rtcIntent);
    }

    //开机启动时，读取settings保存的唤醒时间，重置闹钟时间。
    public static void resetAlarm(Context context) {
        long wakeUpTime = Settings.getWakeUpTime();
        createAlarm(context, wakeUpTime);
    }
}
