package com.sanqiwan.reader.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Environment;
import com.sanqiwan.reader.AppContext;
import com.sanqiwan.reader.engine.ThemeProfile;
import com.sanqiwan.reader.model.UserInfo;
import com.sanqiwan.reader.receiver.WakeUpHelper;
import com.sanqiwan.reader.util.DateUtil;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/22/13
 * Time: 12:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class Settings {
    private static final boolean DEFAULT_PAGING_READING = false;  //屏蔽左右翻页,只有上下翻页

    private static final String CHAPTERS = "chapters";
    private static final String TOCS = "tocs";
    private static final String DOWNLOAD = "download";
    private static final String SPLASHS = "splash";
    private static final String PIC_DIR = "picture";
    private static final String HEAD_PICTURE_REFRESH_TIME = "head_picture_refresh_time";
    private static final String FILE_NAME = "reader_settings";
    private static final String USER_ID = "user_id";
    private static final String USER_NAME = "user_name";
    private static final String USER_AVATAR = "user_avatar";
    private static final String USER_PHONE = "user_phone";
    private static final String USER_EMAIL = "user_email";
    private static final String USER_LEVEL = "user_level";
    private static final String USER_POINT = "user_point";
    private static final String USER_MONEY = "user_money";
    private static final String SHELF_SHOW_MODE = "shelf_show_mode";
    private static final String READER_BACKGROUND_LIGHT_ALWAYS = "background_light_always";
    private static final String READER_FONT_SIZE = "reader_font_size";
    private static final String READER_ORIENTATION = "reader_orientation";
    private static final String READER_VOLUME_PAGING = "reader_volume_paging";
    private static final String READER_BRIGHTNESS = "reader_brightness";
    private static final String READER_READ_MODE = "reader_read_mode";
    private static final String READER_THEME = "reader_theme";
    private static final String FIRST_READING = "first_reading";
    private static final String WAKE_UP_TIME = "wake_up_time";
    private static final String ELAPSED_REAL_TIME = "elapsed_real_time";

    public static final int SHELF_SHOW_MODE_GRID = 0;
    public static final int SHELF_SHOW_MODE_LIST = 1;
    private static volatile Settings sInstance;
    public static final float DEFAULT_READER_FONT_SIZE = 20;

    private static final String IMAGE_CACHE = "image_cache";
    private static final String VERSION_CODE = "version_code";

    private Context mContext;
    private File mCacheDir;
    private File mChapterCacheDir;
    private File mTOCCacheDir;
    private File mSplashCacheDir;
    private File mSplashPicDir;
    private File mImageCacheDir;

    private Settings(Context context) {
        mContext = context;
    }

    public static Settings getInstance() {
        if (sInstance == null) {
            synchronized (Settings.class) {
                if (sInstance == null) {
                    sInstance = new Settings(AppContext.getInstance());
                }
            }
        }
        return sInstance;
    }

    public File getImageCacheDir() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return new File(Environment.getExternalStorageDirectory(), IMAGE_CACHE);
        } else {
            return new File(mContext.getCacheDir(), IMAGE_CACHE);
        }
    }

    public File getCacheDir() {
        if (mCacheDir == null) {
            mCacheDir = mContext.getCacheDir();
        }
        return mCacheDir;
    }

    public File getChapterCacheDir() {
        if (mChapterCacheDir == null) {
            mChapterCacheDir = new File(getCacheDir(), CHAPTERS);
        }
        return mChapterCacheDir;
    }

    public File getTOCCacheDir() {
        if (mTOCCacheDir == null) {
            mTOCCacheDir = new File(getCacheDir(), TOCS);
        }
        return mTOCCacheDir;
    }

    public File getDownloadDir() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return new File(Environment.getExternalStorageDirectory(), DOWNLOAD);
        } else {
            return new File(mContext.getFilesDir(), DOWNLOAD);
        }
    }

    public File getBookDownloadFile(long bookId) {
        return new File(getDownloadDir(), String.valueOf(bookId));
    }

    public File getSplashCacheDir() {
        if (mSplashCacheDir == null) {
            mSplashCacheDir = new File(getCacheDir(), SPLASHS);
        }
        return mSplashCacheDir;
    }

    public File getmSplashPicDir() {
        if (mSplashPicDir == null) {
            mSplashPicDir = new File(getSplashCacheDir(), PIC_DIR);
        }
        return mSplashPicDir;
    }

    public long getHeadPictureRefreshTime() {
        SharedPreferences sp = getPreference();
        long lastRefresh = sp.getLong(HEAD_PICTURE_REFRESH_TIME, 0);
        return lastRefresh;
    }

    public void setHeadPictureRefreshTime(long time) {
        SharedPreferences.Editor editor = getPreference().edit();
        editor.putLong(HEAD_PICTURE_REFRESH_TIME, time).commit();
    }

    public int getShelfShowMode() {
        SharedPreferences sp = getPreference();
        return sp.getInt(SHELF_SHOW_MODE, SHELF_SHOW_MODE_GRID);
    }

    public void setShelfShowMode(int mode) {
        SharedPreferences.Editor editor = getPreference().edit();
        editor.putInt(SHELF_SHOW_MODE, mode).commit();
    }

    private SharedPreferences getPreference() {
        return mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public void setUserId(int id) {
        SharedPreferences.Editor editor = getPreference().edit();
        editor.putInt(USER_ID, id).commit();
    }

    public int getUserId() {
        SharedPreferences sp = getPreference();
        return sp.getInt(USER_ID, 0);
    }

    public void setUserInfo(UserInfo userInfo) {
        if (userInfo == null) {
            return;
        }
        SharedPreferences.Editor editor = getPreference().edit();
        if (userInfo.getUserName() != null) {
            editor.putString(USER_NAME, userInfo.getUserName());
        }
        if (userInfo.getUid() > 0) {
            editor.putInt(USER_ID, userInfo.getUid());
        }
        if (userInfo.getAvatar() != null) {
            editor.putString(USER_AVATAR, userInfo.getAvatar());
        }
        if (userInfo.getEmail() != null) {
            editor.putString(USER_EMAIL, userInfo.getEmail());
        }
        if (userInfo.getPhoneNumber() != null) {
            editor.putString(USER_PHONE, userInfo.getPhoneNumber());
        }
        if (userInfo.getLevel() != null) {
            editor.putString(USER_LEVEL, userInfo.getLevel());
        }
        if (userInfo.getUserPoint() >= 0) {
            editor.putFloat(USER_POINT, userInfo.getUserPoint());
        }
        if (userInfo.getUserMoney() >= 0) {
            editor.putInt(USER_MONEY, userInfo.getUserMoney());
        }
        editor.commit();

    }

    public UserInfo getUserInfo() {
        UserInfo u = new UserInfo();
        SharedPreferences sp = getPreference();
        u.setUid(sp.getInt(USER_ID, 0));
        u.setUserName(sp.getString(USER_NAME, null));
        u.setAvatar(sp.getString(USER_AVATAR, null));
        u.setEmail(sp.getString(USER_EMAIL, null));
        u.setPhoneNumber(sp.getString(USER_PHONE, null));
        u.setLevel(sp.getString(USER_LEVEL, null));
        u.setUserMoney(sp.getInt(USER_MONEY, 0));
        u.setUserPoint(sp.getFloat(USER_POINT, 0));
        return u;
    }

    public void removeUserInfo() {
        SharedPreferences.Editor editor = getPreference().edit();
        editor.remove(USER_ID);
        editor.remove(USER_NAME);
        editor.remove(USER_AVATAR);
        editor.remove(USER_EMAIL);
        editor.remove(USER_PHONE);
        editor.remove(USER_LEVEL);
        editor.remove(USER_MONEY);
        editor.remove(USER_POINT);
        editor.commit();
    }

    public void setReaderBrightness(int brightness) {
        SharedPreferences.Editor editor = getPreference().edit();
        editor.putInt(READER_BRIGHTNESS, brightness).commit();
    }

    public int getReaderBrightness() {
        return getPreference().getInt(READER_BRIGHTNESS, 30);
    }

    public void setReaderVolumePaging(boolean use) {
        SharedPreferences.Editor editor = getPreference().edit();
        editor.putBoolean(READER_VOLUME_PAGING, use).commit();
    }

    public boolean isReaderVolumePaging() {
        return getPreference().getBoolean(READER_VOLUME_PAGING, false);
    }

    public void setReaderBackgroundLightAlways(boolean alwaysOn) {
        SharedPreferences.Editor editor = getPreference().edit();
        editor.putBoolean(READER_BACKGROUND_LIGHT_ALWAYS, alwaysOn).commit();
    }

    public boolean isReaderBackgroundLightAlways() {
        return getPreference().getBoolean(READER_BACKGROUND_LIGHT_ALWAYS, false);
    }

    public void setReaderOrientation(int orientation) {
        SharedPreferences.Editor editor = getPreference().edit();
        editor.putInt(READER_ORIENTATION, orientation).commit();
    }

    public int getReaderOrientation() {
        return getPreference().getInt(READER_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void setReaderFontSize(float fontSize) {
        SharedPreferences.Editor editor = getPreference().edit();
        editor.putFloat(READER_FONT_SIZE, fontSize).commit();
    }

    public float getReaderFontSize() {
        return getPreference().getFloat(READER_FONT_SIZE, DEFAULT_READER_FONT_SIZE);
    }

    public void setReaderPageMode(boolean isPageMode) {
        SharedPreferences.Editor editor = getPreference().edit();
        editor.putBoolean(READER_READ_MODE, isPageMode).commit();
    }

    public boolean isReaderPageMode() {
        return getPreference().getBoolean(READER_READ_MODE, DEFAULT_PAGING_READING);
    }

    public void setReaderTheme(String styleName) {
        SharedPreferences.Editor editor = getPreference().edit();
        editor.putString(READER_THEME, styleName).commit();
    }

    public String getReaderTheme() {
        return getPreference().getString(READER_THEME, ThemeProfile.DAY);
    }

    public boolean isFirstReading() {
        return getPreference().getBoolean(FIRST_READING, true);
    }

    public void setFirstReading(boolean isFirstReading) {
        SharedPreferences.Editor editor = getPreference().edit();
        editor.putBoolean(FIRST_READING, isFirstReading).commit();
    }

    public static void setVersionCode(int versionCode) {
        SharedPreferences sharedPreferences = AppContext.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(VERSION_CODE, versionCode);
        editor.commit();
    }

    public static int getVersionCode() {
        SharedPreferences sharedPreferences = AppContext.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(VERSION_CODE, 0);
    }

    public static void setWakeUpTime(long date) {
        SharedPreferences sharedPreferences = AppContext.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(WAKE_UP_TIME, date);
        editor.commit();
    }

    public static long getWakeUpTime() {
        long defaultTime = WakeUpHelper.getWakeUpTime();//默认是7天后唤醒。
        SharedPreferences sharedPreferences = AppContext.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(WAKE_UP_TIME, defaultTime);
    }

    public static void setElapsedRealTime() {
        SharedPreferences sharedPreferences = AppContext.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(ELAPSED_REAL_TIME, getDefaultElapsedRealTime());//当前手机开机时长+30天，正常情况下，手机重启后，用户在30天内都不连接网络的可能性很小。
        editor.commit();
    }

    public static long getElapsedRealTime() {
        SharedPreferences sharedPreferences = AppContext.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(ELAPSED_REAL_TIME, getDefaultElapsedRealTime());
    }

    private static long getDefaultElapsedRealTime() {
        long intervalTime = 2592000000l;//30天时长,1000 * 60 * 60 * 24 * 30
        long defaultTime = DateUtil.getElapsedRealTime() + intervalTime; //这两个数相加后可能溢出long的最大范围，那就成为负数，重启手机就不会发送通知。
        if (defaultTime < Long.MAX_VALUE && defaultTime > 0) {
            return defaultTime;
        } else {
            return Long.MAX_VALUE;
        }
    }

}
