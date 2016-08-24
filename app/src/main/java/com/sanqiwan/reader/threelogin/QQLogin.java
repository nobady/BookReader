package com.sanqiwan.reader.threelogin;

import android.content.Context;

import com.sanqiwan.reader.ui.MainActivity;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips] Created by tengfei.lv on 2016/8/23 9:54.
 */

public class QQLogin implements ThreeLogin {

    private IUiListener listener;
    private Tencent tencent;

    public QQLogin (IUiListener listener) {
        this.listener = listener;
    }

    @Override
    public void startLogin (Context context) {
        tencent = Tencent.createInstance (LoginContants.QQ_LOGIN_APP_ID, context);

        if(!tencent.isSessionValid ()){
            tencent.login ((MainActivity) context,"all",listener);
        }
    }

    public Tencent getTencent () {
        return tencent;
    }
}
