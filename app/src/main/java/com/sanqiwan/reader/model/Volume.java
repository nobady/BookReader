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
 * Time: 9:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class Volume {

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_ITEMS = "items";

    private long mId;
    private String mName;

    private List<VolumeItem> mVolumeItems;

    public Volume() {
        mVolumeItems = new ArrayList<VolumeItem>();
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

    public void addItem(VolumeItem item) {
        mVolumeItems.add(item);
    }

    public List<VolumeItem> getAllItems() {
        return mVolumeItems;
    }

    public void setItems(List<VolumeItem> items) {
        mVolumeItems = items;
    }

    public VolumeItem getItem(int localIndex) {

        if (localIndex >= 0 && localIndex < mVolumeItems.size()) {
            return mVolumeItems.get(localIndex);
        }

        return null;
    }

    public VolumeItem getItemById(long id) {
        for (VolumeItem item : mVolumeItems) {
            if (id == item.getId()) {
                return item;
            }
        }
        return null;
    }

    public int getItemIndexById(long id) {
        for (int i = 0; i < mVolumeItems.size(); i++) {
            if (id == mVolumeItems.get(i).getId()) {
                return i;
            }
        }
        return -1;
    }

    public void addAll(List<VolumeItem> items) {
        if (items != null) {
            mVolumeItems.addAll(items);
        }
    }

    public static Volume fromJsonObject(JSONObject jsonObject) {
        Volume volume = new Volume();
        volume.setId(jsonObject.optLong(KEY_ID));
        volume.setName(jsonObject.optString(KEY_NAME));
        volume.setItems(VolumeItem.fromJsonArray(jsonObject.optJSONArray(KEY_ITEMS)));
        return volume;
    }

    public static List<Volume> fromJsonArray(JSONArray jsonArray) {
        List<Volume> volumes = new ArrayList<Volume>();
        for (int i = 0; i < jsonArray.length(); i++) {
            volumes.add(fromJsonObject(jsonArray.optJSONObject(i)));
        }
        return volumes;
    }

    public static JSONArray toJsonArray(List<Volume> volumes) {
        JSONArray jsonArray = new JSONArray();
        for (Volume volume : volumes) {
            jsonArray.put(volume.toJsonObject());
        }
        return jsonArray;
    }

    private JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_ID, mId);
            jsonObject.put(KEY_NAME, mName);
            jsonObject.put(KEY_ITEMS, VolumeItem.toJsonArray(mVolumeItems));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
