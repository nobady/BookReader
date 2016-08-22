package com.sanqiwan.reader.threelogin;

import android.content.Context;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by ltf93 on 2016/8/22.
 */

public class WXLogin implements ThreeLogin {

    @Override
    public void startLogin(Context context) {
        IWXAPI wxapi = WXAPIFactory.createWXAPI(context, LoginContants.WX_LOGIN_APP_ID, true);

        wxapi.registerApp(LoginContants.WX_LOGIN_APP_ID);

        SendAuth.Req req = new SendAuth.Req();

        req.scope = "snsapi_userinfo";
        req.state = "wx_login";

        wxapi.sendReq(req);

    }
}
