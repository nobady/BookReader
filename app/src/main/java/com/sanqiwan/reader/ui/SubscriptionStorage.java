package com.sanqiwan.reader.ui;

import android.util.SparseBooleanArray;
import com.sanqiwan.reader.model.VipVolumeItem;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jwb
 * Date: 13-11-18
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
public class SubscriptionStorage {

    private List<VipVolumeItem> mVipVolumeItemList;
    private SparseBooleanArray mSparseBooleanArray;

    public List<VipVolumeItem> getVipVolumeItemList() {
        return mVipVolumeItemList;
    }

    public void setVipVolumeItemList(List<VipVolumeItem> vipVolumeItemList) {
        mVipVolumeItemList = vipVolumeItemList;
    }

    public SparseBooleanArray getSparseBooleanArray() {
        return mSparseBooleanArray;
    }

    public void setSparseBooleanArray(SparseBooleanArray sparseBooleanArray) {
        mSparseBooleanArray = sparseBooleanArray;
    }
}
