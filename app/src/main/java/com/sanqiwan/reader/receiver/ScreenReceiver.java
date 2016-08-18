package com.sanqiwan.reader.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.sanqiwan.reader.track.Tracker;

/**
 * Created by IBM on 14-2-20.
 */
public class ScreenReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (Intent.ACTION_SCREEN_ON.equals(action)) {
            Tracker.getInstance().start();
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            Tracker.getInstance().stop();
        }
    }
}
