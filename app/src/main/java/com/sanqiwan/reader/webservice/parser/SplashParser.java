package com.sanqiwan.reader.webservice.parser;

import android.util.Log;
import com.sanqiwan.reader.model.Splash;
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
 * Date: 13-7-29
 * Time: 下午3:21
 */
public class SplashParser implements IParser<Splash> {
    private final static String KEY_ERR_CODE = "errcode";
    private final static String KEY_DATA = "data";
    private final static String KEY_ID = "f_id";
    private final static String KEY_PICURL = "f_image";
    private final static String KEY_TEXT = "f_content";
    private final static String KEY_REFRESH_TIME = "f_time";
    private final static String LOG_TAG = "SplashParser";

    @Override
    public Splash parse(InputStream inputStream) {
        List<Splash> list = parseList(inputStream);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<Splash> parseList(InputStream inputStream) {
        String string = StringUtil.toString(inputStream);
        List<Splash> splashs = new ArrayList<Splash>();
        try {
            JSONObject jsonObject = new JSONObject(string);
            if (!jsonObject.has(KEY_ERR_CODE)) {
                return splashs;
            }
            int errcode = jsonObject.getInt(KEY_ERR_CODE);
            if (errcode != 0) {
                return splashs;
            }
            if (!jsonObject.has(KEY_DATA)) {
                return splashs;
            }
            JSONArray datas = jsonObject.getJSONArray(KEY_DATA);
            for (int i = 0; i < datas.length(); i++) {
                JSONObject splashJsonObj = datas.getJSONObject(i);
                Splash splash = new Splash();
                if (splashJsonObj.has(KEY_ID)) {
                    splash.setId(splashJsonObj.getInt(KEY_ID));
                }
                if (splashJsonObj.has(KEY_PICURL)) {
                    splash.setPictureUrl(splashJsonObj.getString(KEY_PICURL));
                }
                if (splashJsonObj.has(KEY_TEXT)) {
                    splash.setDescribeText(splashJsonObj.getString(KEY_TEXT));
                }
                if (splashJsonObj.has(KEY_REFRESH_TIME)) {
                    splash.setRefreshTime(splashJsonObj.getLong(KEY_REFRESH_TIME));
                }
                splashs.add(splash);

            }
        } catch (JSONException e) {
            Log.i(LOG_TAG, e.getMessage());
        }
        return splashs;
    }
}
