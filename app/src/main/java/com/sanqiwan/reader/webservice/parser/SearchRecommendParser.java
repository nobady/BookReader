package com.sanqiwan.reader.webservice.parser;

import com.sanqiwan.reader.util.StringUtil;
import com.sanqiwan.reader.webservice.IParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jwb
 * Date: 13-11-22
 * Time: 上午10:35
 * To change this template use File | Settings | File Templates.
 */
public class SearchRecommendParser implements IParser<String> {

    private final static String KEY_ERR_CODE = "errcode";
    private final static String KEY_MSG = "msg";
    private final static String KEY_DATA = "data";

    private static final String KEY_OK = "ok";
    private static final int INVALID = -1;
    private static final int SUCCESS = 0;

    @Override
    public String parse(InputStream inputStream) {
        return null;
    }

    @Override
    public List<String> parseList(InputStream inputStream) {
        String string = StringUtil.toString(inputStream);
        try {
            JSONObject jsonObject = new JSONObject(string);
            int errcode = jsonObject.optInt(KEY_ERR_CODE, INVALID);
            String ok = jsonObject.optString(KEY_MSG);
            if (errcode != SUCCESS || !KEY_OK.equals(ok)) {
                return null;
            }
            JSONArray jsonArray = jsonObject.optJSONArray(KEY_DATA);
            List<String> bookNameList = new ArrayList<String>();
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    bookNameList.add(jsonArray.optString(i));
                }
                return bookNameList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
