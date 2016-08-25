package com.sanqiwan.reader.usercenter.dataBridge;

import com.sanqiwan.reader.model.AccountResult;

import mvp.BaseDataBridge;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips] Created by tengfei.lv on 2016/8/25 17:17.
 */

public interface UserCenterDataBridge extends BaseDataBridge{
    void onNext (AccountResult accountResult);
}
