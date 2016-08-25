package com.sanqiwan.reader.net;

import com.sanqiwan.reader.AppContext;
import com.sanqiwan.reader.model.AccountResult;
import com.sanqiwan.reader.webservice.UriBuilder;
import com.sanqiwan.reader.webservice.WebServiceConfig;

import rx.Observable;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips] Created by tengfei.lv on 2016/8/25 17:22.
 */

public class NetRequestManager {

    public static Observable<AccountResult> login(String userName, String password){

        UriBuilder builder = new UriBuilder (new WebServiceConfig ());
        Observable<RxHttpResult<AccountResult>> observable = HttpMethod.getInstance (AppContext.getInstance ()).
                initRetrofit (builder.getBaseBuilder ().toString (), 0).
                create (WXInterfaceList.class).
                login (userName, password);
        return HttpMethod.getInstance (AppContext.getInstance ()).call (observable);
    }
}
