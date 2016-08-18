package com.sanqiwan.reader.engine;

import android.graphics.Bitmap;
import com.sanqiwan.reader.cache.SplashCache;
import com.sanqiwan.reader.model.Splash;
import com.sanqiwan.reader.preference.Settings;
import com.sanqiwan.reader.util.IOUtil;
import com.sanqiwan.reader.webservice.OperationWebService;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;
import org.geometerplus.zlibrary.core.network.ZLNetworkManager;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidLibrary;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 8/16/13
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class SplashManager {
    private static final String CACHE_PIC_NAME = "pic";
    OperationWebService mOperationWebService;

    public  SplashManager(){
        ZLAndroidLibrary.Instance();
        mOperationWebService = new OperationWebService();
    }

    public List<Splash> getSplashs(){
        return SplashCache.getInstance().getSplashs();
    }

    public void saveSplashs(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Splash> splashs = null;
                try {
                    splashs = mOperationWebService.getSplashs();
                } catch (ZLNetworkException e) {
                }
                if(splashs != null){
                    SplashCache.getInstance().setmSplashs(splashs);
                    clearPictureDir();
                    for(Splash splash:splashs){
                        downLoadImage(splash);
                    }
                }
            }
        }).start();
    }

    public Bitmap getBitmap(int id){
        return  SplashCache.getInstance().getBitmapById(id);
    }

    private void downLoadImage(Splash splash){
        File dir = Settings.getInstance().getmSplashPicDir();
        if(!dir.exists()){
            dir.mkdir();
            if(!dir.exists()){
                dir.mkdirs();
            }
        }
        File pic = new File(dir, CACHE_PIC_NAME+splash.getId());
        try {
            if(!pic.exists()){
                pic.createNewFile();
            }
            ZLNetworkManager.Instance().downloadToFile(splash.getPictureUrl(), pic);
        } catch (ZLNetworkException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void clearPictureDir(){
        File dir = Settings.getInstance().getmSplashPicDir();
        if(!dir.exists()){
            return;
        }
        IOUtil.deleteFile(dir);
    }
}
