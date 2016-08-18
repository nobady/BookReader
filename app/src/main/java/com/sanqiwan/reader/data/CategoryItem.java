package com.sanqiwan.reader.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * User: sam
 * Date: 9/24/13
 * Time: 5:53 PM
 */
public class CategoryItem implements Parcelable {
    private String mName;
    private int mId;
    private int mIndex;

    public CategoryItem() {
    }

    public CategoryItem(String name, int id, int index) {
        mName = name;
        mId = id;
        mIndex = index;
    }

    public String getName() {
        return mName;
    }

    public int getId() {
        return mId;
    }

    public int getIndex() {
        return mIndex;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeInt(mId);
        dest.writeInt(mIndex);
    }

    public static final Parcelable.Creator<CategoryItem> CREATOR = new Parcelable.Creator<CategoryItem>() {

        @Override
        public CategoryItem createFromParcel(Parcel source) {
            CategoryItem categoryItem = new CategoryItem();
            categoryItem.mName = source.readString();
            categoryItem.mId = source.readInt();
            categoryItem.mIndex = source.readInt();
            return categoryItem;
        }

        @Override
        public CategoryItem[] newArray(int size) {
            return new CategoryItem[size];
        }
    };
}
