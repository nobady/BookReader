package com.sanqiwan.reader.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import com.sanqiwan.reader.AppContext;
import com.sanqiwan.reader.preference.Settings;
import com.sanqiwan.reader.track.Tracker;
import com.sanqiwan.reader.util.DateUtil;
import com.sanqiwan.reader.util.NetWorkUtil;

/**
 * Created by IBM on 14-1-15.
 */
public class NetworkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            startTracker();
            checkBootComplete(context);
        }
    }

    private void startTracker() {
        if (NetWorkUtil.isNetworkAvailable(AppContext.getInstance())) {
            Tracker.getInstance().start();
        }
    }

    private void checkBootComplete(Context context) {
        if (DateUtil.getElapsedRealTime() < Settings.getElapsedRealTime()) {//手机重启了
            WakeUpHelper.resetAlarm(context);
        }
    }

}
