package com.sanqiwan.reader.ui;

import android.support.v4.app.FragmentActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-8-9
 * Time: 上午11:00
 * To change this template use File | Settings | File Templates.
 */
public class BaseFragmentActivity extends FragmentActivity {

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