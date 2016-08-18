package com.sanqiwan.reader.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.data.Categories;
import com.sanqiwan.reader.data.CategoryItem;
import com.sanqiwan.reader.track.UMengEventId;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * User: sam
 * Date: 9/22/13
 * Time: 2:18 PM
 */
public class OnlineBookListFragment extends BaseFragment implements View.OnClickListener {

    public static final String TYPE_MONTH = "m";
    private static final int CATEGORY = 0;
    private static final int NEW = 1;
    private static final int CHOICE = 2;
    private static final String CATEGORY_ID = "category_id";
    private static final String TYPE = "type";
    private static final String CATEGORY_ITEM = "category_item";
    /**
     * 页面list *
     */
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private ViewPager mViewPager;
    private TextView[] mTextViewArray = new TextView[3];
    private int mCategoryId;
    private CategoryItem mCategoryItem;
    private String mType;
    private ImageView mBackBtn;
    private TextView mTitleTextView;
    private RelativeLayout mTopBar;
    private Context mContext;
    private View mFragmentView;

    public static OnlineBookListFragment newFragment(int categoryId, String type, CategoryItem categoryItem) {
        OnlineBookListFragment fragment = new OnlineBookListFragment();
        Bundle args = new Bundle();
        args.putInt(CATEGORY_ID, categoryId);
        args.putString(TYPE, type);
        args.putParcelable(CATEGORY_ITEM, categoryItem);
        fragment.setArguments(args);

        return fragment;
    }

    private void takeArguments() {
        Bundle args = getArguments();
        if (args != null) {
            mCategoryId = args.getInt(CATEGORY_ID);
            mType = args.getString(TYPE);
            mCategoryItem = args.getParcelable(CATEGORY_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        takeArguments();
        mContext = getContext();
        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.book_list_layout, container, false);
            mViewPager = (ViewPager) mFragmentView.findViewById(R.id.viewpager);

            mFragmentList.add(BookListFragment.newFragment(BookListFragment.FRAGMENT_CATEGORY, mCategoryId, mCategoryItem));

            mFragmentList.add(BookListFragment.newFragment(BookListFragment.FRAGMENT_CHOICE, mCategoryId, mCategoryItem));

            mFragmentList.add(BookListFragment.newFragment(BookListFragment.FRAGMENT_NEW, mCategoryId, mCategoryItem));

            mViewPager.setAdapter(new myPagerAdapter(getChildFragmentManager(), mFragmentList));
            mTextViewArray[CATEGORY] = (TextView) mFragmentView.findViewById(R.id.text_type);
            mTextViewArray[NEW] = (TextView) mFragmentView.findViewById(R.id.text_choice);
            mTextViewArray[CHOICE] = (TextView) mFragmentView.findViewById(R.id.text_new);
            mTextViewArray[CATEGORY].setSelected(true);
            mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
            for (TextView tv : mTextViewArray) {
                tv.setOnClickListener(this);
            }
            initTopBar();
        }
        if (mFragmentView.getParent() != null) {
            ((ViewGroup) mFragmentView.getParent()).removeAllViews();
        }
        return mFragmentView;
    }

    private void initTopBar() {
        mTopBar = (RelativeLayout) mFragmentView.findViewById(R.id.top_bar);
        mBackBtn = (ImageView) mTopBar.findViewById(R.id.btn_return);
        mBackBtn.setOnClickListener(this);
        mTitleTextView = (TextView) mTopBar.findViewById(R.id.top_title);
        mTitleTextView.setText(getTopBarTitle());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_type:
                umengAnalysis(BookListFragment.FRAGMENT_CATEGORY);
                mViewPager.setCurrentItem(BookListFragment.FRAGMENT_CATEGORY);
                break;
            case R.id.text_choice:
                umengAnalysis(BookListFragment.FRAGMENT_CHOICE);
                mViewPager.setCurrentItem(BookListFragment.FRAGMENT_CHOICE);
                break;
            case R.id.text_new:
                umengAnalysis(BookListFragment.FRAGMENT_NEW);
                mViewPager.setCurrentItem(BookListFragment.FRAGMENT_NEW);
                break;
            case R.id.btn_return:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

    private String getTopBarTitle() {
        switch (mCategoryId) {
            case Categories.CATEGORY_CITY:
                return getString(R.string.city);
            case Categories.CATEGORY_ANCIENT:
                return getString(R.string.ancient);
            case Categories.CATEGORY_ACROSS:
                return getString(R.string.across);
            case Categories.CATEGORY_FANTASY:
                return getString(R.string.fantasy);
        }
        return getString(R.string.across);
    }


    /**
     * 定义适配器
     */
    class myPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList;

        public myPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        /**
         * 得到每个页面
         */
        @Override
        public Fragment getItem(int arg0) {
            return (fragmentList == null || fragmentList.size() == 0) ? null : fragmentList.get(arg0);
        }

        /**
         * 页面的总个数
         */
        @Override
        public int getCount() {
            return fragmentList == null ? 0 : fragmentList.size();
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            umengAnalysis(position);
            for (int i = 0; i < mTextViewArray.length; i++) {
                mTextViewArray[i].setSelected(false);
                if (i == position) {
                    mTextViewArray[i].setSelected(true);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    private void umengAnalysis(int viewId) {
        if (viewId == BookListFragment.FRAGMENT_CATEGORY) {
            MobclickAgent.onEvent(mContext, UMengEventId.UMENG_TRACK_EVENT, Categories.getCategoryItem(mCategoryId, FINISHED));
        } else if (viewId == BookListFragment.FRAGMENT_CHOICE) {
            MobclickAgent.onEvent(mContext, UMengEventId.UMENG_HOT_BOOK_EVENT, Categories.getCategory(mCategoryId));
        } else if (viewId == BookListFragment.FRAGMENT_NEW) {
            MobclickAgent.onEvent(mContext, UMengEventId.UMENG_LATEST_BOOK_EVENT, Categories.getCategory(mCategoryId));
        } else {

        }
    }

    private static final int FINISHED = 0;
}
