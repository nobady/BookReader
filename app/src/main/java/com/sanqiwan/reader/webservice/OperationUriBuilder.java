package com.sanqiwan.reader.webservice;

import android.net.Uri;
import com.sanqiwan.reader.util.ManifestUtil;

import java.net.URLDecoder;

/**
 * User: Sam
 * Date: 13-8-7
 * Time: 上午10:29
 */
public class OperationUriBuilder {
    private final static String R_MODEL = "r";
    private final static String GET_SPLASH = "bookapp/getFlash";
    private final static String GET_TOPIC = "bookapp/getSubject";
    private final static String REFRESH_TIME = "refreshtime";
    private final static String CATE_ID = "cate_id";
    private final static String SIZE = "size";
    private final static String SINCE = "since";
    private final static String PAGE_SIZE = "pagesize";
    private final static String VERSION = "v";

    private final static String GET_BOOK_RECOMMEND = "bookapp/getBookRecommend";
    private final static String GET_TODAY_RECOMMEND = "bookapp/getTodayRecommend";
    private static final String GET_SEARCH_RECOMMEND = "bookapp/getSearchRecommend";
    private static final String GET_APP_RECOMMEND = "bookapp/getAppRecommend";
    private static final String ANALYSIS_SERVICE = "analysis/upload";

    private OperationServerConfig mOperationServerConfig;
    private Uri mBaseUrl;

    public OperationUriBuilder(OperationServerConfig operationServerConfig) {
        mOperationServerConfig = operationServerConfig;
    }

    private Uri.Builder getBaseUrl() {
        if (mBaseUrl == null) {
            mBaseUrl = Uri.parse(mOperationServerConfig.getHost());
        }

        return mBaseUrl.buildUpon();
    }

    public String builderUriForSplash() {
        Uri.Builder builder = getBaseUrl();
        builder.appendQueryParameter(R_MODEL, GET_SPLASH);
        String url = URLDecoder.decode(builder.toString());
        return url;
    }

    public String builderUriForTopic(long time) {
        Uri.Builder builder = getBaseUrl();
        builder.appendQueryParameter(R_MODEL, GET_TOPIC);
        builder.appendQueryParameter(REFRESH_TIME, String.valueOf(time));
        String url = URLDecoder.decode(builder.toString());
        return url;
    }

    public String builderUriForBookRecommend(int categoryId) {
        Uri.Builder builder = getBaseUrl();
        builder.appendQueryParameter(R_MODEL, GET_BOOK_RECOMMEND);
        builder.appendQueryParameter(CATE_ID, String.valueOf(categoryId));
        String url = URLDecoder.decode(builder.toString());
        return url;
    }

    public String builderUriForTodayRecommend() {
        Uri.Builder builder = getBaseUrl();
        builder.appendQueryParameter(R_MODEL, GET_TODAY_RECOMMEND);
        String url = URLDecoder.decode(builder.toString());
        return url;
    }


    public String builderUriForGetSearchRecommend(int size) {
        Uri.Builder builder = getBaseUrl();
        builder.appendQueryParameter(R_MODEL, GET_SEARCH_RECOMMEND);
        builder.appendQueryParameter(SIZE, Integer.toString(size));
        String url = URLDecoder.decode(builder.toString());
        return url;
    }

    public String buildUriForGetAppList(int since, int pageSize) {
        Uri.Builder builder = getBaseUrl();
        builder.appendQueryParameter(R_MODEL, GET_APP_RECOMMEND);
        builder.appendQueryParameter(SINCE, String.valueOf(since));
        builder.appendQueryParameter(PAGE_SIZE, String.valueOf(pageSize));
        String url = URLDecoder.decode(builder.toString());
        return url;
    }

    public String buildUriForAnalysis() {
        Uri.Builder builder = getBaseUrl();
        builder.appendQueryParameter(R_MODEL, ANALYSIS_SERVICE);
        builder.appendQueryParameter(VERSION, String.valueOf(ManifestUtil.getVersion()));
        String url = URLDecoder.decode(builder.toString());
        return url;
    }
}
