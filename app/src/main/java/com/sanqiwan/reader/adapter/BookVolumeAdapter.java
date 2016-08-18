package com.sanqiwan.reader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.model.TOC;
import com.sanqiwan.reader.model.VolumeItem;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-8-15
 * Time: 下午9:58
 * To change this template use File | Settings | File Templates.
 */
public class BookVolumeAdapter extends BucketListAdapter<VolumeItem> {

    private TOC mTOC;
    private LayoutInflater mInflater;
    private Context mContext;

    public BookVolumeAdapter(Context context) {
        super(context, new ArrayList<VolumeItem>());
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    public TOC getTOC() {
        return mTOC;
    }

    public void addTOC(TOC toc) {
        if (mTOC == null) {
            mTOC = toc;
        } else {
            mTOC.addTOC(toc);
        }
        notifyDataSetChanged();
    }

    @Override
    public VolumeItem getElement(int position) {
        if (mTOC == null) {
            return null;
        } else {
            return mTOC.getItem(position);
        }
    }

    @Override
    public int getElementCount() {
        if (mTOC == null) {
            return 0;
        } else {
            return mTOC.getSize();
        }
    }

    @Override
    protected View newView(int position, VolumeItem element) {
        ViewHolder holder = new ViewHolder();
        View view = mInflater.inflate(R.layout.book_volume_item, null);
        holder.mVolumeTitle = (TextView) view.findViewById(R.id.dir_title);
        holder.mVolumeNum = (TextView) view.findViewById(R.id.dir_pageNum);
        holder.mVIPView = view.findViewById(R.id.vip_bg);
        view.setTag(holder);
        return view;
    }

    @Override
    protected void bindView(View view, int position, VolumeItem element) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (element.isVip()) {
            holder.mVIPView.setVisibility(View.VISIBLE);
        } else {
            holder.mVIPView.setVisibility(View.GONE);
        }
        holder.mVolumeTitle.setText(element.getName());
        holder.mVolumeNum.setText(String.valueOf(position + 1));
    }

    private class ViewHolder {
        public TextView mVolumeTitle;
        public TextView mVolumeNum;
        public View mVIPView;
    }
}
