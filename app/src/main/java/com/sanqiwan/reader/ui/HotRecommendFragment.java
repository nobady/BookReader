package com.sanqiwan.reader.ui;

import android.app.Activity;
import android.content.Context;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.adapter.BucketListAdapter;
import com.sanqiwan.reader.adapter.OnlineBooksAdapter;
import com.sanqiwan.reader.model.BookItem;
import com.sanqiwan.reader.model.BookRecommendItem;
import com.sanqiwan.reader.pulltorefresh.library.PullToRefreshBase;
import com.sanqiwan.reader.pulltorefresh.library.PullToRefreshListView;
import com.sanqiwan.reader.util.AsyncTaskUtil;
import com.sanqiwan.reader.util.DeviceUtil;
import com.sanqiwan.reader.util.UIUtil;
import com.sanqiwan.reader.view.TodayRecommendView;
import com.sanqiwan.reader.webimageview.WebImageView;
import com.sanqiwan.reader.webservice.BookWebService;
import com.sanqiwan.reader.webservice.OperationWebService;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jwb
 * Date: 13-11-21
 * Time: 上午10:23
 * To change this template use File | Settings | File Templates.
 */
public class HotRecommendFragment extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener, PullToRefreshListView.OnRefreshListener {

    private View mView;
    private View mHeadView;
    private LinearLayout mErrorLayout;
    private TodayRecommendView mTodayRecommendView;
    private WebImageView mLeftImageView, mRightImageView;
    private PullToRefreshListView mListView;
    private OnlineBooksAdapter mAdapter;
    private Context mContext;
    private LoadMoreTask mLoadMoreTask;
    private TextView mTodayRecommend, mHotRead;
    private ProgressBar mProgressBar;
    private BookWebService mBookWebService;
    private OperationWebService mOperationWebService;
    private int mPageNum = 1;
    private static final int IMAGE_PADDING = UIUtil.dipToPixel(8);
    private static final int IMAGE_MARGIN = UIUtil.dipToPixel(7);
    private static final int HOT_RECOMMEND = 1;
    private static final int LEFT_RECOMMEND = 0;
    private static final int RIGHT_RECOMMEND = 1;
    private LinearLayout mRecommends;

    private Button mRetryBtn;

    public static HotRecommendFragment newFragment() {
        return new HotRecommendFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("FragmentTest", "fragment HotRecommendFragment onCreate");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i("FragmentTest", "fragment HotRecommendFragment  onAttach");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("FragmentTest", "fragment  HotRecommendFragment onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("FragmentTest", "fragment HotRecommendFragment  onPause");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("FragmentTest", "fragment HotRecommendFragment  onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("FragmentTest", "fragment  HotRecommendFragment onStop");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("FragmentTest", "fragment HotRecommendFragment  onDetach");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("FragmentTest", "fragment  HotRecommendFragment onDestroyView");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("FragmentTest", "fragment  HotRecommendFragment onViewCreated");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("FragmentTest", "fragment  HotRecommendFragment onActivityCreated");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("FragmentTest", "fragment  HotRecommendFragment onDestroy");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("FragmentTest", "fragment  HotRecommendFragment onCreateView");
        mContext = getContext();
        if (mView == null) {
            mView = inflater.inflate(R.layout.hot_recommend_layout, container, false);
            mListView = (PullToRefreshListView) mView.findViewById(R.id.hot_recommend_listview);
            mErrorLayout = (LinearLayout) mView.findViewById(R.id.error_layout);
            mProgressBar = (ProgressBar) mView.findViewById(R.id.progress_bar);
            mRetryBtn = (Button) mErrorLayout.findViewById(R.id.retry_btn);
            mRetryBtn.setOnClickListener(this);
            mHeadView = inflater.inflate(R.layout.hot_recommend_head_layout, null, false);
            mRecommends = (LinearLayout) mHeadView.findViewById(R.id.recommend_layout);
            mLeftImageView = (WebImageView) mHeadView.findViewById(R.id.head_picture_left);
            mLeftImageView.setDefaultImageResource(R.drawable.recommend_default);
            mRightImageView = (WebImageView) mHeadView.findViewById(R.id.head_picture_right);
            mRightImageView.setDefaultImageResource(R.drawable.recommend_default);
            setImageViewHeight();
            mTodayRecommend = (TextView) mHeadView.findViewById(R.id.today_recommend_title);
            mHotRead = (TextView) mHeadView.findViewById(R.id.hot_read_title);
            mTodayRecommendView = (TodayRecommendView) mHeadView.findViewById(R.id.today_recommend);
            mTodayRecommendView.setOpenBookDetailListener(mOpenBookDetailListener);
            mListView.getRefreshableView().addHeaderView(mHeadView);
            mAdapter = new OnlineBooksAdapter(mContext);
            mListView.setOnRefreshListener(this);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(this);
            mBookWebService = new BookWebService();
            mOperationWebService = new OperationWebService();
            AsyncTaskUtil.execute(new InitTask());
        }
        if (mView.getParent() != null) {
            ((ViewGroup) mView.getParent()).removeAllViews();
        }
        return mView;
    }

    private int getImageViewHeight() {
        int deviceWidth = DeviceUtil.getDeviceWidth();
        return (int) (deviceWidth / 24) * 7;
    }

    private void setImageViewHeight() {
        LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getImageViewHeight());
        mRecommends.setLayoutParams(leftParams);
        mRecommends.setPadding(IMAGE_PADDING, 0, IMAGE_PADDING, IMAGE_MARGIN);
    }

    private BucketListAdapter.LoadMoreListener mLoadMoreListener = new BucketListAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (mLoadMoreTask == null || mLoadMoreTask.getStatus() == AsyncTask.Status.FINISHED) {
                mLoadMoreTask = new LoadMoreTask();
            }
            AsyncTaskUtil.execute(mLoadMoreTask);
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.retry_btn) {
            mErrorLayout.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            AsyncTaskUtil.execute(new InitTask());
        } else {
            BookRecommendItem bookRecommendItem = (BookRecommendItem) v.getTag();
            openBookDetailFragment(bookRecommendItem.getBookId(), null, false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position -= mListView.getRefreshableView().getHeaderViewsCount();
        if (position >= 0 && position < mAdapter.getElementCount()) {
            BookItem bookItem = mAdapter.getElement(position);
            openBookDetailFragment(bookItem.getBookId(), bookItem.getBookName(), bookItem.isFinish());
        }
    }

    private View.OnClickListener mOpenBookDetailListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            BookItem bookItem = (BookItem) v.getTag();
            openBookDetailFragment(bookItem.getBookId(), bookItem.getBookName(), bookItem.isFinish());
        }
    };

    private void openBookDetailFragment(long bookId, String bookName, boolean isFinish) {
        MainActivity.openSubFragment(BookDetailFragment.newFragment(bookId, bookName, isFinish));
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        String label = DateUtils.formatDateTime(getContext(), System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
        mErrorLayout.setVisibility(View.GONE);
        AsyncTaskUtil.execute(new InitTask());
    }

    public class InitTask extends AsyncTask<Void, Void, List<BookItem>> {

        private boolean mIsNetworkException = false;
        private List<BookItem> mTodayRecommendList;
        private List<BookRecommendItem> mBookRecommendItemList;

        @Override
        protected List<BookItem> doInBackground(Void... params) {
            mPageNum = 1;
            mBookRecommendItemList = null;
            try {
                mBookRecommendItemList = mOperationWebService.getBookRecommends(HOT_RECOMMEND);
                return mBookWebService.getHotReadBooks(mPageNum);
            } catch (ZLNetworkException e) {
                mIsNetworkException = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<BookItem> bookItems) {
            mProgressBar.setVisibility(View.GONE);
            if (mListView.isRefreshing()) {
                mListView.onRefreshComplete();
            }
            if (mIsNetworkException && mAdapter.getCount() == 0) {
                mErrorLayout.setVisibility(View.VISIBLE);
                return;
            }
            if (bookItems != null && bookItems.isEmpty() && mAdapter.getCount() == 0) {
                mErrorLayout.setVisibility(View.VISIBLE);
                return;
            }
            if (mBookRecommendItemList != null && mBookRecommendItemList.size() >= 2) {
                //todo 当服务器没有推荐位时的处理
                mRecommends.setVisibility(View.VISIBLE);
                mLeftImageView.setImageUrl(mBookRecommendItemList.get(LEFT_RECOMMEND).getPictureUrl());
                mLeftImageView.setTag(mBookRecommendItemList.get(LEFT_RECOMMEND));
                mLeftImageView.setOnClickListener(HotRecommendFragment.this);
                mRightImageView.setImageUrl(mBookRecommendItemList.get(RIGHT_RECOMMEND).getPictureUrl());
                mRightImageView.setTag(mBookRecommendItemList.get(RIGHT_RECOMMEND));
                mRightImageView.setOnClickListener(HotRecommendFragment.this);
            }
            if (bookItems != null && !bookItems.isEmpty() && bookItems.size() > 2) {
                mTodayRecommendList = bookItems.subList(0, 2);
                if (mTodayRecommendList != null && !mTodayRecommendList.isEmpty()) {
                    mTodayRecommend.setVisibility(View.VISIBLE);
                    mTodayRecommendView.setRecommends(mTodayRecommendList);
                    bookItems.remove(0);
                    bookItems.remove(0);
                }
                mHotRead.setVisibility(View.VISIBLE);
                mAdapter.clear();
                mAdapter.addAll(bookItems);
                mAdapter.setLoadMoreListener(mLoadMoreListener);
                mPageNum++;
            }
        }
    }

    public class LoadMoreTask extends AsyncTask<Void, Void, List<BookItem>> {
        @Override
        protected List<BookItem> doInBackground(Void... params) {
            try {
                return mBookWebService.getHotReadBooks(mPageNum);
            } catch (ZLNetworkException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<BookItem> bookItemList) {
            if (bookItemList != null) {
                mAdapter.addAll(bookItemList);
                mPageNum++;
            } else {
                mAdapter.disableLoadMore();
                if (mAdapter.getElementCount() > 0) {
                    Toast.makeText(mContext, getString(R.string.network_error_tips), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
