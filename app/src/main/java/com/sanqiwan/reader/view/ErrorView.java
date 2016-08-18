package com.sanqiwan.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sanqiwan.reader.R;

/**
 * Created with IntelliJ IDEA.
 * User: jwb
 * Date: 13-12-20
 * Time: 下午3:53
 * To change this template use File | Settings | File Templates.
 */
public class ErrorView extends LinearLayout {

    private View mErrorView;
    private Button mRetryBtn;
    private TextView mErrorTips;

    public ErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mErrorView = LayoutInflater.from(context).inflate(R.layout.error_view_layout, null);
        mRetryBtn = (Button) mErrorView.findViewById(R.id.retry_btn);
        mErrorTips = (TextView) mErrorView.findViewById(R.id.game_list_fragment_error_view);
        addView(mErrorView);
    }

    private void setErrorTips(String errorTips) {
        if (mErrorTips != null) {
            mErrorTips.setText(errorTips);
        }
    }

    public void setRetryBtnClickListenner(OnClickListener onClickListenner) {
        mRetryBtn.setOnClickListener(onClickListenner);
    }
}
