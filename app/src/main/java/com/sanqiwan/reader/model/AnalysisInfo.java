package com.sanqiwan.reader.model;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-9-11
 * Time: 下午12:14
 * To change this template use File | Settings | File Templates.
 */
public class AnalysisInfo {
    private int mID;
    private String mData;
    private String mUri;

    public int getID() {
        return mID;
    }

    public void setID(int id) {
        this.mID = id;
    }

    public String getData() {
        return mData;
    }

    public void setData(String data) {
        this.mData = data;
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String uri) {
        this.mUri = uri;
    }
}
