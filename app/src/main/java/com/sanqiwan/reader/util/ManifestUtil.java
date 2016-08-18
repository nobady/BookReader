package com.sanqiwan.reader.util;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.sanqiwan.reader.AppContext;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-12-11
 * Time: 下午12:30
 * To change this template use File | Settings | File Templates.
 */
public class ManifestUtil {

    private static final String DEFAULT = "test";
    private static final String KEY_CHANNEL = "UMENG_CHANNEL";
    private static int mVersionCode;

    public static String getChannel() {
        try {
            ApplicationInfo appInfo = AppContext.getInstance().getPackageManager().getApplicationInfo(
                    AppContext.getInstance().getPackageName(), PackageManager.GET_META_DATA);
            String channel = appInfo.metaData.getString(KEY_CHANNEL);
            if (channel == null) {
                return DEFAULT;
            }
            return channel;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return DEFAULT;
    }

    public static int getVersion() {
        try {
            if (mVersionCode == 0) {
                PackageManager manager = AppContext.getInstance().getPackageManager();
                PackageInfo info = manager.getPackageInfo(AppContext.getInstance().getPackageName(), 0);
                mVersionCode = info.versionCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mVersionCode;
    }
}
