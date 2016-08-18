package com.sanqiwan.reader.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.model.Topic;
import com.sanqiwan.reader.util.ImageLoadUtil;
import com.sanqiwan.reader.util.UIUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 8/9/13
 * Time: 9:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class TopicAdapter extends BaseAdapter {

    private Activity mActivity;
    private List<Topic> mTopics;
    private LayoutInflater mLayoutInflater;
    private Map<String, Bitmap> mBitmaps;
    private ListView mListView;

    public TopicAdapter(Activity context, List<Topic> topics, ListView listView) {
        mActivity = context;
        mTopics = topics;
        mLayoutInflater = LayoutInflater.from(mActivity);
        mBitmaps = new HashMap<String, Bitmap>();
        mListView = listView;
    }

    @Override
    public int getCount() {
        return mTopics.size();
    }

    @Override
    public Object getItem(int position) {
        return mTopics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.topic_item, null);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.topic_image);
        String url = mTopics.get(position).getPictureUrl();
        if (url != null && !url.equals("")) {
            Bitmap coverBitmap = mBitmaps.get(url);
            if (coverBitmap != null) {
                int height = coverBitmap.getHeight()*(mListView.getWidth()-20)/coverBitmap.getWidth();
                ListView.LayoutParams params = (AbsListView.LayoutParams) imageView.getLayoutParams();
                if (params == null) {
                    params = new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                } else {
                    params.height = height;
                }

                imageView.setLayoutParams(params);
                imageView.setImageBitmap(coverBitmap);

            } else {
                //imageView.setImageResource(R.drawable.recommend_loading);
                loadImage(url);
            }
        }
        return convertView;
    }

    private void loadImage(final String coverUrl) {

        ImageLoadUtil.loadImage(coverUrl, new ImageLoadUtil.Callback() {
            @Override
            public void onImageLoaded(String url, Bitmap bitmap) {
                mBitmaps.put(url, bitmap);

                UIUtil.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onImageLoadFailed(String url) {
            }
        });
    }

    public void setData(List<Topic> topics) {
        mTopics = topics;
        this.notifyDataSetChanged();
    }
}
