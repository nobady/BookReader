package com.sanqiwan.reader.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import com.sanqiwan.reader.util.UIUtil;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 11/21/13
 * Time: 9:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class HorizontalListLayout extends HorizontalScrollView {

    private static final int SPACE = 10;
    private ListAdapter mAdapter;
    private DataSetObserver mDataSetObserver;
    private LinearLayout mContainer;
    private View mEmptyView;
    private int mSelection;
    private boolean mIsSelection = false;

    public HorizontalListLayout(Context context) {
        super(context);
        init(context);
    }

    public HorizontalListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContainer = new LinearLayout(context);
        setHorizontalScrollBarEnabled(false);
        addView(mContainer, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mIsSelection) {
            scrollTo(getDistance(), 0);
            mIsSelection = false;
        }
    }

    public void setSelection(int selection) {
        mSelection = selection;
        mIsSelection = true;
    }

    private int getDistance() {
        int distance = 0;
        if (mSelection > 0 && mSelection < mContainer.getChildCount()) {
            for (int i = 0; i < mSelection; i++) {
                View view = mContainer.getChildAt(i);
                distance += view.getMeasuredWidth();
            }
        }
        return distance;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // FIXME: 应该只反拦截水平方向的手式
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(event);
    }

    public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        resetList();
        mAdapter = adapter;
        if (mAdapter != null) {
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
        }
        setupChildren();
        requestLayout();
    }

    private void resetList() {
        mContainer.removeAllViews();
    }

    private void setupChildren() {
        if (mAdapter != null) {
            int margin = UIUtil.dipToPixel(SPACE);
            int count = mAdapter.getCount();
            for (int i = 0; i < count; i++) {
                View child = mAdapter.getView(i, null, mContainer);
                if (i != count - 1) {
                    ViewGroup.LayoutParams params = child.getLayoutParams();
                    if (params != null && params instanceof MarginLayoutParams) {
                        ((MarginLayoutParams) params).rightMargin = margin;
                    }
                }
                mContainer.addView(child);
            }
        }
        updateEmptyViewState();
    }

    private void updateEmptyViewState() {
        if (mContainer.getChildCount() == 0) {
            if (mEmptyView != null) {
                mEmptyView.setVisibility(VISIBLE);
            }
        } else {
            if (mEmptyView != null) {
                mEmptyView.setVisibility(GONE);
            }
        }
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
        updateEmptyViewState();
    }

    class AdapterDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            resetList();
            setupChildren();
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            resetList();
        }
    }
}
