package com.sanqiwan.reader.model;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/19/13
 * Time: 9:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class Chapter {

    private static final String KEY_BOOK_ID = "book_id";
    private static final String KEY_CHAPTER_ID = "chapter_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_VIP = "vip";
    private static final String KEY_CONTENT = "content";

    private long mBookId;
    private long mChapterId;
    private String mTitle;
    private boolean mVip;
    private String mContent;

    public Chapter() {
    }

    @Override
    public boolean equals(Object o) {

        if (o == null || !(o instanceof Chapter)) {
            return false;
        }

        if (this == o) {
            return true;
        }

        Chapter another = (Chapter) o;
        if (getBookId() == another.getBookId() && getChapterId() == another.getChapterId()) {
            return true;
        }

        return false;
    }

    public long getBookId() {
        return mBookId;
    }

    public long getChapterId() {
        return mChapterId;
    }

    public String getTitle() {
        return mTitle;
    }

    public boolean isVip() {
        return mVip;
    }

    public String getContent() {
        return mContent;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setVip(boolean vip) {
        mVip = vip;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public void setChapterId(long chapterId) {
        mChapterId = chapterId;
    }

    public void setBookId(long bookId) {
        mBookId = bookId;
    }

    public String toJsonString() {
        return toJsonObject().toString();
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_BOOK_ID, mBookId);
            jsonObject.put(KEY_CHAPTER_ID, mChapterId);
            jsonObject.put(KEY_TITLE, mTitle);
            jsonObject.put(KEY_VIP, mVip);
            jsonObject.put(KEY_CONTENT, mContent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static Chapter fromJsonString(String jsonString) {
        try {
            return fromJsonObject(new JSONObject(jsonString));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Chapter fromJsonObject(JSONObject jsonObject) {

        Chapter chapter = new Chapter();
        chapter.setBookId(jsonObject.optLong(KEY_BOOK_ID));
        chapter.setChapterId(jsonObject.optLong(KEY_CHAPTER_ID));
        chapter.setTitle(jsonObject.optString(KEY_TITLE));
        chapter.setVip(jsonObject.optBoolean(KEY_VIP));
        chapter.setContent(jsonObject.optString(KEY_CONTENT));
        return chapter;
    }
}
