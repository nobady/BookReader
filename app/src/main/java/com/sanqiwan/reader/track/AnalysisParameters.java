package com.sanqiwan.reader.track;

import android.text.TextUtils;
import com.sanqiwan.reader.util.DateUtil;
import com.sanqiwan.reader.util.DeviceUtil;
import com.sanqiwan.reader.util.ManifestUtil;
import com.sanqiwan.reader.util.SecurityUtil;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: jwb
 * Date: 13-12-6
 * Time: 上午10:52
 * To change this template use File | Settings | File Templates.
 */
public class AnalysisParameters {

    private String mModel;
    private String mOs;
    private String mOsVersion;
    private String mDisplaySize;
    private String mSimOperator;
    private String mChannel;
    private String mImei;
    private String mMac;
    private int mVersionCode;

    private static final String CHANNEL = "channel";

    private static final String MODEL = "model";
    private static final String OS = "os";
    private static final String OS_VERSION = "os_version";
    private static final String DISPLAY = "display";
    private static final String TIME = "time";
    private static final String OP = "op";
    private static final String IMEI = "imei";
    private static final String MAC = "mac";
    private static final String APP_VER = "app_ver";
    private static final String KEY_SIGN = "sign";

    private static AnalysisParameters sInstance;

    public static AnalysisParameters getInstance() {
        if (sInstance == null) {
            synchronized (AnalysisParameters.class) {
                if (sInstance == null) {
                    sInstance = new AnalysisParameters();
                }
            }
        }
        return sInstance;
    }

    private AnalysisParameters() {
        mModel = DeviceUtil.getModel();
        mOs = "android";
        mOsVersion = DeviceUtil.getOsVersion();
        mDisplaySize = String.valueOf(DeviceUtil.getDeviceWidth()) + "x" + String.valueOf(DeviceUtil.getDeviceHeight());
        mSimOperator = DeviceUtil.getSimOperator();
        mChannel = ManifestUtil.getChannel();
        mImei = DeviceUtil.getIMEI();
        mMac = DeviceUtil.getMAC();
        mVersionCode = ManifestUtil.getVersion();
    }

    public void toJsonObject(JSONObject jsonObject) {
        try {
            jsonObject.put(MODEL, mModel);
            jsonObject.put(OS, mOs);
            jsonObject.put(OS_VERSION, mOsVersion);
            jsonObject.put(DISPLAY, mDisplaySize);
            jsonObject.put(OP, mSimOperator);
            jsonObject.put(CHANNEL, mChannel);
            jsonObject.put(IMEI, mImei);
            jsonObject.put(MAC, mMac);
            jsonObject.put(APP_VER, mVersionCode);
            long time = DateUtil.getUnixTime();
            jsonObject.put(TIME, time);
            jsonObject.put(KEY_SIGN, getSign(time));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // md5(md5(imei + mac + os + os_version
    // + display + op + channel + app_ver + time))
    private String getSign(long time) {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(mImei)) {
            sb.append(mImei);
        }
        if (!TextUtils.isEmpty(mMac)) {
            sb.append(mMac);
        }
        if (!TextUtils.isEmpty(mOs)) {
            sb.append(mOs);
        }
        if (!TextUtils.isEmpty(mOsVersion)) {
            sb.append(mOsVersion);
        }
        if (!TextUtils.isEmpty(mDisplaySize)) {
            sb.append(mDisplaySize);
        }
        if (!TextUtils.isEmpty(mSimOperator)) {
            sb.append(mSimOperator);
        }
        if (!TextUtils.isEmpty(mChannel)) {
            sb.append(mChannel);
        }
        sb.append(mVersionCode);
        sb.append(time);
        return SecurityUtil.md5(SecurityUtil.md5(sb.toString()));
    }
}
