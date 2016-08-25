package com.sanqiwan.reader.usercenter;

import com.sanqiwan.reader.engine.UserManager;
import com.sanqiwan.reader.model.AccountResult;
import com.sanqiwan.reader.usercenter.dataBridge.UserCenterDataBridge;
import com.sanqiwan.reader.usercenter.model.UserCenterModel;
import com.sanqiwan.reader.usercenter.model.UserCenterModelImpl;

import mvp.BasePresenter;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips] Created by tengfei.lv on 2016/8/25 15:09.
 */

public class UserCenterPresenter extends BasePresenter<UserCenterView> implements UserCenterDataBridge{

    private UserCenterModel userCenterModel;
    private UserManager userManager;

    public void doLogin (String userName, String password) {
        if(!isViewAttached ()){
            return;
        }
        userManager = UserManager.getInstance ();
        getView ().showLoadDialog (false);
        userCenterModel = new UserCenterModelImpl ();
        mCompositeSubscription.add (userCenterModel.login(userName,password,this));

    }

    @Override
    public void onError (Throwable e) {

    }

    @Override
    public void onCompleted () {

    }

    @Override
    public void onNext (AccountResult accountResult) {
        userManager.setAccountResult (accountResult);
        getView ().showLoginSuccess();
    }
}
