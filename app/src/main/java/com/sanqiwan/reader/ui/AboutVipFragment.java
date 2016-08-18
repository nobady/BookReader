package com.sanqiwan.reader.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sanqiwan.reader.R;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 11/16/13
 * Time: 4:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class AboutVipFragment extends BaseFragment {

    private View mFragmentView;
    private Context mContext;

    public static AboutVipFragment newFragment() {
        return new AboutVipFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.about_vip, container, false);

            TextView textView = (TextView) mFragmentView.findViewById(R.id.top_title);
            textView.setText(R.string.about_vip_title);

            ImageView returnView = (ImageView) mFragmentView.findViewById(R.id.btn_return);
            returnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }
        if (mFragmentView.getParent() != null) {
            ((ViewGroup) mFragmentView.getParent()).removeAllViews();
        }
        return mFragmentView;
    }
}