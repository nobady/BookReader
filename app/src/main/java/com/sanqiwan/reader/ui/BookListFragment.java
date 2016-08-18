package com.sanqiwan.reader.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.adapter.BucketListAdapter;
import com.sanqiwan.reader.adapter.OnlineBooksAdapter;
import com.sanqiwan.reader.data.Categories;
import com.sanqiwan.reader.data.CategoryItem;
import com.sanqiwan.reader.data.XBookManager;
import com.sanqiwan.reader.model.BookItem;
import com.sanqiwan.reader.preload.HotBookLoadTask;
import com.sanqiwan.reader.preload.LatestBookLoadTask;
import com.sanqiwan.reader.preload.OnlineBookLoadTask;
import com.sanqiwan.reader.preload.ScenarioBookLoadTask;
import com.sanqiwan.reader.pulltorefresh.library.PullToRefreshBase;
import com.sanqiwan.reader.pulltorefresh.library.PullToRefreshListView;
import com.sanqiwan.reader.track.UMengEventId;
import com.sanqiwan.reader.util.AsyncTaskUtil;
import com.sanqiwan.reader.view.HorizontalListLayout;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 9/22/13
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class BookListFragment extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener, PullToRefreshListView.OnRefreshListener {

    public static final int FRAGMENT_CATEGORY = 0;
    public static final int FRAGMENT_CHOICE = 1;
    public static final int FRAGMENT_NEW = 2;
    private static final String FRAGMENT_TYPE = "fragment_type";
    private static final String CATEGORY_ID = "category_id";
    private static final String CATEGORY_ITEM = "category_item";

    private int mFragmentType;
    private int mCategoryId;
    private List<CategoryItem> mCategories;
    private PullToRefreshListView mBookListView;
    private HorizontalListLayout mHorizontalListView;
    private View mView;
    private OnlineBookLoadTask mLatestBookLoadTask;
    private int mLatestBooksPageIndex = 1;
    private OnlineBooksAdapter mLatestBooksAdapter;
    private LayoutInflater mInflater;
    private String mType;
    private CategoryItem mCategoryItem;
    private int mScenarioId;
    private CategoriesAdapter mHorizonListAdapter;
    private LinearLayout mErrorLayout;
    private Button mRetryBtn;
    private XBookManager mXBookManager;

    private OnlineBookLoadTask.OnlineBookCallback mOnlineBookCallback = new OnlineBookLoadTask.OnlineBookCallback() {
        @Override
        public void onOnlineBookLoaded(List<BookItem> onlineBookItemList) {
            if (mBookListView.isRefreshing()) {
                mBookListView.onRefreshComplete();
                mLatestBooksAdapter.clear();
            }
            if (onlineBookItemList.size() > 0) {
                mErrorLayout.setVisibility(View.GONE);
                mLatestBooksPageIndex++;
                for (BookItem item : onlineBookItemList) {
                    if (!mXBookManager.hasBook(item.getBookId())) {
                        mLatestBooksAdapter.add(item);
                    }
                }
            } else {
                mLatestBooksAdapter.disableLoadMore();
                if (mLatestBooksAdapter.getElementCount() == 0) {
                    mErrorLayout.setVisibility(View.VISIBLE);
                    return;
                }
            }

        }

        @Override
        public void onOnlineBookLoadFailed() {
            if (mBookListView.isRefreshing()) {
                mBookListView.onRefreshComplete();
            } else {
                mLatestBooksAdapter.disableLoadMore();
                if (mLatestBooksPageIndex <= 1) {
                    mErrorLayout.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    public static BookListFragment newFragment(int fragmentType, int categoryId, CategoryItem categoryItem) {
        BookListFragment fragment = new BookListFragment();
        Bundle args = new Bundle();
        args.putInt(FRAGMENT_TYPE, fragmentType);
        args.putInt(CATEGORY_ID, categoryId);
        args.putParcelable(CATEGORY_ITEM, categoryItem);
        // FIXME: if the parent fragment is null
        fragment.setArguments(args);
        return fragment;
    }

    private void takeArguments() {
        Bundle args = getArguments();
        if (args != null) {
            mFragmentType = args.getInt(FRAGMENT_TYPE);
            mCategoryId = args.getInt(CATEGORY_ID);
            mCategoryItem = args.getParcelable(CATEGORY_ITEM);
            mScenarioId = mCategoryItem.getId();
            setType();
        }
    }

    /**
     * 覆盖此函数，先通过inflater inflate函数得到view最后返回
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        takeArguments();

        if (mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            parent.removeView(mView);
            return mView;
        }
        mInflater = inflater;
        mView = mInflater.inflate(R.layout.book_list_fragment, null);
        mBookListView = (PullToRefreshListView) mView.findViewById(R.id.grid_book_list);
        mBookListView.setOnItemClickListener(this);
        mBookListView.setOnRefreshListener(this);
        mErrorLayout = (LinearLayout) mView.findViewById(R.id.error_layout);
        mRetryBtn = (Button) mErrorLayout.findViewById(R.id.retry_btn);
        mRetryBtn.setOnClickListener(this);
        mHorizontalListView = (HorizontalListLayout) mView.findViewById(R.id.horizontal_listview);
        mXBookManager = new XBookManager();
        initTop();
        initListView();
        return mView;
    }

    private void initTop() {
        if (mFragmentType != FRAGMENT_CATEGORY) {
            mHorizontalListView.setVisibility(View.GONE);
            return;
        }
        showCategories();
    }

    private void showCategories() {
        mCategories = Categories.categories.get(mCategoryId);
        mHorizonListAdapter = new CategoriesAdapter(mCategories);
        mHorizontalListView.setAdapter(mHorizonListAdapter);
        mHorizontalListView.setSelection(mCategoryItem.getIndex());
        mHorizonListAdapter.setSelected(mCategoryItem.getIndex());
    }

    private void initListView() {
        mLatestBooksAdapter = new OnlineBooksAdapter(getContext());
        mBookListView.setAdapter(mLatestBooksAdapter);
        mLatestBooksAdapter.setLoadMoreListener(mLoadMoreBooksListener);
    }

    private BucketListAdapter.LoadMoreListener mLoadMoreBooksListener = new BucketListAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            loadMoreBooks(mCategoryId, mLatestBooksPageIndex);
        }
    };

    private void loadMoreBooks(int category, int pageIndex) {
        if (mLatestBookLoadTask == null || mLatestBookLoadTask.getStatus() == AsyncTask.Status.FINISHED) {
            mLatestBookLoadTask = createOnlineBookloadTask();
            AsyncTaskUtil.execute(mLatestBookLoadTask);
        }
    }

    private void setType() {
        if (mFragmentType == FRAGMENT_CHOICE) {
            mType = "m";
        }
    }

    @Override
    public void onClick(View v) {
        mErrorLayout.setVisibility(View.GONE);
        if (v.getId() == R.id.retry_btn) {
            mLatestBooksAdapter.enableLoadMore();
            mLoadMoreBooksListener.onLoadMore();
        } else {
            CategoryItem categoryItem = (CategoryItem) v.getTag();
            if (categoryItem == null) {
                return;
            }
            if (mScenarioId != categoryItem.getId()) {
                mScenarioId = categoryItem.getId();
                mLatestBooksPageIndex = 1;
                mLatestBooksAdapter.clear();
                mHorizonListAdapter.setSelected(categoryItem.getIndex());
                mLatestBooksAdapter.enableLoadMore();
                mLoadMoreBooksListener.onLoadMore();
                MobclickAgent.onEvent(getContext(), UMengEventId.UMENG_TRACK_EVENT, Categories.getCategoryItem(mCategoryId, mCategoryItem.getId()));
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position -= mBookListView.getRefreshableView().getHeaderViewsCount();
        if (position >= 0 && position < mLatestBooksAdapter.getElementCount()) {
            BookItem item = mLatestBooksAdapter.getElement(position);
            MainActivity.openSubFragment(BookDetailFragment.newFragment(item.getBookId(), item.getBookName(),
                    item.isFinish()));
        }
    }

    private OnlineBookLoadTask createOnlineBookloadTask() {
        switch (mFragmentType) {
            case FRAGMENT_CATEGORY:
                return new ScenarioBookLoadTask(mCategoryId, mScenarioId, mLatestBooksPageIndex, mOnlineBookCallback);
            case FRAGMENT_CHOICE:
                return new HotBookLoadTask(mCategoryId, mType, mLatestBooksPageIndex, mOnlineBookCallback);
            case FRAGMENT_NEW:
                return new LatestBookLoadTask(mCategoryId, mLatestBooksPageIndex, mOnlineBookCallback);
            default:
                return null;
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        mLatestBooksPageIndex = 1;
        String label = DateUtils.formatDateTime(getContext(), System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
        mErrorLayout.setVisibility(View.GONE);
        AsyncTaskUtil.execute(createOnlineBookloadTask());
    }

    private class CategoriesAdapter extends BaseAdapter {
        private List<CategoryItem> mCategories;
        private int mSelectedPosition;

        public CategoriesAdapter(List<CategoryItem> categories) {
            mCategories = categories;
        }

        @Override
        public int getCount() {
            return mCategories.size();
        }

        @Override
        public Object getItem(int position) {
            return mCategories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mCategories.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.scen_item, null);
            }
            TextView view = (TextView) convertView.findViewById(R.id.textview);
            view.setTag(mCategories.get(position));
            view.setOnClickListener(BookListFragment.this);
            view.setText(mCategories.get(position).getName());
            if (position == mSelectedPosition) {
                view.setSelected(true);
            } else {
                view.setSelected(false);
            }
            return convertView;
        }

        public void setSelected(int selectedPosition) {
            mSelectedPosition = selectedPosition;
            this.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDetach();
        if (mLatestBookLoadTask != null &&
                (mLatestBookLoadTask.getStatus() == AsyncTask.Status.PENDING ||
                        mLatestBookLoadTask.getStatus() == AsyncTask.Status.PENDING)) {
            mLatestBookLoadTask.cancel(true);
        }
    }
}
