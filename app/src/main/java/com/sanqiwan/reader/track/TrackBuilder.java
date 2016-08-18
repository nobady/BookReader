package com.sanqiwan.reader.track;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-9-11
 * Time: 下午3:25
 * To change this template use File | Settings | File Templates.
 */
class TrackBuilder {

    private static final String KEY_ACTION = "action";
    private static final String ACTIVATE = "activate";
    private static final String LAUNCH_APP = "launch_app";

    public String buildAppActivateJson() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(KEY_ACTION, ACTIVATE);
            AnalysisParameters.getInstance().toJsonObject(jsonObject);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String buildLaunchAppJson() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(KEY_ACTION, LAUNCH_APP);
            AnalysisParameters.getInstance().toJsonObject(jsonObject);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
