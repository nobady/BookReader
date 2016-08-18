package com.sanqiwan.reader.webservice.parser;

import android.util.Log;
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
public class TopicParser implements IParser<Topic> {
    private final static String KEY_ERR_CODE = "errcode";
    private final static String KEY_DATA = "data";
    private final static String KEY_ID = "s_id";
    private final static String KEY_PICURL = "s_cover";
    private final static String KEY_TEXT = "s_content";
    private final static String KEY_TITLE = "s_title";
    private final static String KEY_BOOKIDS = "s_book";
    private final static String KEY_REFRESH_TIME = "s_time";
    private final static String KEY_BANNER = "s_banner";
    private final static String LOG_TAG = "TopicParser";

    @Override
    public Topic parse(InputStream inputStream) {
        List<Topic> topics = parseList(inputStream);
        return topics.size() > 0 ? topics.get(0) : null;
    }

    @Override
    public List<Topic> parseList(InputStream inputStream) {
        String string = StringUtil.toString(inputStream);
        List<Topic> topics = new ArrayList<Topic>();
        try {
            JSONObject jsonObject = new JSONObject(string);
            if (!jsonObject.has(KEY_ERR_CODE)) {
                return topics;
            }
            int errcode = jsonObject.getInt(KEY_ERR_CODE);
            if (errcode != 0) {
                return topics;
            }
            if (!jsonObject.has(KEY_DATA)) {
                return topics;
            }
            JSONArray datas = jsonObject.getJSONArray(KEY_DATA);
            for (int i = 0; i < datas.length(); i++) {
                JSONObject topicJsonObj = datas.getJSONObject(i);
                Topic topic = new Topic();
                topic.setId(topicJsonObj.optInt(KEY_ID));
                topic.setPictureUrl(topicJsonObj.optString(KEY_PICURL));
                topic.setBannerUrl(topicJsonObj.optString(KEY_BANNER));
                topic.setDescribeText(topicJsonObj.optString(KEY_TEXT));
                topic.setRefreshTime(topicJsonObj.optLong(KEY_REFRESH_TIME));
                topic.setTitle(topicJsonObj.optString(KEY_TITLE));
                if (topicJsonObj.has(KEY_BOOKIDS)) {
                    JSONArray bookids = topicJsonObj.getJSONArray(KEY_BOOKIDS);
                    for (int x = 0; x < bookids.length(); x++) {
                        topic.addBook(bookids.optLong(x));
                    }
                }
                topics.add(topic);

            }
        } catch (JSONException e) {
            Log.i(LOG_TAG, e.getMessage());
        }
        return topics;
    }
}
