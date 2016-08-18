package com.sanqiwan.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sanqiwan.reader.R;

/**
 * Created with IntelliJ IDEA.
 * User: jwb
 * Date: 13-12-20
 * Time: 下午6:44
 * To change this template use File | Settings | File Templates.
 */
public class LoadingView extends LinearLayout {

    private View mLoadingView;
    protected TextView mLoadingTips;

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mLoadingView = LayoutInflater.from(context).inflate(R.layout.loading_view_layout, null);
        mLoadingTips = (TextView) mLoadingView.findViewById(R.id.home_progress_bar_title);
        addView(mLoadingView);
    }

    private void setLoadingTips(String loadingTips) {
        mLoadingTips.setText(loadingTips);
    }
}
