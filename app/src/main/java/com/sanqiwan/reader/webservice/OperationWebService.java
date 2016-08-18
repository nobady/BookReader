package com.sanqiwan.reader.webservice;

import com.sanqiwan.reader.apps.AppInfo;
import com.sanqiwan.reader.model.BookRecommendItem;
import com.sanqiwan.reader.model.Splash;
import com.sanqiwan.reader.model.Topic;
import com.sanqiwan.reader.webservice.parser.*;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/25/13
 * Time: 10:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OperationWebService {

    private OperationUriBuilder mOperationUriBuilder;

    public OperationWebService() {
        mOperationUriBuilder = new OperationUriBuilder(new OperationServerConfig());
    }

    public List<Topic> getTopics(long time) throws ZLNetworkException {
        HttpRequester<Topic> httpRequester = new HttpRequester<Topic>();
        return httpRequester.requestList(mOperationUriBuilder.builderUriForTopic(time), new TopicParser());
    }

    public List<Splash> getSplashs() throws ZLNetworkException {
        HttpRequester<Splash> httpRequester = new HttpRequester<Splash>();
        return httpRequester.requestList(mOperationUriBuilder.builderUriForSplash(), new SplashParser());
    }

    public List<BookRecommendItem> getBookRecommends(int categoryId) throws ZLNetworkException {
        HttpRequester<BookRecommendItem> httpRequester = new HttpRequester<BookRecommendItem>();
        return httpRequester.requestList(mOperationUriBuilder.builderUriForBookRecommend(categoryId), new BookRecommendParser());
    }

    public List<BookRecommendItem> getBookRecommends() throws ZLNetworkException {
        return getBookRecommends(0);
    }

    public List<Long> getTodayRecommends() throws ZLNetworkException {
        HttpRequester<Long> httpRequester = new HttpRequester<Long>();
        return httpRequester.requestList(mOperationUriBuilder.builderUriForTodayRecommend(), new TodayRecommendParser());
    }

    public List<String> getSearchRecommend(int size) throws ZLNetworkException {
        HttpRequester<String> httpRequester = new HttpRequester<String>();
        return httpRequester.requestList(mOperationUriBuilder.builderUriForGetSearchRecommend(size), new SearchRecommendParser());
    }

    public List<AppInfo> getAppList(int since, int pageSize) throws ZLNetworkException {
        HttpRequester<AppInfo> httpRequester = new HttpRequester<AppInfo>();
        return httpRequester.requestList(mOperationUriBuilder.buildUriForGetAppList(since, pageSize), new AppInfoParser());
    }
}
