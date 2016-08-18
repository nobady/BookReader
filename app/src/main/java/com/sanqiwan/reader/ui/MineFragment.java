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
import android.widget.*;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.adapter.FragmentAdapter;
import com.sanqiwan.reader.engine.UserManager;
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
public class MineFragment extends BaseFragment implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private static final String SELECTED_ITEM_ID = "selected_item_id";
    private View mFragmentView;
    public static final int ACTION_FOR_LOGIN = 100;
    public static final int ACTION_IN_USER_CENTER = 0;
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private ViewPager mViewPager;
    private TextView[] mTextViewArray = new TextView[2];
    private View[] mTitltBottomViewArray = new View[2];
    public static final int BOOK_SHELF = 0;
    public static final int USER_CENTER = 1;
    private BitmapDrawable mDrawable;
    private int mCurrentItem = -1;
    private UserCenterFragment mUserCenterFragment;
    private int mActionCode;

    public static MineFragment newFragment() {
        return newFragment(BOOK_SHELF);
    }

    public static MineFragment newFragment(int selectItemId) {
        MineFragment fragment = new MineFragment();
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
            mFragmentView = inflater.inflate(R.layout.mine_layout, container, false);
            mTextViewArray[USER_CENTER] = (TextView) mFragmentView.findViewById(R.id.usercenter_title);
            mTextViewArray[USER_CENTER].setSelected(true);
            mTextViewArray[USER_CENTER].setOnClickListener(this);
            mTextViewArray[BOOK_SHELF] = (TextView) mFragmentView.findViewById(R.id.shelf_title);
            mTextViewArray[BOOK_SHELF].setOnClickListener(this);
//            mTitltBottomViewArray[USER_CENTER] = mFragmentView.findViewById(R.id.usercenter_title_bottom);
//            mTitltBottomViewArray[BOOK_SHELF] = mFragmentView.findViewById(R.id.shelf_title_bottom);
            mViewPager = (ViewPager) mFragmentView.findViewById(R.id.mine_viewpager);
            mFragmentList.add(BookShelfFragment.newFragment());
            mUserCenterFragment = UserCenterFragment.newFragment();
            mUserCenterFragment.setActionCode(mActionCode);
            mFragmentList.add(mUserCenterFragment);
            mViewPager.setAdapter(new FragmentAdapter(getChildFragmentManager(), mFragmentList));
            mViewPager.setOnPageChangeListener(this);
            setNowItem(BOOK_SHELF);
        }
        if (mFragmentView.getParent() != null) {
            ((ViewGroup) mFragmentView.getParent()).removeAllViews();
        }
        if (mCurrentItem == USER_CENTER) {
            mViewPager.setCurrentItem(USER_CENTER);
            setNowItem(USER_CENTER);
        } else {
            mViewPager.setCurrentItem(BOOK_SHELF);
            setNowItem(BOOK_SHELF);
        }
        return mFragmentView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shelf_title:
                mViewPager.setCurrentItem(BOOK_SHELF);
                break;
            case R.id.usercenter_title:
                mViewPager.setCurrentItem(USER_CENTER);
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
            case BOOK_SHELF:
                mTextViewArray[BOOK_SHELF].setSelected(true);
                mTextViewArray[USER_CENTER].setSelected(false);
//                mTitltBottomViewArray[BOOK_SHELF].setBackgroundDrawable(getRepeatLine());
//                mTitltBottomViewArray[USER_CENTER].setBackgroundResource(R.drawable.top_shadow);
                break;
            case USER_CENTER:
                mTextViewArray[USER_CENTER].setSelected(true);
                mTextViewArray[BOOK_SHELF].setSelected(false);
//                mTitltBottomViewArray[USER_CENTER].setBackgroundDrawable(getRepeatLine());
//                mTitltBottomViewArray[BOOK_SHELF].setBackgroundResource(R.drawable.top_shadow);
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

    public void setActionCode(int actionCode) {
        if (mUserCenterFragment == null) {
            mActionCode = actionCode;
        } else {
            mUserCenterFragment.setActionCode(actionCode);
            showUserCenter();
        }
    }

    private void showUserCenter() {
        mViewPager.setCurrentItem(USER_CENTER);
    }
}
