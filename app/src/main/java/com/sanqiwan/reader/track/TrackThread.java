package com.sanqiwan.reader.track;

import android.os.Handler;
import android.os.Looper;


/**
 * Created by IBM on 14-1-18.
 */
class TrackThread extends Thread {

    private Handler mHandler;
    private TrackSender mSender;

    @Override
    public void run() {
        Looper.prepare();
        mHandler = new Handler();
        mSender = new TrackSender(mHandler);
        mHandler.post(mSender);
        Looper.loop();
    }

    public void enforceStart() {
        if (mHandler != null && mSender != null) {
            mSender.resetStop();
            mHandler.removeCallbacks(mSender);
            mHandler.post(mSender);
        }
    }

    public void enforceStop() {
        if (mSender != null) {
            mSender.stop();
        }
        if (mHandler != null && mSender != null) {
            mHandler.removeCallbacks(mSender);
        }
    }
}
