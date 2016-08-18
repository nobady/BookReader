package com.sanqiwan.reader.track;

import android.os.Handler;
import android.text.TextUtils;
import com.sanqiwan.reader.data.AnalysisManager;
import com.sanqiwan.reader.model.AnalysisInfo;
import com.sanqiwan.reader.util.AesEncrypt;
import com.sanqiwan.reader.util.StringUtil;
import com.sanqiwan.reader.webservice.OperationServerConfig;
import com.sanqiwan.reader.webservice.OperationUriBuilder;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;
import org.geometerplus.zlibrary.core.network.ZLNetworkManager;
import org.geometerplus.zlibrary.core.network.ZLNetworkRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by IBM on 14-1-18.
 */
class TrackSender implements Runnable {
    private static final String POST_PARAMETER_NAME = "data";
    private static final String KEY_STATUS = "status";
    private static final int SUCCESS = 0;
    private static final int INVALID = -1;
    private static final int PERIOD = 30000;
    private static final String KEY = "moH1OX-7T#BMqV[ZhOA+JR7yJ.BgcPuM";  //长度必须为16的倍数
    private AnalysisManager mAnalysisManager;
    private List<AnalysisInfo> mInfoList;
    private OperationUriBuilder mOperationUriBuilder;
    private Handler mHandler;
    private Boolean mStop = false;

    public TrackSender(Handler handler) {
        mAnalysisManager = new AnalysisManager();
        mOperationUriBuilder = new OperationUriBuilder(new OperationServerConfig());
        mHandler = handler;
    }

    @Override
    public void run() {
        sendTrackData();
        mHandler.removeCallbacks(this);
        if (!isStop()) {
            mHandler.postDelayed(this, PERIOD);
        }
    }

    private void sendTrackData() {
        mInfoList = mAnalysisManager.queryData();
        if (mInfoList == null) {
            return;
        }
        List<String> uriList = getUris();
        if (uriList == null) {
            return;
        }
        for (String uri : uriList) {
            sendRequest(uri);
        }
    }

    private void sendRequest(String uri) {
        String postData = getPostData(uri);
        if (TextUtils.isEmpty(postData)) {
            return;
        }
        try {
            //todo 内容编码并发送
            String encodePostData = AesEncrypt.encrypt(postData, KEY);
            ZLNetworkRequest request = getNetworkRequest(uri);
            request.addPostParameter(POST_PARAMETER_NAME, encodePostData);
            ZLNetworkManager.Instance().perform(request);
        } catch (ZLNetworkException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPostData(String sendUri) {
        JSONArray array = new JSONArray();
        try {
            for (AnalysisInfo analysis : mInfoList) {
                if (isUriEquals(sendUri, analysis.getUri())) {
                    JSONObject jsonObject = new JSONObject(analysis.getData());
                    array.put(jsonObject);
                }
            }
            return array.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<String> getUris() {
        if (mInfoList.size() == 0) {
            return null;
        }
        List<String> uris = new ArrayList<String>();
        for (AnalysisInfo analysis : mInfoList) {
            if (!uris.contains(analysis.getUri())) {
                uris.add(analysis.getUri());
            }
        }
        return uris;
    }

    private boolean isUriEquals(String sendUri, String dataUri) {
        if (TextUtils.isEmpty(sendUri) && TextUtils.isEmpty(dataUri)) {
            return true;
        } else if (!TextUtils.isEmpty(sendUri) && sendUri.equals(dataUri)) {
            return true;
        }
        return false;
    }

    private ZLNetworkRequest getNetworkRequest(final String uri) {
        String sendUri = uri;
        if (TextUtils.isEmpty(sendUri)) {
            sendUri = mOperationUriBuilder.buildUriForAnalysis();
        }
        return new ZLNetworkRequest(sendUri, false) {
            @Override
            public void handleStream(InputStream inputStream, int length) throws IOException, ZLNetworkException {
                if (getResponseStatus(inputStream)) {
                    for (AnalysisInfo info : mInfoList) {
                        //删除已经发送的数据
                        if (isUriEquals(uri, info.getUri())) {
                            mAnalysisManager.deleteByID(info.getID());
                        }
                    }
                }
            }

            @Override
            public void doAfter(boolean success) throws ZLNetworkException {
            }
        };
    }

    private boolean getResponseStatus(InputStream inputStream) {
        String string = StringUtil.toString(inputStream);
        if (string != null) {
            try {
                JSONObject jsonObject = new JSONObject(string);
                int status = jsonObject.optInt(KEY_STATUS, INVALID);
                if (SUCCESS == status) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean isStop() {
        synchronized (mStop) {
            if (mStop) {
                return true;
            } else {
                return false;
            }
        }
    }

    public void stop() {
        synchronized (mStop) {
            if (!isStop()) {
                mStop = true;
            }
        }
    }

    public void resetStop() {
        synchronized (mStop) {
            if (isStop()) {
                mStop = false;
            }
        }
    }
}
