package com.sanqiwan.reader.ui;

import android.app.Activity;
import com.umeng.analytics.MobclickAgent;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-8-9
 * Time: 上午11:00
 * To change this template use File | Settings | File Templates.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}