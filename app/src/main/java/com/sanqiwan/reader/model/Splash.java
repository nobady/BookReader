package com.sanqiwan.reader.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/23/13
 * Time: 5:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class Splash {

    private final static String KEY_ID = "f_id";
    private final static String KEY_PICURL = "f_image";
    private final static String KEY_TEXT = "f_content";
    private final static String KEY_REFRESH_TIME = "f_time";

    private int mId;
    private String mPictureUrl;
    private String mDescribeText;
    private long mRefreshTime;
    private long mPastTime;

    public Splash() {

    }

    public Splash(int splashId) {
        mId = splashId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public String getPictureUrl() {
        return mPictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        mPictureUrl = pictureUrl;
    }

    public String getDescribeText() {
        return mDescribeText;
    }

    public void setDescribeText(String describeText) {
        mDescribeText = describeText;
    }

    public long getRefreshTime() {
        return mRefreshTime;
    }

    public void setRefreshTime(long refreshTime) {
        mRefreshTime = refreshTime;
    }

    public long getPastTime() {
        return mPastTime;
    }

    public void setPastTime(long pastTime) {
        mPastTime = pastTime;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put(KEY_ID, mId);
            jsonObject.put(KEY_PICURL, mPictureUrl);
            jsonObject.put(KEY_TEXT, mDescribeText);
            jsonObject.put(KEY_REFRESH_TIME, mRefreshTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static Splash fromJSONObject(JSONObject object) {
        if (object == null) {
            return null;
        }
        Splash splash = null;
        splash = new Splash();
        splash.setPictureUrl(object.optString(KEY_PICURL));
        splash.setId(object.optInt(KEY_ID));
        splash.setDescribeText(object.optString(KEY_TEXT));
        splash.setRefreshTime(object.optLong(KEY_REFRESH_TIME));
        return splash;
    }

    public static String toJSONArrayString(List<Splash> splashs) {
        if (splashs == null) {
            return null;
        }
        JSONArray array = new JSONArray();
        for (Splash splash : splashs) {
            array.put(splash.toJSONObject());
        }
        return array.toString();
    }

    public static List<Splash> fromJSONArray(JSONArray jsonArray) {
        List<Splash> splashs = new ArrayList<Splash>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            Splash splash = fromJSONObject(jsonArray.optJSONObject(i));
            if (splash != null) {
                splashs.add(splash);
            }
        }
        return splashs;
    }
}
