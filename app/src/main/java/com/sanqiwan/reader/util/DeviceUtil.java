package com.sanqiwan.reader.util;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.sanqiwan.reader.AppContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 9/24/13
 * Time: 4:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeviceUtil {

    private static int SYSTEM_DEFAULT_HEIGHT = 25;
    private static String sDeviceId;
    private static int sDeviceWidth;
    private static int sDeviceHeight;
    private static String sModel;
    private static String sOsVersion;
    private static String sSimOperator;
    private static float sDensity;
    private static String sIMEI;
    private static String sMAC;

    public static String getDeviceId() {
        if (TextUtils.isEmpty(sDeviceId)) {
            String deviceId = getIMEI();
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = getMAC();
                if (deviceId == null) {
                    deviceId = StringUtil.EMPTY_STRING;
                }
            }
            sDeviceId = SecurityUtil.md5(SecurityUtil.md5(deviceId));
        }
        return sDeviceId;
    }

    public static int getDeviceWidth() {
        if (sDeviceWidth == 0) {
            WindowManager wm = (WindowManager) AppContext.getInstance().getSystemService(AppContext.getInstance().WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            sDeviceWidth = display.getWidth();
        }
        return sDeviceWidth;
    }

    public static int getDeviceHeight() {
        if (sDeviceHeight == 0) {
            WindowManager wm = (WindowManager) AppContext.getInstance().getSystemService(AppContext.getInstance().WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            sDeviceHeight = display.getHeight();
        }
        return sDeviceHeight;
    }

    public static String getModel() {
        if (TextUtils.isEmpty(sModel)) {
            sModel = android.os.Build.MODEL;
        }
        return sModel;
    }

    public static String getOsVersion() {
        if (TextUtils.isEmpty(sOsVersion)) {
            sOsVersion = android.os.Build.VERSION.RELEASE;
        }
        return sOsVersion;
    }

    public static String getSimOperator() {
        if (TextUtils.isEmpty(sSimOperator)) {
            TelephonyManager telManager = (TelephonyManager) AppContext.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            sSimOperator = telManager.getSimOperator();
        }
        return sSimOperator;
    }

    public static float getDensity() {
        if (sDensity < 0.5) {
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager wm = (WindowManager) AppContext.getInstance().getSystemService(AppContext.getInstance().WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            display.getMetrics(dm);
            sDensity = dm.density;
        }
        return sDensity;
    }

    public static String getIMEI() {
        if (TextUtils.isEmpty(sIMEI)) {
            final TelephonyManager tm = (TelephonyManager) AppContext.getInstance().getSystemService(AppContext.getInstance().TELEPHONY_SERVICE);
            sIMEI = tm.getDeviceId();
            if (TextUtils.isEmpty(sIMEI)) {
                sIMEI = StringUtil.EMPTY_STRING;
            } else {
                sIMEI = sIMEI.toLowerCase();
            }
        }
        return sIMEI;
    }

    public static String getMAC() {
        if (TextUtils.isEmpty(sMAC)) {
            WifiManager wm = (WifiManager)AppContext.getInstance().getSystemService(Context.WIFI_SERVICE);
            if (wm.getConnectionInfo() == null) {
                return StringUtil.EMPTY_STRING;
            }
            sMAC = wm.getConnectionInfo().getMacAddress();
            if (TextUtils.isEmpty(sMAC)) {
                sMAC = StringUtil.EMPTY_STRING;
            } else {
                sMAC = sMAC.toLowerCase();
            }
        }
        return sMAC;
    }

    // 由于targetSdkVersion低于17，只能通过反射获取

    /**
     * 反射方式获得UserSerial
     * @param context
     * @return
     */
    public static String getUserSerial(Context context)
    {
        Object userManager = context.getSystemService("user");
        if (userManager == null)
        {
            return null;
        }

        try
        {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);

            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);
            return String.valueOf(userSerial);
        }
        catch (NoSuchMethodException e)
        {
        }
        catch (IllegalArgumentException e)
        {
        }
        catch (IllegalAccessException e)
        {
        }
        catch (InvocationTargetException e)
        {
        }

        return null;
    }

    public static int getStatusBarHeight() {
        int statusBarHeight = UIUtil.dipToPixel(SYSTEM_DEFAULT_HEIGHT);
        int resourceId = AppContext.getInstance().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = AppContext.getInstance().getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }
}


