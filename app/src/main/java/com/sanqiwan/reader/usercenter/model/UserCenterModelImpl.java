package com.sanqiwan.reader.usercenter.model;

import com.sanqiwan.reader.AppContext;
import com.sanqiwan.reader.model.AccountResult;
import com.sanqiwan.reader.net.HttpMethod;
import com.sanqiwan.reader.net.RxHttpResult;
import com.sanqiwan.reader.net.WXInterfaceList;
import com.sanqiwan.reader.usercenter.dataBridge.UserCenterDataBridge;
import com.sanqiwan.reader.webservice.UriBuilder;
import com.sanqiwan.reader.webservice.WebServiceConfig;

import mvp.BaseDataBridge;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

/**
 * Created by ltf93 on 2016/8/25.
 */
public class UserCenterModelImpl implements UserCenterModel {
    @Override
    public Subscription login(String userName, String password, final BaseDataBridge bridge) {
        final UserCenterDataBridge dataBridge = (UserCenterDataBridge) bridge;
        Observable<RxHttpResult<AccountResult>> observable = HttpMethod.getInstance(AppContext.getInstance()).
                initRetrofit(WebServiceConfig.SERVER_HOST, 0)
                .create(WXInterfaceList.class).login(userName, password);
        return HttpMethod.getInstance(AppContext.getInstance()).call(observable).subscribe(new Observer<AccountResult>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
                dataBridge.onError(e);
            }
            @Override
            public void onNext(AccountResult accountResult) {
                dataBridge.onNext(accountResult);
            }
        });
    }
}
