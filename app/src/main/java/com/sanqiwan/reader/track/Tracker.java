package com.sanqiwan.reader.track;

import android.text.TextUtils;
import com.sanqiwan.reader.data.AnalysisManager;
import com.sanqiwan.reader.model.AnalysisInfo;
import com.sanqiwan.reader.webservice.OperationServerConfig;
import com.sanqiwan.reader.webservice.OperationUriBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-9-11
 * Time: 下午5:22
 * To change this template use File | Settings | File Templates.
 */
public class Tracker {

    private AnalysisManager mAnalysisManager;
    private TrackBuilder mTrackBuilder;
    private TrackThread mTrackThread;
    private static Tracker sInstance;
    private OperationUriBuilder mOperationUriBuilder;

    private Tracker() {
        initAnalysisGameManager();
        initTrackBuilder();
        mOperationUriBuilder = new OperationUriBuilder(new OperationServerConfig());

    }

    public static Tracker getInstance() {
        if (sInstance == null) {
            synchronized (Tracker.class) {
                if (sInstance == null) {
                    sInstance = new Tracker();
                }
            }
        }
        return sInstance;
    }

    private void initAnalysisGameManager() {
        if (mAnalysisManager == null) {
            mAnalysisManager = new AnalysisManager();
        }
    }

    private void initTrackBuilder() {
        if (mTrackBuilder == null) {
            mTrackBuilder = new TrackBuilder();
        }
    }

    private void ensureTrackThread() {
        if (mTrackThread == null) {
            synchronized (TrackThread.class) {
                if (mTrackThread == null) {
                    mTrackThread = new TrackThread();
                    mTrackThread.start();
                }
            }
        }
    }

    public void start() {
        if (mTrackThread != null) {
            mTrackThread.enforceStart();
        } else {
            ensureTrackThread();
        }
    }

    public void stop() {
        if (mTrackThread != null) {
            mTrackThread.enforceStop();
        }
    }

    private void saveAnalysisJson(String jsonData) {
        if (!TextUtils.isEmpty(jsonData)) {
            AnalysisInfo info = new AnalysisInfo();
            info.setData(jsonData);
            info.setUri(mOperationUriBuilder.buildUriForAnalysis());//该数据对应发送的url
            mAnalysisManager.insert(info);
            ensureTrackThread();
        }
    }

    public void trackAppActivated() {
        saveAnalysisJson(mTrackBuilder.buildAppActivateJson());
    }

    public void trackLaunchApp() {
        saveAnalysisJson(mTrackBuilder.buildLaunchAppJson());
    }
}
