package com.sanqiwan.reader.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.data.XBookManager;
import com.sanqiwan.reader.engine.SplashManager;
import com.sanqiwan.reader.model.Splash;
import com.sanqiwan.reader.preference.Settings;
import com.sanqiwan.reader.track.Tracker;
import com.sanqiwan.reader.util.ManifestUtil;
import com.sanqiwan.reader.util.PackageUtil;
import com.sanqiwan.reader.util.UIUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: lenovo
 * Date: 13-7-22
 * Time: 下午4:58
 * To change this template use File | Settings | File Templates.
 */
public class LauncherActivity extends BaseActivity implements View.OnClickListener {

    public static int sScreenWidth = 0;
    public static int sScreenHeight = 0;
    private ImageView mImageView;
    private Intent mIntent;
    private TextView mTextView;
    private List<Drawable> mdrawable = new ArrayList<Drawable>();
    private Resources mres;
    private ImageView mBackgroundImageView;
    private List<Splash> mSplashList;
    private SplashManager mSplashManager;
    private Timer mTimer;
    private static final int DURATION = 3000;
    private static final int SPLASH_DURATION = 2000;
    private int mNow = 0;
    private float mStartX, mEndX;
    private ImageView mTopIcomImageView;
    private ImageView mSplashCenter;
    private XBookManager mXBookManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadanimation);
        mIntent = getIntent();
        mImageView = (ImageView) findViewById(R.id.mImage);
        mTextView = (TextView) findViewById(R.id.splashtext);
        mBackgroundImageView = (ImageView) findViewById(R.id.iv_splash_bg);
        mTopIcomImageView = (ImageView) findViewById(R.id.logo_top);
        mSplashCenter = (ImageView) findViewById(R.id.splash_center);
        mres = getResources();

        checkBookUpdate();
        loadImage();
        mImageView.setOnClickListener(this);
    }

    private void loadImage() {
        mSplashManager = new SplashManager();
        mSplashList = mSplashManager.getSplashs();
        if (mSplashList != null) {
            for (Splash splash : mSplashList) {
                Bitmap bitmap = mSplashManager.getBitmap(splash.getId());
                if (bitmap != null) {
                    mdrawable.add(new BitmapDrawable(mres, bitmap));
                } else {
                    mdrawable.add(mres.getDrawable(R.drawable.lx1));
                    mdrawable.add(mres.getDrawable(R.drawable.lx2));
                    mdrawable.add(mres.getDrawable(R.drawable.lx3));
                    break;
                }
            }
        } else {
            mdrawable.add(mres.getDrawable(R.drawable.lx1));
            mdrawable.add(mres.getDrawable(R.drawable.lx2));
            mdrawable.add(mres.getDrawable(R.drawable.lx3));
            mSplashList = new ArrayList<Splash>();
            Splash splash = new Splash();
            splash.setDescribeText(getString(R.string.splash_description_1));
            mSplashList.add(splash);
            splash = new Splash();
            splash.setDescribeText(getString(R.string.splash_description_2));
            mSplashList.add(splash);
            splash = new Splash();
            splash.setDescribeText(getString(R.string.splash_description_3));
            mSplashList.add(splash);
        }

        showAnimation();
        mSplashManager.saveSplashs();
    }

    private void showAnimation() {
        mTimer = new Timer();
        mNow = 0;
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mSplashList != null) {

                            int splashSize = mdrawable.size() >= mSplashList.size() ? mSplashList.size() : mdrawable.size();
                            if (splashSize == 0) {
                                return;
                            }
                            int currentPosition = mNow % splashSize;
                            mTextView.setText(mSplashList.get(currentPosition).getDescribeText());
                            mBackgroundImageView.setImageDrawable(mdrawable.get(currentPosition));
                            if(mImageView.getVisibility() == View.GONE){
                                mImageView.setVisibility(View.VISIBLE);
                                mTextView.setVisibility(View.VISIBLE);
                                mSplashCenter.setVisibility(View.GONE);
                            }
                            Animation alphaAnimation = new AlphaAnimation(0.2f,1.0f);
                            alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                            alphaAnimation.setDuration(1000);
                            mBackgroundImageView.startAnimation(alphaAnimation);
                        }
                    }
                });
                mNow++;
            }
        }, SPLASH_DURATION, DURATION);

    }

    @Override
    public void onClick(View view) {
        trackAppActivated(); //统计激活
        gotoMainActivity();
    }

    private void trackAppActivated() {
        if (Settings.getInstance().getVersionCode() == 0) {
            Settings.setVersionCode(ManifestUtil.getVersion());
            if (isFirstInstall()) {
                Tracker.getInstance().trackAppActivated();
            }
        }
    }

    private boolean isFirstInstall() {
        PackageInfo packageInfo = PackageUtil.getPackageInfo();
        if (packageInfo == null) {
            return false;
        }
        if (packageInfo.firstInstallTime == packageInfo.lastUpdateTime) {
            return true;
        }
        return false;
    }

    private void gotoMainActivity() {
        //rocketAnimation.stop();
        if (mTimer != null) {
            mTimer.cancel();
        }
        MainActivity.startMainActivity(LauncherActivity.this);
        this.finish();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                mEndX = event.getX();
                if (mStartX - mEndX > 0) {
                    trackAppActivated(); //统计激活
                    gotoMainActivity();
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //获取小说书库按钮长度和宽度，并计算小说书库按钮中心坐标，提供给动画终止使用。
        UIUtil.getHandler().post(new Runnable() {  //添加到消息队列，在绘制图形之后，立即执行
            @Override
            public void run() {
                DisplayMetrics metric = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metric);
                sScreenWidth = metric.widthPixels;
                sScreenHeight = metric.heightPixels;
            }
        });

    }

    private void checkBookUpdate(){
        mXBookManager = new XBookManager();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mXBookManager.checkBooksUpdate();
            }
        }).start();
    }
}