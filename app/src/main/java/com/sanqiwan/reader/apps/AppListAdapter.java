package com.sanqiwan.reader.apps;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.adapter.BucketListAdapter;
import com.sanqiwan.reader.track.UMengEventId;
import com.sanqiwan.reader.util.StringUtil;
import com.sanqiwan.reader.webimageview.WebImageView;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class AppListAdapter extends BucketListAdapter<AppInfo> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public AppListAdapter(Context context, List<AppInfo> appInfoList) {
        super(context, appInfoList);
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    protected View newView(int position, AppInfo element) {
        Holder holder = new Holder();
        View view = mLayoutInflater.inflate(R.layout.app_list_item_layout, null);
        holder.mIcon = (WebImageView) view.findViewById(R.id.icon);
        holder.mTitle = (TextView) view.findViewById(R.id.game_title);
        holder.mSize = (TextView) view.findViewById(R.id.home_game_size);
        holder.mDescribe = (TextView) view.findViewById(R.id.describe);
        view.setTag(holder);
        return view;
    }

    @Override
    protected void bindView(View view, int position, AppInfo appInfo) {
        String describe = appInfo.getDescribe();
        Holder holder = (Holder) view.getTag();
        holder.mTitle.setText(appInfo.getName());
        String gameSize = mContext.getResources().getString(R.string.app_size);
        holder.mSize.setText(gameSize + StringUtil.getSizeText(appInfo.getPackageSize()));
        holder.mIcon.setDefaultImageResource(R.drawable.default_app_icon);
        holder.mIcon.setImageUrl(appInfo.getIconUrl());

        if (!TextUtils.isEmpty(describe)) {
            holder.mDescribe.setText(describe);
            holder.mDescribe.setVisibility(View.VISIBLE);
        } else {
            holder.mDescribe.setVisibility(View.GONE);
        }
        holder.mTitle.setTag(appInfo);
        view.setOnClickListener(mViewOnClickListener);
    }

    private View.OnClickListener mViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Holder holder = (Holder) v.getTag();
            AppInfo appInfo = (AppInfo) holder.mTitle.getTag();
            MobclickAgent.onEvent(mContext, UMengEventId.CLICK_TO_DOWNLOAD, appInfo.getName());
            downloadApp(appInfo);
        }
    };

    private void downloadApp(AppInfo appInfo) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(appInfo.getPackUrl()));
        if (!(mContext instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private class Holder {
        private WebImageView mIcon;
        private TextView mTitle;
        private TextView mSize;
        private TextView mDescribe;
    }
}


