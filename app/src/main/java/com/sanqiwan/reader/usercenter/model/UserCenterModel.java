package com.sanqiwan.reader.usercenter.model;

import mvp.BaseDataBridge;
import mvp.BaseModel;
import rx.Subscription;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips] Created by tengfei.lv on 2016/8/25 15:53.
 */

public interface UserCenterModel extends BaseModel {

    Subscription login (String userName, String password,
                        BaseDataBridge bridge);
}
