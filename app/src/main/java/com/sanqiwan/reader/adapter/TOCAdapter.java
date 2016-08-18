package com.sanqiwan.reader.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.model.TOC;
import com.sanqiwan.reader.model.VolumeItem;

/**
 * Created with IntelliJ IDEA.
 * User: lenovo
 * Date: 13-8-9
 * Time: 下午4:53
 * To change this template use File | Settings | File Templates.
 */
public class TOCAdapter extends BaseAdapter{
    private LayoutInflater mInflater;
    private TOC mToc;
    private boolean mNightMode;
    private Context mContext;
    private int mCurrentIndex;
    private ColorStateList mTextColor;

    public TOCAdapter(Context context) {
        mContext = context;
        this.mInflater = LayoutInflater.from(context);
        mTextColor = context.getResources().getColorStateList(R.color.toc_default_color);
    }

    public void setTOC(TOC toc) {
        mToc = toc;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mToc == null) {
            return 0;
        } else {
            return mToc.getSize();
        }
    }

    @Override
    public VolumeItem getItem(int i) {
        if (mToc == null) {
            return null;
        } else {
            return mToc.getItem(i);
        }
    }

    @Override
    public long getItemId(int i) {
        if (mToc == null) {
            return 0;
        } else {
            return mToc.getItem(i).getId();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = newView();
        }

        bindView(position, convertView);
        return convertView;
    }

    private View newView() {

        ViewHolder holder = new ViewHolder();
        View view = mInflater.inflate(R.layout.bookread_toc_item, null);
        holder.mChapterName = (TextView) view.findViewById(R.id.chaptername);
        holder.mChapterIndex = (TextView) view.findViewById(R.id.chapternum);
        holder.mVIPView = view.findViewById(R.id.vip_bg);
        view.setTag(holder);
        return view;
    }

    private void bindView(int position, View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        VolumeItem item = getItem(position);
        holder.mChapterName.setText(item.getName());
        holder.mChapterIndex.setText(String.valueOf(position + 1));
        Resources r = mContext.getResources();
        if (item.isVip()) {
            holder.mVIPView.setVisibility(View.VISIBLE);
        } else {
            holder.mVIPView.setVisibility(View.GONE);
        }
        if (mCurrentIndex == position) {
            holder.mChapterName.setTextColor(r.getColor(R.color.reader_toc_press));
            holder.mChapterIndex.setTextColor(r.getColor(R.color.reader_toc_press));
        } else {
            holder.mChapterName.setTextColor(mTextColor);
            holder.mChapterIndex.setTextColor(mTextColor);
        }

    }

    private class ViewHolder {
        public TextView mChapterName;
        public TextView mChapterIndex;
        private View mVIPView;
    }

    public void setNightMode(boolean nightMode) {
        if (mNightMode != nightMode) {
            mNightMode = nightMode;
            notifyDataSetChanged();
        }
    }

    public void setCurrentIndex(int currentIndex) {
        mCurrentIndex = currentIndex;
        notifyDataSetChanged();
    }

    public void setTocTextColor(ColorStateList color) {
        mTextColor = color;
        notifyDataSetChanged();
    }
}
