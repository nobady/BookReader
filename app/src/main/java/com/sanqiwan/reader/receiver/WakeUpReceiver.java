package com.sanqiwan.reader.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.sanqiwan.reader.notification.NotificationHelper;
import com.sanqiwan.reader.preference.Settings;

/**
 * Created by IBM on 14-4-11.
 */
public class WakeUpReceiver extends BroadcastReceiver {

    public static final String ACTION = "com.sanqiwan.reader.wake_up";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION.equals(action)) {
            NotificationHelper.getInstance().wakeUpNotify();
            Settings.setWakeUpTime(WakeUpHelper.getWakeUpTime());//发送一次通知后，重置下一次发送通知的时间。
        }
    }
}
