package com.sanqiwan.reader.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.model.TOC;
import com.sanqiwan.reader.model.VipVolumeItem;
import com.sanqiwan.reader.model.VolumeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jwb
 * Date: 13-11-7
 * Time: 下午3:54
 * To change this template use File | Settings | File Templates.
 */
public class VipChaptersAdapter extends BucketListAdapter<VipVolumeItem> {
    private LayoutInflater mInflater;
    private List<VipVolumeItem> mVipVolumeList;
    private int mCurrentIndex;
    private ListView mOwnListView;

    public VipChaptersAdapter(Context context, ListView ownerListView) {
        super(context, new ArrayList<VipVolumeItem>());
        this.mInflater = LayoutInflater.from(context);
        mOwnListView = ownerListView;
    }

    public void setVipVolumeItemList(List<VipVolumeItem> vipVolumeItemList) {
        mVipVolumeList = vipVolumeItemList;
        notifyDataSetChanged();
    }

    public List<VipVolumeItem> getVipVolumeItemList() {
        return mVipVolumeList;
    }

    @Override
    public VipVolumeItem getElement(int position) {
        if (mVipVolumeList == null) {
            return null;
        } else {
            return mVipVolumeList.get(position);
        }
    }

    @Override
    public int getElementCount() {
        if (mVipVolumeList == null) {
            return 0;
        } else {
            return mVipVolumeList.size();
        }
    }

    @Override
    public long getItemId(int position) {
        return mVipVolumeList.get(position).getChapterId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    protected void bindView(View view, int position, VipVolumeItem element) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (element != null && !TextUtils.isEmpty(element.getChapterName())) {
            holder.mChapterName.setText(element.getChapterName());
        }
        holder.mCheckBox.setChecked(mOwnListView.isItemChecked(position + mOwnListView.getHeaderViewsCount()));
    }

    @Override
    protected View newView(int position, VipVolumeItem element) {
        ViewHolder holder = new ViewHolder();
        View view = mInflater.inflate(R.layout.vip_chapters_item, null);
        holder.mChapterName = (TextView) view.findViewById(R.id.chapter_name);
        holder.mCheckBox = (CheckBox) view.findViewById(R.id.vip_checkbox);
        view.setTag(holder);
        return view;
    }

    private class ViewHolder {
        public TextView mChapterName;
        public CheckBox mCheckBox;
    }
}
