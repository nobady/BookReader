package com.sanqiwan.reader.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sam on 14-4-10.
 */
public class RankItem  implements Parcelable {
    private int mId;
    private String mCover;
    private String mName;

    public RankItem() {

    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getCover() {
        return mCover;
    }

    public void setCover(String cover) {
        mCover = cover;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mCover);
        dest.writeString(mName);
    }

    public static final Parcelable.Creator<RankItem> CREATOR = new Parcelable.Creator<RankItem>() {

        @Override
        public RankItem createFromParcel(Parcel source) {
            RankItem item = new RankItem();
            item.setId(source.readInt());
            item.setCover(source.readString());
            item.setName(source.readString());
            return item;
        }

        @Override
        public RankItem[] newArray(int size) {
            return new RankItem[0];
        }
    };
}
