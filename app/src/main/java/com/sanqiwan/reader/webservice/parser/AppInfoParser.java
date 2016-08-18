package com.sanqiwan.reader.webservice.parser;

import android.util.Log;
import com.sanqiwan.reader.apps.AppInfo;
import com.sanqiwan.reader.util.StringUtil;
import com.sanqiwan.reader.webservice.IParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lenovo
 * Date: 13-9-7
 * Time: 上午9:55
 * To change this template use File | Settings | File Templates.
 */
public class AppInfoParser implements IParser<AppInfo> {

    private static final String KEY_ERRCODE = "errcode";
    private static final String KEY_DATA = "data";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PACK_NAME = "pack_name";
    private static final String KEY_ICON_URL = "icon_url";
    private static final String KEY_PACK_SIZE = "pack_size";
    private static final String KEY_HOT = "hot";
    private static final String KEY_DESCRIBE = "describe";
    private static final String KEY_PACK_URL = "pack_url";
    private static final String LOG_TAG = "GameInfoParser";

    private static final int INVALID = -1;
    private static final int SUCCESS = 0;

    @Override
    public AppInfo parse(InputStream inputStream) {
        List<AppInfo> gameInfoList = parseList(inputStream);
        return gameInfoList.size() > 0 ? gameInfoList.get(0) : null;
    }

    @Override
    public List parseList(InputStream inputStream) {
        String string = StringUtil.toString(inputStream);
        try {
            JSONObject jsonObject = new JSONObject(string);
            int status = jsonObject.optInt(KEY_ERRCODE, INVALID);
            if (status != SUCCESS) {
                return null;
            }
            JSONArray resultArray = jsonObject.optJSONArray(KEY_DATA);
            if (resultArray == null) {
                return null;
            }
            List<AppInfo> gameInfoList = new ArrayList<AppInfo>();
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject gameInfojsonObject = resultArray.optJSONObject(i);
                if (gameInfojsonObject != null) {
                    AppInfo gameInfo = new AppInfo();
                    gameInfo.setId(gameInfojsonObject.optString(KEY_ID));
                    gameInfo.setName(StringUtil.optStringFromJsonObject(gameInfojsonObject, KEY_NAME));
                    gameInfo.setPackageName(gameInfojsonObject.optString(KEY_PACK_NAME));
                    gameInfo.setIconUrl(StringUtil.optStringFromJsonObject(gameInfojsonObject, KEY_ICON_URL));
                    gameInfo.setPackageSize(gameInfojsonObject.optLong(KEY_PACK_SIZE));
                    gameInfo.setHot(gameInfojsonObject.optInt(KEY_HOT));
                    gameInfo.setDescribe(StringUtil.optStringFromJsonObject(gameInfojsonObject, KEY_DESCRIBE));
                    gameInfo.setPackUrl(StringUtil.optStringFromJsonObject(gameInfojsonObject, KEY_PACK_URL));
                    gameInfoList.add(gameInfo);
                }
            }
            return gameInfoList;
        } catch (Exception e) {
            Log.i(LOG_TAG, e.getMessage());
        }
        return null;
    }
}
