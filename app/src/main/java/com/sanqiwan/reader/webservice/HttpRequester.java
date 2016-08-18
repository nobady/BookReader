package com.sanqiwan.reader.webservice;

import org.geometerplus.fbreader.network.NetworkException;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;
import org.geometerplus.zlibrary.core.network.ZLNetworkManager;
import org.geometerplus.zlibrary.core.network.ZLNetworkRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/25/13
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class HttpRequester<T> {

    public T request(String url, final IParser<T> parser) throws ZLNetworkException {

        final HttpResult<T> httpResult = new HttpResult<T>();

        ZLNetworkRequest networkRequest = new ZLNetworkRequest(url) {
            @Override
            public void handleStream(InputStream inputStream, int length) throws IOException, ZLNetworkException {
                T result = parser.parse(inputStream);
                httpResult.setResult(result);
            }
        };

        ZLNetworkManager.Instance().perform(networkRequest);

        return httpResult.getResult();
    }

    public List<T> requestList(String url, final IParser<T> parser) throws ZLNetworkException {

        final HttpResult<List<T>> httpResult = new HttpResult<List<T>>();

        ZLNetworkRequest networkRequest = new ZLNetworkRequest(url) {
            @Override
            public void handleStream(InputStream inputStream, int length) throws IOException, ZLNetworkException {
                List<T> result = parser.parseList(inputStream);
                httpResult.setResult(result);
            }
        };

        ZLNetworkManager.Instance().perform(networkRequest);

        return httpResult.getResult();
    }
}
