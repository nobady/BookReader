package com.sanqiwan.reader.util;


import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import com.sanqiwan.reader.AppContext;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 8/16/13
 * Time: 11:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class UIUtil {

    private static volatile Handler sHandler;

    public static Handler getHandler() {

        if (sHandler == null) {
            synchronized (UIUtil.class) {
                if (sHandler == null) {
                    sHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return sHandler;
    }

    public static void runOnUiThread(Runnable runnable) {
        if (runnable != null) {
            getHandler().post(runnable);
        }
    }

    public static int dipToPixel(float dip) {
        DisplayMetrics metrics = AppContext.getInstance().getResources().getDisplayMetrics();
        // Get the screen's density scale
        final float scale = metrics.density;
        // Convert the dps to pixels, based on density scale
        return  (int) (dip * scale + 0.5f);
    }
}
