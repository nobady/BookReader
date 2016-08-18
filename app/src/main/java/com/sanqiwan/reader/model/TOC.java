package com.sanqiwan.reader.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/29/13
 * Time: 4:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class TOC {

    private static final String KEY_BOOK_ID = "book_id";
    private static final String KEY_VOLUMES = "volumes";

    private long mBookId;
    private List<Volume> mVolumes;

    public TOC() {
        mVolumes = new ArrayList<Volume>();
    }

    public long getBookId() {
        return mBookId;
    }

    public void setBookId(long bookId) {
        mBookId = bookId;
    }

    public void addVolume(Volume volume) {
        mVolumes.add(volume);
    }

    public List<Volume> getAllVolume() {
        return mVolumes;
    }

    public void setVolumes(List<Volume> volumes) {
        mVolumes = volumes;
    }

    public VolumeItem getItem(int index) {

        if (index < 0){
            return null;
        }

        int localIndex = index;
        for (Volume volume : mVolumes) {
            int size = volume.getAllItems().size();
            if (size <= localIndex) {
                localIndex -= size;
            } else {
                return volume.getItem(localIndex);
            }
        }

        return null;
    }

    public VolumeItem getItemById(long id) {
        for (Volume volume : mVolumes) {
            VolumeItem item = volume.getItemById(id);
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    public int getItemIndexById(long id) {
        int count = 0;
        for (Volume volume : mVolumes) {
            int index = volume.getItemIndexById(id);
            if (index != -1) {
                return count + index;
            }
            count += volume.getAllItems().size();
        }
        return -1;
    }

    public VolumeItem getLastItem() {
        int size = getSize();
        if (size > 0) {
            return getItem(size - 1);
        }
        return null;
    }

    public int getSize() {
        int size = 0;
        for (Volume volume : mVolumes) {
            size += volume.getAllItems().size();
        }
        return size;
    }

    public static TOC fromJsonString(String jsonString) {

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return fromJsonObject(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static TOC fromJsonObject(JSONObject jsonObject) {

        TOC toc = new TOC();
        toc.setBookId(jsonObject.optLong(KEY_BOOK_ID));
        toc.setVolumes(Volume.fromJsonArray(jsonObject.optJSONArray(KEY_VOLUMES)));
        return toc;
    }

    public String toJsonString() {
        return toJsonObject().toString();
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_BOOK_ID, mBookId);
            jsonObject.put(KEY_VOLUMES, Volume.toJsonArray(mVolumes));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void addTOC(TOC toc) {

        if (toc == null || toc.getSize() == 0 || mBookId != toc.getBookId()) {
            return;
        }

        List<Volume> volumes = toc.mVolumes;
        if (getSize() <= 0) {
            mVolumes.addAll(volumes);
        } else {
            Volume lastVolume = mVolumes.get(mVolumes.size() - 1);
            Volume firstVolume = volumes.get(0);
            int start = 0;
            if (lastVolume.getId() == firstVolume.getId()) {
                lastVolume.addAll(firstVolume.getAllItems());
                start++;
            }

            for (int i = start; i < volumes.size(); i++) {
                mVolumes.add(volumes.get(i));
            }
        }
    }
}
