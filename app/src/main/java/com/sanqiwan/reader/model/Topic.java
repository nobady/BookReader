package com.sanqiwan.reader.model;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/23/13
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class Topic implements Parcelable {
    private int mId;
    private String mPictureUrl;
    private String mBannerUrl;
    private String mDescribeText;
    private String mTitle;
    private long mRefreshTime;
    private List<Long> mBookList;

    public Topic() {
        mBookList = new ArrayList<Long>();
    }

    public Topic(int topicId) {
        this();
        mId = topicId;
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

    public void setDescribeText(String descriptText) {
        mDescribeText = descriptText;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getBannerUrl() {
        return mBannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        mBannerUrl = bannerUrl;
    }

    public long getRefreshTime() {
        return mRefreshTime;
    }

    public void setRefreshTime(long refreshTime) {
        mRefreshTime = refreshTime;
    }

    public List<Long> getBookList() {
        return mBookList;
    }

    public void addBooks(List<Long> bookList) {
        mBookList = bookList;
    }

    public void addBook(long bookid) {
        mBookList.add(bookid);
    }

    public String getJSONArrayStringBooklist() {
        return new JSONArray(mBookList).toString();
    }

    public void setBookListByJSONArray(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            this.addBook(jsonArray.optLong(i));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPictureUrl);
        dest.writeString(mBannerUrl);
        dest.writeInt(mBookList.size());
        for (Long l : mBookList) {
            dest.writeLong(l);
        }
        dest.writeString(mTitle);
        dest.writeString(mDescribeText);
        dest.writeLong(mRefreshTime);
        dest.writeInt(mId);
    }

    public static final Parcelable.Creator<Topic> CREATOR = new Parcelable.Creator<Topic>() {

        @Override
        public Topic createFromParcel(Parcel source) {
            Topic topic = new Topic();
            topic.mPictureUrl = source.readString();
            topic.mBannerUrl = source.readString();
            int size = source.readInt();
            for (int i = 0; i < size; i++) {
                topic.addBook(source.readLong());
            }
            topic.mTitle = source.readString();
            topic.mDescribeText = source.readString();
            topic.mRefreshTime = source.readLong();
            topic.mId = source.readInt();
            return topic;
        }

        @Override
        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };
}
