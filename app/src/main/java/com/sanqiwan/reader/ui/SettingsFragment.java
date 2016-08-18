package com.sanqiwan.reader.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.adapter.SettingListAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 11/18/13
 * Time: 10:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class SettingsFragment extends BaseFragment implements View.OnClickListener {
    private ListView mListView;
    private TextView mTitleTextView;
    private ImageView mReturnView;
    private View mFragmentView;
    private Context mContext;

    public static SettingsFragment newFragment() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.settings_layout, container, false);

            mTitleTextView = (TextView) mFragmentView.findViewById(R.id.top_title);
            mReturnView = (ImageView) mFragmentView.findViewById(R.id.btn_return);
            mListView = (ListView) mFragmentView.findViewById(R.id.lv_setting);

            mTitleTextView.setText(R.string.function_settings);
            mReturnView.setOnClickListener(this);
            SettingListAdapter settingListAdapter = new SettingListAdapter(mContext, true);
            settingListAdapter.setOpenFeedbackFragmentListener(mOpenFeedbackListener);
            mListView.setAdapter(settingListAdapter);
        }
        if (mFragmentView.getParent() != null) {
            ((ViewGroup) mFragmentView.getParent()).removeAllViews();
        }
        return mFragmentView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_return:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

    private View.OnClickListener mOpenFeedbackListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openFeedbackActivity(mContext);
        }
    };
}