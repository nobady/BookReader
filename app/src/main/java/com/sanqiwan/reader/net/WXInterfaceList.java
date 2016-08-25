package com.sanqiwan.reader.net;

import com.sanqiwan.reader.model.AccountResult;
import com.sanqiwan.reader.webservice.UriBuilder;

import org.json.JSONObject;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips] Created by tengfei.lv on 2016/8/22 14:05.
 */

public interface WXInterfaceList {



    @GET
    Observable<RxHttpResult<JSONObject>> getWxinfo(@Query ("appid") String appId,
                                                   @Query ("secret") String secret,
                                                   @Query ("code") String code,
                                                   @Query ("grant_type") String grant);
    @GET
    Observable<RxHttpResult<JSONObject>> getWXUserInfo(@Query ("appid") String appId,
                                                       @Query ("access_token") String token);

    @GET(UriBuilder.ACTION_GET_LOGIN_RESULT)
    Observable<RxHttpResult<AccountResult>> login(@Query ("userName") String userName,
                                                  @Query ("userPwd") String pwd);
}
