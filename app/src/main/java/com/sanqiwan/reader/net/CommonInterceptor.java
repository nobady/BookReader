package com.sanqiwan.reader.net;

import com.sanqiwan.reader.util.SecurityUtil;
import com.sanqiwan.reader.webservice.UriBuilder;
import com.sanqiwan.reader.webservice.WebServiceConfig;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.sanqiwan.reader.webservice.UriBuilder.KEY_QTIME;
import static com.sanqiwan.reader.webservice.UriBuilder.KEY_SIGN;
import static com.sanqiwan.reader.webservice.UriBuilder.KEY_SPID;

/**
 * Created by ltf93 on 2016/8/25.
 */

public class CommonInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {

        Request oldRequest = chain.request();

        // 添加新的参数
        String date = getDate();
        String sign = getSign(date);
        HttpUrl.Builder authorizedUrlBuilder = oldRequest.url()
                .newBuilder()
                .scheme(oldRequest.url().scheme())
                .host(oldRequest.url().host())
                .addQueryParameter(UriBuilder.KEY_SIGN, sign)
                .addQueryParameter(UriBuilder.KEY_QTIME, date)
                .addQueryParameter(UriBuilder.KEY_SPID, WebServiceConfig.SPID);

        // 新的请求
        Request newRequest = oldRequest.newBuilder()
                .method(oldRequest.method(), oldRequest.body())
                .url(authorizedUrlBuilder.build())
                .build();

        return chain.proceed(newRequest);
    }

    private String getSign(String date) {
        String sign = SecurityUtil.md5(WebServiceConfig.SPID + WebServiceConfig.SECRET_KEY + date);
        sign = SecurityUtil.md5(sign);
        return sign;
    }

    private String getDate() {
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(new Date());
    }
}
