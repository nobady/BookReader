package com.sanqiwan.reader.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.adapter.FragmentAdapter;
import com.sanqiwan.reader.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-21
 * Time: 下午6:58
 * To change this template use File | Settings | File Templates.
 */
public class ChannelFragment extends BaseFragment implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private static final String SELECTED_ITEM_ID = "selected_item_id";
    private View mFragmentView;
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private ViewPager mViewPager;
    private TextView[] mTextViewArray = new TextView[2];
    public static final int CLASSIFICATION = 0;
    public static final int RANKLIST = 1;
    private BitmapDrawable mDrawable;
    private int mCurrentItem = -1;

    public static ChannelFragment newFragment() {
        return newFragment(CLASSIFICATION);
    }

    public static ChannelFragment newFragment(int selectItemId) {
        ChannelFragment fragment = new ChannelFragment();
        Bundle args = new Bundle();
        args.putInt(SELECTED_ITEM_ID, selectItemId);
        fragment.setArguments(args);
        return fragment;
    }

    private void takeArguments() {
        Bundle args = getArguments();

        if (args != null) {
            mCurrentItem = args.getInt(SELECTED_ITEM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        takeArguments();

        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.channel_layout, container, false);
            mTextViewArray[RANKLIST] = (TextView) mFragmentView.findViewById(R.id.ranklist_title);
            mTextViewArray[RANKLIST].setSelected(true);
            mTextViewArray[RANKLIST].setOnClickListener(this);
            mTextViewArray[CLASSIFICATION] = (TextView) mFragmentView.findViewById(R.id.classification_title);
            mTextViewArray[CLASSIFICATION].setOnClickListener(this);
            mViewPager = (ViewPager) mFragmentView.findViewById(R.id.mine_viewpager);
            mFragmentList.add(ClassificationFragment.newFragment());
            mFragmentList.add(RankListFragment.newFragment());
            mViewPager.setAdapter(new FragmentAdapter(getChildFragmentManager(), mFragmentList));
            mViewPager.setOnPageChangeListener(this);
            setNowItem(CLASSIFICATION);
        }
        if (mFragmentView.getParent() != null) {
            ((ViewGroup) mFragmentView.getParent()).removeAllViews();
        }
        if (mCurrentItem == RANKLIST) {
            mViewPager.setCurrentItem(RANKLIST);
            setNowItem(RANKLIST);
        } else {
            mViewPager.setCurrentItem(CLASSIFICATION);
            setNowItem(CLASSIFICATION);
        }
        return mFragmentView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.classification_title:
                mViewPager.setCurrentItem(CLASSIFICATION);
                break;
            case R.id.ranklist_title:
                mViewPager.setCurrentItem(RANKLIST);
                break;
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        setNowItem(i);
    }

    private void setNowItem(int i) {
        switch (i) {
            case CLASSIFICATION:
                mTextViewArray[CLASSIFICATION].setSelected(true);
                mTextViewArray[RANKLIST].setSelected(false);
                mTextViewArray[CLASSIFICATION].setBackgroundResource(R.drawable.classification_top_press);
                mTextViewArray[RANKLIST].setBackgroundDrawable(new BitmapDrawable());
                break;
            case RANKLIST:
                mTextViewArray[RANKLIST].setSelected(true);
                mTextViewArray[CLASSIFICATION].setSelected(false);
                mTextViewArray[RANKLIST].setBackgroundResource(R.drawable.classification_top_press);
                mTextViewArray[CLASSIFICATION].setBackgroundDrawable(new BitmapDrawable());
                break;
        }
    }

    private BitmapDrawable getRepeatLine() {
        if (mDrawable == null) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.top_line);
            mDrawable = BitmapUtil.getRepeatDrawable(bitmap);
        }
        return mDrawable;
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

}
