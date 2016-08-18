package com.sanqiwan.reader.apps;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created with IntelliJ IDEA.
 * User: lenovo
 * Date: 13-9-5
 * Time: 下午2:19
 * To change this template use File | Settings | File Templates.
 */
public class AppInfo implements Parcelable {

    private static final String[] EMPTY_ARRAY = new String[0];
    private String mIconUrl;
    private String mName;
    private String[] mTag = EMPTY_ARRAY;
    private String mPackageName;
    private long mPackageSize;
    private String mDescribe;
    private String mPackageUrl;
    private int mHot;
    private String mId;

    public String getIconUrl() {
        return mIconUrl;
    }

    public void setIconUrl(String iconUrl) {
        mIconUrl = iconUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getHot() {
        return mHot;
    }

    public void setHot(int degress) {
        mHot = degress;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    public long getPackageSize() {
        return mPackageSize;
    }

    public void setPackageSize(long packageSize) {
        mPackageSize = packageSize;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getPackUrl() {
        return mPackageUrl;
    }

    public void setPackUrl(String packUrl) {
        mPackageUrl = packUrl;
    }

    public String getDescribe() {
        return mDescribe;
    }

    public void setDescribe(String describe) {
        mDescribe = describe;
    }

    public String[] getTag() {
        return mTag;
    }

    public void setTag(String[] tag) {
        mTag = tag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mIconUrl);
        dest.writeString(mName);
        dest.writeString(mPackageUrl);
        dest.writeString(mId);
        dest.writeInt(mHot);
        dest.writeString(mPackageName);
        dest.writeLong(mPackageSize);
        dest.writeInt(mTag.length);
        dest.writeStringArray(mTag);
    }

    public static final Creator<AppInfo> CREATOR = new Creator<AppInfo>() {

        @Override
        public AppInfo createFromParcel(Parcel source) {
            AppInfo appInfo = new AppInfo();
            appInfo.setIconUrl(source.readString());
            appInfo.setName(source.readString());
            appInfo.setPackUrl(source.readString());
            appInfo.setId(source.readString());
            appInfo.setHot(source.readInt());
            appInfo.setPackageName(source.readString());
            appInfo.setPackageSize(source.readLong());
            int length = source.readInt();
            String[] tag = new String[length];
            source.readStringArray(tag);
            appInfo.setTag(tag);
            return appInfo;

        }

        @Override
        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };
}
