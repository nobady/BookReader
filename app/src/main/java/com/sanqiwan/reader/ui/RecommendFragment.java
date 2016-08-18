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
 * User: jwb
 * Date: 13-11-21
 * Time: 上午10:12
 * To change this template use File | Settings | File Templates.
 */
public class RecommendFragment extends BaseFragment implements ViewPager.OnPageChangeListener, View.OnClickListener{
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private ViewPager mViewPager;
    private View mView;
    private TextView[] mTextViewArray = new TextView[3];
    private View[] mViews = new View[3];
    private static final int HOT_RECOMMEND = 0;
    private static final int NEW_FINISH = 1;
    private static final int NEW_POTENTIAL = 2;
    private BitmapDrawable mDrawable;

    public static RecommendFragment newFragment() {
        return new RecommendFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.recommend_layout, container, false);
            mTextViewArray[HOT_RECOMMEND] = (TextView) mView.findViewById(R.id.hot_recommend_tv);
            mViews[HOT_RECOMMEND] = (mView).findViewById(R.id.hot_recommend_title_bottom);
            mViews[HOT_RECOMMEND].setBackgroundDrawable(getRepeatLine());
            mTextViewArray[HOT_RECOMMEND].setSelected(true);
            mTextViewArray[HOT_RECOMMEND].setOnClickListener(this);
            mTextViewArray[NEW_FINISH] = (TextView) mView.findViewById(R.id.new_finish_tv);
            mViews[NEW_FINISH] = (mView).findViewById(R.id.new_finish_usercenter_title_bottom);
            mTextViewArray[NEW_FINISH].setOnClickListener(this);
            mTextViewArray[NEW_POTENTIAL] = (TextView) mView.findViewById(R.id.new_potential_tv);
            mViews[NEW_POTENTIAL] = (mView).findViewById(R.id.new_potential_usercenter_title_bottom);
            mTextViewArray[NEW_POTENTIAL].setOnClickListener(this);
            mViewPager = (ViewPager) mView.findViewById(R.id.recommend_viewpager);

            mFragmentList.add(HotRecommendFragment.newFragment());

            mFragmentList.add(NewFinishFragment.newFragment());

            mFragmentList.add(NewPotentialFragment.newFragment());

            mViewPager.setAdapter(new FragmentAdapter(getChildFragmentManager(), mFragmentList));
            mViewPager.setOnPageChangeListener(this);
            mViewPager.setCurrentItem(HOT_RECOMMEND);
        }
        if (mView.getParent() != null) {
            ((ViewGroup) mView.getParent()).removeAllViews();
        }
        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hot_recommend_tv:
                mViewPager.setCurrentItem(HOT_RECOMMEND);
                break;
            case R.id.new_finish_tv:
                mViewPager.setCurrentItem(NEW_FINISH);
                break;
            case R.id.new_potential_tv:
                mViewPager.setCurrentItem(NEW_POTENTIAL);
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
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        setTabBackground(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    private void setTabBackground(int item) {
        for (int i = 0; i < mTextViewArray.length; i++) {
            mTextViewArray[i].setSelected(false);
            mViews[i].setBackgroundDrawable(null);
            if (i == item) {
                mTextViewArray[i].setSelected(true);
                mViews[i].setBackgroundDrawable(getRepeatLine());
            }
        }
    }
}