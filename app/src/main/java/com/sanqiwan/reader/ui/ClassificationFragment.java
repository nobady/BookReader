package com.sanqiwan.reader.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.data.Categories;
import com.sanqiwan.reader.data.CategoryItem;
import com.sanqiwan.reader.view.ExpandableItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-21
 * Time: 下午6:57
 * To change this template use File | Settings | File Templates.
 */
public class ClassificationFragment extends BaseFragment implements View.OnClickListener{

    private View mView, mHeadView;
    private ExpandableListView mExpandableListView;
    private List<String> mTitleList;
    private Context mContext;
    private int[] mBackgroundList = {R.drawable.city_bg, R.drawable.ancient_bg, R.drawable.across_bg, R.drawable.fantasy_bg};
    private ImageView mCityImageView, mAncientImageView, mAcrossImageView, mFantasyImageView;

    public static ClassificationFragment newFragment() {
        return new ClassificationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        if (mView == null) {
            mView = inflater.inflate(R.layout.classification_fragment, container, false);
            mHeadView = inflater.inflate(R.layout.classification_head_view, null);
            mCityImageView = (ImageView) mHeadView.findViewById(R.id.img_city);
            mCityImageView.setOnClickListener(this);
            mAncientImageView = (ImageView) mHeadView.findViewById(R.id.img_ancient);
            mAncientImageView.setOnClickListener(this);
            mAcrossImageView = (ImageView) mHeadView.findViewById(R.id.img_across);
            mAcrossImageView.setOnClickListener(this);
            mFantasyImageView = (ImageView) mHeadView.findViewById(R.id.img_fantasy);
            mFantasyImageView.setOnClickListener(this);
            mTitleList = new ArrayList<String>();
            if (getActivity() != null) {
                mTitleList.add(mContext.getString(R.string.city));
                mTitleList.add(mContext.getString(R.string.ancient));
                mTitleList.add(mContext.getString(R.string.across));
                mTitleList.add(mContext.getString(R.string.fantasy));
            }
            mExpandableListView = (ExpandableListView) mView.findViewById(R.id.expandablelistview);
            mExpandableListView.addHeaderView(mHeadView);
            mExpandableListView.setAdapter(new ExpandableAdapter());
            for (int i = 0; i < Categories.categories.size(); i++) {
                mExpandableListView.expandGroup(i);
            }
        }
        if (mView.getParent() != null) {
            ((ViewGroup) mView.getParent()).removeAllViews();
        }
        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_city:
                openOnlineBookListFragment(Categories.CATEGORY_CITY,
                        OnlineBookListFragment.TYPE_MONTH, Categories.categories.get(Categories.CATEGORY_CITY).get(0));
                break;
            case R.id.img_ancient:
                openOnlineBookListFragment(Categories.CATEGORY_ANCIENT,
                        OnlineBookListFragment.TYPE_MONTH, Categories.categories.get(Categories.CATEGORY_ANCIENT).get(0));
                break;
            case R.id.img_across:
                openOnlineBookListFragment(Categories.CATEGORY_ACROSS,
                        OnlineBookListFragment.TYPE_MONTH, Categories.categories.get(Categories.CATEGORY_ACROSS).get(0));
                break;
            case R.id.img_fantasy:
                openOnlineBookListFragment(Categories.CATEGORY_FANTASY,
                        OnlineBookListFragment.TYPE_MONTH, Categories.categories.get(Categories.CATEGORY_FANTASY).get(0));
                break;
        }
    }

    private void openOnlineBookListFragment(int categoryId, String type, CategoryItem categoryItem) {
        OnlineBookListFragment bookListFragment = OnlineBookListFragment.newFragment(categoryId, type, categoryItem);
        MainActivity.openSubFragment(bookListFragment);
    }

    private int getCategoryId(int position) {
        switch (position) {
            case 0:
                return Categories.CATEGORY_CITY;
            case 1:
                return Categories.CATEGORY_ANCIENT;
            case 2:
                return Categories.CATEGORY_ACROSS;
            case 3:
                return Categories.CATEGORY_FANTASY;
        }
        return 0;
    }

    class ExpandableAdapter extends BaseExpandableListAdapter {

        public ExpandableAdapter() {

        }

        @Override
        public int getGroupCount() {
            return Categories.categories.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.group_view, null);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.group_view);
            textView.setText(mTitleList.get(groupPosition));
            textView.setBackgroundResource(mBackgroundList[groupPosition]);
            textView.setSelected(isExpanded);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.classification_item, null);
            }
            ExpandableItemView expandableItemView = (ExpandableItemView) convertView.findViewById(R.id.expandable_item);
            final int categoryId = getCategoryId(groupPosition);
            List<CategoryItem> itemList = new ArrayList<CategoryItem>(Categories.categories.get(categoryId));
            if (itemList.get(0).getIndex() == 0) {
                itemList.remove(0);
            }
            expandableItemView.setCategorys(itemList, categoryId);
            expandableItemView.setOpenBookListFragmentListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CategoryItem categoryItem = (CategoryItem) v.getTag();
                    openOnlineBookListFragment(categoryId, OnlineBookListFragment.TYPE_MONTH, categoryItem);
                }
            });
            return convertView;
        }


        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}