package com.sanqiwan.reader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.sanqiwan.reader.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-25
 * Time: 下午10:58
 * To change this template use File | Settings | File Templates.
 */
public class SearchRecommendAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<String> mRecommendKey;
    private Callback mCallback;

    public interface Callback {
        public void onItemClick(View v);
    }

    public SearchRecommendAdapter(Context context, Callback callback) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mRecommendKey = new ArrayList<String>(0);
        mCallback = callback;
    }

    public void setRecommendKey(List<String> recommendKey) {
        mRecommendKey.addAll(recommendKey);
        this.notifyDataSetChanged();
    }

    public void clearRecommendKey() {
        mRecommendKey.clear();
    }

    @Override
    public int getCount() {
        return mRecommendKey.size();
    }

    @Override
    public Object getItem(int position) {
        return mRecommendKey.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.search_recommend_item, null);
        }
        String recommendKey = mRecommendKey.get(position);
        TextView recommendView = (TextView) convertView.findViewById(R.id.recommend_key);
        recommendView.setText(recommendKey);
        recommendView.setTag(recommendKey);
        recommendView.setOnClickListener(mListener);
        recommendView.setTextColor(getTextColor());
        recommendView.setTextSize(getTextSize());
        return recommendView;
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCallback.onItemClick(v);
        }
    };

    private int getTextColor() {
        switch (getRandInteger()) {
            case 0:
                return mContext.getResources().getColor(R.color.search_recommend_color_one);
            case 1:
                return mContext.getResources().getColor(R.color.search_recommend_color_two);
            case 2:
            default:
                return mContext.getResources().getColor(R.color.search_recommend_color_three);
        }
    }

    private int getTextSize() {
        switch (getRandInteger()) {
            case 0:
                return (int)mContext.getResources().getDimension(R.dimen.search_recommend_key_size_one);
            case 1:
                return (int)mContext.getResources().getDimension(R.dimen.search_recommend_key_size_two);
            case 2:
            default:
                return (int)mContext.getResources().getDimension(R.dimen.search_recommend_key_size_three);
        }
    }

    private int getRandInteger() {
        Random rand = new Random();
        return rand.nextInt(3);
    }
}
