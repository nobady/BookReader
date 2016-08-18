package com.sanqiwan.reader.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/29/13
 * Time: 9:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class VolumeItem {

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_URL = "url";
    private static final String KEY_VIP = "vip";

    private String mUrl;
    private String mName;
    private long mId;
    private boolean mVip;

    public boolean isVip() {
        return mVip;
    }

    public void setVip(boolean vip) {
        mVip = vip;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public static VolumeItem fromJsonObject(JSONObject jsonObject) {

        VolumeItem item = new VolumeItem();
        item.setId(jsonObject.optLong(KEY_ID));
        item.setName(jsonObject.optString(KEY_NAME));
        item.setUrl(jsonObject.optString(KEY_URL));
        item.setVip(jsonObject.optBoolean(KEY_VIP));
        return item;
    }

    public static List<VolumeItem> fromJsonArray(JSONArray jsonArray) {

        List<VolumeItem> items = new ArrayList<VolumeItem>();
        for (int i = 0; i < jsonArray.length(); i++) {
            items.add(fromJsonObject(jsonArray.optJSONObject(i)));
        }
        return items;
    }

    public static JSONArray toJsonArray(List<VolumeItem> volumeItems) {
        JSONArray jsonArray = new JSONArray();
        for (VolumeItem item : volumeItems) {
            jsonArray.put(item.toJsonObject());
        }
        return jsonArray;
    }

    private JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_ID, mId);
            jsonObject.put(KEY_NAME, mName);
            jsonObject.put(KEY_URL, mUrl);
            jsonObject.put(KEY_VIP, mVip);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
