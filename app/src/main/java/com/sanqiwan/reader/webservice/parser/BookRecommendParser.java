package com.sanqiwan.reader.webservice.parser;

import android.util.Log;
import com.sanqiwan.reader.model.BookRecommendItem;
import com.sanqiwan.reader.model.Topic;
import com.sanqiwan.reader.util.StringUtil;
import com.sanqiwan.reader.webservice.IParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Sam
 * Date: 13-7-30
 * Time: 下午4:17
 */
public class BookRecommendParser implements IParser<BookRecommendItem> {
    private final static String KEY_ERR_CODE = "errcode";
    private final static String KEY_DATA = "data";
    private final static String KEY_ID = "bookid";
    private final static String KEY_PICURL = "cover";
    private final static String KEY_REFRESH_TIME = "time";
    private final static String LOG_TAG = "TopicParser";
    private final static String KEY_CATEGORY_ID = "cate_id";
    private final static int STATUS_ERROR = 1;
    private final static int STATUS_SUCCESS = 0;

    @Override
    public BookRecommendItem parse(InputStream inputStream) {
        List<BookRecommendItem> topics = parseList(inputStream);
        return topics.size() > 0 ? topics.get(0) : null;
    }

    @Override
    public List<BookRecommendItem> parseList(InputStream inputStream) {
        String string = StringUtil.toString(inputStream);
        List<BookRecommendItem> recommends = new ArrayList<BookRecommendItem>();
        try {
            JSONObject jsonObject = new JSONObject(string);
            int errcode = jsonObject.optInt(KEY_ERR_CODE, STATUS_ERROR);
            if (errcode != STATUS_SUCCESS) {
                return recommends;
            }
            JSONArray datas = jsonObject.optJSONArray(KEY_DATA);
            if (datas == null) {
                return recommends;
            }
            for (int i = 0; i < datas.length(); i++) {
                JSONObject bookJsonObj = datas.getJSONObject(i);
                BookRecommendItem bookRecommend = new BookRecommendItem();
                bookRecommend.setBookId(bookJsonObj.optInt(KEY_ID));
                bookRecommend.setPictureUrl(bookJsonObj.optString(KEY_PICURL));
                bookRecommend.setRefreshTime(bookJsonObj.optLong(KEY_REFRESH_TIME));
                bookRecommend.setCategoryId(bookJsonObj.optInt(KEY_CATEGORY_ID));
                recommends.add(bookRecommend);

            }
        } catch (JSONException e) {
            Log.i(LOG_TAG, e.getMessage());
        }
        return recommends;
    }
}
