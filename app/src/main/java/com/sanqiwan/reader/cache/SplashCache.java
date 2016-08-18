package com.sanqiwan.reader.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.sanqiwan.reader.AppContext;
import com.sanqiwan.reader.model.Splash;
import com.sanqiwan.reader.preference.Settings;
import com.sanqiwan.reader.util.BitmapUtil;
import com.sanqiwan.reader.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.List;

/**
 * User: sam
 * Date: 8/16/13
 * Time: 11:58 AM
 */
public class SplashCache {
    private static final String CACHE_FILE_NAME = "splashcaches";
    private static final String CACHE_PIC_NAME = "pic";
    private static volatile SplashCache sInstance;

    private List<Splash> mSplashs;

    private SplashCache() {
        mSplashs = loadCacheSplashFromFile();
    }

    public static SplashCache getInstance() {

        if (sInstance == null) {
            synchronized (SplashCache.class) {
                if (sInstance == null) {
                    sInstance = new SplashCache();
                }
            }
        }
        return sInstance;
    }

    public List<Splash> getSplashs() {
        if (mSplashs == null) {
            mSplashs = loadCacheSplashFromFile();
        }
        return mSplashs;
    }

    public void setmSplashs(List<Splash> splashs) {
        if (splashs == null) {
            return;
        }
        mSplashs = splashs;
        saveSplashCacheToFile();
    }

    private List<Splash> loadCacheSplashFromFile() {
        File cacheFile = getCacheSplashFile();
        if (!cacheFile.exists()) {
            return null;
        }
        String jsonString = StringUtil.loadStringFromFile(cacheFile);
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            return Splash.fromJSONArray(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveSplashCacheToFile() {
        if (mSplashs == null || mSplashs.isEmpty()) {
            return;
        }

        File recordFile = getCacheSplashFile();
        String jsonArrayString = Splash.toJSONArrayString(mSplashs);
        StringUtil.saveStringToFile(jsonArrayString, recordFile);
    }

    private File getCacheSplashFile() {
        File dir = Settings.getInstance().getSplashCacheDir();
        return new File(dir, CACHE_FILE_NAME);
    }

    private File getCachePictureFileById(int id) {
        File dir = Settings.getInstance().getmSplashPicDir();
        File pic = new File(dir, CACHE_PIC_NAME + id);
        return pic.exists() ? pic : null;
    }


    public Bitmap getBitmapById(int id) {
        File file = getCachePictureFileById(id);
        if (file == null || !file.exists()) {
            return null;
        }
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) AppContext.getInstance().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        int targetW = metric.widthPixels;
        int targetH = metric.heightPixels;
        return BitmapUtil.decodeSampledBitmapFromResource(file.getPath(), targetW, targetH);
    }

}
