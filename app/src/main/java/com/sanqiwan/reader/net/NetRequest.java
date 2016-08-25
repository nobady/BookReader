package com.sanqiwan.reader.net;

import android.content.Context;

import com.sanqiwan.reader.threelogin.LoginContants;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips] Created by tengfei.lv on 2016/8/25 11:16.
 */

public class NetRequest {

    private CompositeSubscription mCompositeSubscription;

    private Context mContext;
    public NetRequest (Context context) {
        mContext = context;
        mCompositeSubscription = new CompositeSubscription ();
    }

    public void getWxInfo(String code){
        final WXInterfaceList interfaceList = HttpMethod.getInstance (mContext).
                initRetrofit (LoginContants.WX_LOGIN_TOKEN_URL,1).
                create (WXInterfaceList.class);

        Subscription subscribe = HttpMethod.getInstance (mContext).
                call (interfaceList.getWxinfo (LoginContants.WX_LOGIN_APP_ID, LoginContants.WX_LOGIN_SECRET, code, "authorization_code")).subscribe (new Subscriber<JSONObject> () {
            @Override
            public void onCompleted () {
            }

            @Override
            public void onError (Throwable e) {
            }

            @Override
            public void onNext (JSONObject jsonObject) {
                try {
                    //发送请求获取用户信息
                    String access_token = jsonObject.getString ("access_token");
                    String openid = jsonObject.getString ("openid");
                    HttpMethod.getInstance (mContext).
                            call (interfaceList.getWXUserInfo (openid,access_token)).subscribe (new Subscriber<JSONObject> () {
                        @Override
                        public void onCompleted () {
                        }
                        @Override
                        public void onError (Throwable e) {
                        }
                        @Override
                        public void onNext (JSONObject jsonObject) {
                            //获取unionid和nickname
                            try {
                                String unionid = jsonObject.getString ("unionid");
                                String nickname = jsonObject.getString ("nickname");
                                //在发送给服务器端
                            } catch (JSONException e) {
                                e.printStackTrace ();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace ();
                }
            }
        });

        mCompositeSubscription.add (subscribe);

    }
}
