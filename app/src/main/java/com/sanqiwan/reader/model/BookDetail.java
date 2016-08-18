package com.sanqiwan.reader.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-8-4
 * Time: 下午10:24
 * To change this template use File | Settings | File Templates.
 */
public class BookDetail {

    private static final String KEY_CATEGORY = "category";
    private static final String KEY_SCENARIO = "scenario";
    private static final String KEY_BOOK_ID = "book_id";
    private static final String KEY_BOOK_NAME = "book_name";
    private static final String KEY_COVER = "cover";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_AUTHOR_ID = "author_id";
    private static final String KEY_AUTHOR_NAME = "author_name";
    private static final String KEY_CREATE_TIME = "create_time";
    private static final String KEY_BOOK_SIZE = "book_size";
    private static final String KEY_VIP = "vip";
    private static final String KEY_FINISH = "finish";
    private static final String KEY_WEEK_VISIT = "week_visit";
    private static final String KEY_MONTH_VISIT = "month_visit";
    private static final String KEY_ALL_VISIT = "all_visit";
    private static final String KEY_TAGS= "tags";
    private static final String KEY_AUTHORIZATION = "authorization";

    private String mCategory;
    private String mScenario;
    private String mBookName;
    private long mBookId;
    private String mBookCover;
    private String mDescription;
    private long mAuthorId;
    private String mAuthorName;
    private String mCreateTime;
    private int mBookSize;
    private boolean mVip;
    private boolean mFinish;
    private boolean mAuthorization;
    private int mWeekVisit;
    private int mMonthVisit;
    private int mAllVisit;
    private String mTags;

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public String getScenario() {
        return mScenario;
    }

    public void setScenario(String mScenario) {
        this.mScenario = mScenario;
    }

    public String getBookName() {
        return mBookName;
    }

    public void setBookName(String mBookName) {
        this.mBookName = mBookName;
    }

    public long getBookId() {
        return mBookId;
    }

    public void setBookId(long mBookId) {
        this.mBookId = mBookId;
    }

    public String getBookCover() {
        return mBookCover;
    }

    public void setBookCover(String mBookCover) {
        this.mBookCover = mBookCover;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public long getAuthorId() {
        return mAuthorId;
    }

    public void setAuthorId(long mAuthorId) {
        this.mAuthorId = mAuthorId;
    }

    public String getAuthorName() {
        return mAuthorName;
    }

    public void setAuthorName(String mAuthorName) {
        this.mAuthorName = mAuthorName;
    }

    public String getCreateTime() {
        return mCreateTime;
    }

    public void setCreateTime(String mCreateTime) {
        this.mCreateTime = mCreateTime;
    }

    public int getBookSize() {
        return mBookSize;
    }

    public void setBookSize(int mBookSize) {
        this.mBookSize = mBookSize;
    }

    public boolean getVip() {
        return mVip;
    }

    public void setVip(boolean mVip) {
        this.mVip = mVip;
    }

    public boolean getFinish() {
        return mFinish;
    }

    public void setFinish(boolean mFinish) {
        this.mFinish = mFinish;
    }

    public int getWeekVisit() {
        return mWeekVisit;
    }

    public void setWeekVisit(int mWeekVisit) {
        this.mWeekVisit = mWeekVisit;
    }

    public int getMonthVisit() {
        return mMonthVisit;
    }

    public void setMonthVisit(int mMonthVisit) {
        this.mMonthVisit = mMonthVisit;
    }

    public int getAllVisit() {
        return mAllVisit;
    }

    public void setAllVisit(int mAllVisit) {
        this.mAllVisit = mAllVisit;
    }

    public String getTags() {
        return mTags;
    }

    public void setTags(String mTags) {
        this.mTags = mTags;
    }

    public boolean getAuthorization() {
        return this.mAuthorization;
    }

    public void setAuthorization(boolean authorization) {
        this.mAuthorization = authorization;
    }

    public static BookDetail fromJsonString(String jsonString) {

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return fromJsonObject(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BookDetail fromJsonObject(JSONObject jsonObject) {

        BookDetail bookDetail = new BookDetail();
        bookDetail.setBookId(jsonObject.optInt(KEY_BOOK_ID));
        bookDetail.setBookName(jsonObject.optString(KEY_BOOK_NAME));
        bookDetail.setBookSize(jsonObject.optInt(KEY_BOOK_SIZE));
        bookDetail.setVip(jsonObject.optBoolean(KEY_VIP));
        bookDetail.setBookCover(jsonObject.optString(KEY_COVER));
        bookDetail.setAuthorId(jsonObject.optInt(KEY_AUTHOR_ID));
        bookDetail.setAuthorName(jsonObject.optString(KEY_AUTHOR_NAME));
        bookDetail.setCategory(jsonObject.optString(KEY_CATEGORY));
        bookDetail.setScenario(jsonObject.optString(KEY_SCENARIO));
        bookDetail.setCreateTime(jsonObject.optString(KEY_CREATE_TIME));
        bookDetail.setDescription(jsonObject.optString(KEY_DESCRIPTION));
        bookDetail.setTags(jsonObject.optString(KEY_TAGS));
        bookDetail.setWeekVisit(jsonObject.optInt(KEY_WEEK_VISIT));
        bookDetail.setMonthVisit(jsonObject.optInt(KEY_MONTH_VISIT));
        bookDetail.setAllVisit(jsonObject.optInt(KEY_ALL_VISIT));
        bookDetail.setFinish(jsonObject.optBoolean(KEY_FINISH));
        bookDetail.setAuthorization(jsonObject.optBoolean(KEY_AUTHORIZATION));
        return bookDetail;
    }

    public String toJsonString() {
        return toJsonObject().toString();
    }

    public JSONObject toJsonObject() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_BOOK_ID, mBookId);
            jsonObject.put(KEY_BOOK_NAME, mBookName);
            jsonObject.put(KEY_BOOK_SIZE, mBookSize);
            jsonObject.put(KEY_VIP, mVip);
            jsonObject.put(KEY_COVER, mBookCover);
            jsonObject.put(KEY_AUTHOR_ID, mAuthorId);
            jsonObject.put(KEY_AUTHOR_NAME, mAuthorName);
            jsonObject.put(KEY_CATEGORY, mCategory);
            jsonObject.put(KEY_SCENARIO, mScenario);
            jsonObject.put(KEY_CREATE_TIME, mCreateTime);
            jsonObject.put(KEY_DESCRIPTION, mDescription);
            jsonObject.put(KEY_TAGS, mTags);
            jsonObject.put(KEY_WEEK_VISIT, mWeekVisit);
            jsonObject.put(KEY_MONTH_VISIT, mMonthVisit);
            jsonObject.put(KEY_ALL_VISIT, mAllVisit);
            jsonObject.put(KEY_FINISH, mFinish);
            jsonObject.put(KEY_AUTHORIZATION, mAuthorization);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
