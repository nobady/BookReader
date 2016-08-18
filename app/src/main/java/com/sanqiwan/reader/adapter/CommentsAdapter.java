package com.sanqiwan.reader.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.model.CommentInfo;
import com.sanqiwan.reader.webimageview.WebImageView;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-23
 * Time: 下午10:27
 * To change this template use File | Settings | File Templates.
 */
public class CommentsAdapter extends BucketListAdapter<CommentInfo> {

    private LayoutInflater mInflater;

    public CommentsAdapter(Context context) {
        super(context, new ArrayList<CommentInfo>());
        mInflater = LayoutInflater.from(context);
    }

    @Override
    protected View newView(int position, CommentInfo element) {
        ViewHolder holder = new ViewHolder();
        View view = mInflater.inflate(R.layout.comment_item, null);
        holder.mUserName = (TextView) view.findViewById(R.id.user_name);
        holder.mContent = (TextView) view.findViewById(R.id.comment_content);
        holder.mTime = (TextView) view.findViewById(R.id.comment_time);
        holder.mUserImage = (WebImageView)  view.findViewById(R.id.user_image);
        view.setTag(holder);
        return view;
    }

    @Override
    protected void bindView(View view, int position, CommentInfo element) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.mUserName.setText(element.getUserName());
        holder.mContent.setText(Html.fromHtml(element.getContent()));
        holder.mTime.setText(element.getTime());
        holder.mUserImage.setDefaultImageResource(R.drawable.comment_user_image);
        holder.mUserImage.setImageUrl(element.getUserImageUrl());
    }

    private class ViewHolder {
        public WebImageView mUserImage;
        public TextView mUserName;
        public TextView mContent;
        public TextView mTime;
    }
}
