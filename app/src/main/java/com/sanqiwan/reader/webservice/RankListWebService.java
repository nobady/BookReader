package com.sanqiwan.reader.webservice;

import com.sanqiwan.reader.alipay.HttpRequest;
import com.sanqiwan.reader.model.*;
import com.sanqiwan.reader.payment.NullPostDataException;
import com.sanqiwan.reader.webservice.parser.*;
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
 * Time: 10:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class RankListWebService {

    private UriBuilder mUriBuilder;
    private HttpRequester<BookItem> mHttpRequester;
    private HttpRequester<RankItem> mRankHttpRequester;


    public RankListWebService() {
        mUriBuilder = new UriBuilder(new WebServiceConfig());
    }


    public List<RankItem> getRankList() throws ZLNetworkException {
        String url = mUriBuilder.builderUriForGetRankList();
        mRankHttpRequester = new HttpRequester<RankItem>();
        return mRankHttpRequester.requestList(url, new RankItemParser());
    }

    public List<BookItem> getRankBookList(int rankId, int nowpage) throws ZLNetworkException {
        String url = mUriBuilder.builderUriForGetRankBookList(rankId, nowpage);
        mHttpRequester = new HttpRequester<BookItem>();
        List<BookItem> result = mHttpRequester.requestList(url, new BookItemParser());
        return result;
    }
}
