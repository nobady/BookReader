package com.sanqiwan.reader.util;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.sanqiwan.reader.AppContext;

/**
 * Created by IBM on 14-2-24.
 */
public class PackageUtil {

    public static PackageInfo getPackageInfo() {
        PackageManager packageManager = AppContext.getInstance().getPackageManager();
        try {
            return packageManager.getPackageInfo(AppContext.getInstance().getPackageName(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
