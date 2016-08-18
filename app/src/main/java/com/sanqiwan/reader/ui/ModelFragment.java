package com.sanqiwan.reader.ui;

import android.content.Context;
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
import com.sanqiwan.reader.model.BookItem;
import com.sanqiwan.reader.model.BookRecommendItem;
import com.sanqiwan.reader.pulltorefresh.library.PullToRefreshBase;
import com.sanqiwan.reader.pulltorefresh.library.PullToRefreshListView;
import com.sanqiwan.reader.util.AsyncTaskUtil;
import com.sanqiwan.reader.util.DeviceUtil;
import com.sanqiwan.reader.util.UIUtil;
import com.sanqiwan.reader.webimageview.WebImageView;
import com.sanqiwan.reader.webservice.BookWebService;
import com.sanqiwan.reader.webservice.OperationWebService;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jwb
 * Date: 13-11-21
 * Time: 上午10:26
 * To change this template use File | Settings | File Templates.
 */
public abstract class ModelFragment extends BaseFragment implements BucketListAdapter.LoadMoreListener, AdapterView.OnItemClickListener, View.OnClickListener, PullToRefreshListView.OnRefreshListener {

    private View mView;
    private View mHeadView;
    private LinearLayout mErrorLayout;
    private PullToRefreshListView mListView;
    private ProgressBar mProgressBar;
    private OnlineBooksAdapter mAdapter;
    private Context mContext;
    private WebImageView mLeftImageView, mRightImageView;
    private List<BookRecommendItem> mBookRecommendItemList;
    private LoadMoreTask mLoadMoreTask;
    private int mPageNum = 1;
    private static final int IMAGE_PADDING = UIUtil.dipToPixel(8);
    private static final int IMAGE_MARGIN = UIUtil.dipToPixel(7);
    private static final int LEFT_RECOMMEND = 0;
    private static final int RIGHT_RECOMMEND = 1;
    private LinearLayout mRecommends;
    private Button mRetryBtn;
    private BookWebService mBookWebService;
    private OperationWebService mOperationWebService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            mContext = getContext();
            mView = inflater.inflate(R.layout.new_finish_fragment_layout, container, false);
            mHeadView = inflater.inflate(R.layout.new_finish_head_view, null);
            mErrorLayout = (LinearLayout) mView.findViewById(R.id.error_layout);
            mLeftImageView = (WebImageView) mHeadView.findViewById(R.id.head_picture_left);
            mLeftImageView.setDefaultImageResource(R.drawable.recommend_default);
            mRightImageView = (WebImageView) mHeadView.findViewById(R.id.head_picture_right);
            mRightImageView.setDefaultImageResource(R.drawable.recommend_default);
            mRecommends = (LinearLayout) mHeadView.findViewById(R.id.top_recommends);
            setImageViewHeight();
            mListView = (PullToRefreshListView) mView.findViewById(R.id.new_finish_listview);
            mListView.getRefreshableView().addHeaderView(mHeadView);
            mAdapter = new OnlineBooksAdapter(mContext);
            mListView.setAdapter(mAdapter);
            mListView.setOnRefreshListener(this);
            mListView.setOnItemClickListener(this);
            mProgressBar = (ProgressBar) mView.findViewById(R.id.progress_bar);
            mRetryBtn = (Button) mErrorLayout.findViewById(R.id.retry_btn);
            mRetryBtn.setOnClickListener(this);
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
        mRecommends.setPadding(IMAGE_PADDING, 0, IMAGE_PADDING, IMAGE_MARGIN);
        mRecommends.setLayoutParams(leftParams);
    }

    @Override
    public void onLoadMore() {
        if (mLoadMoreTask == null || mLoadMoreTask.getStatus() == AsyncTask.Status.FINISHED) {
            mLoadMoreTask = new LoadMoreTask();
        }
        AsyncTaskUtil.execute(mLoadMoreTask);
    }

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
    public void onRefresh(PullToRefreshBase refreshView) {
        String label = DateUtils.formatDateTime(getContext(), System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
        mErrorLayout.setVisibility(View.GONE);
        AsyncTaskUtil.execute(new InitTask());
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position -= mListView.getRefreshableView().getHeaderViewsCount();
        if (position >= 0 && position < mAdapter.getElementCount()) {
            BookItem bookItem = mAdapter.getElement(position);
            openBookDetailFragment(bookItem.getBookId(), bookItem.getBookName(),
                    bookItem.isFinish());
        }
    }

    private void openBookDetailFragment(long bookId, String bookName, boolean isFinish) {
        MainActivity.openSubFragment(BookDetailFragment.newFragment(bookId, bookName, isFinish));
    }

    public abstract List<BookRecommendItem> getBookRecommendItemList(OperationWebService operationWebService) throws ZLNetworkException;
    public abstract List<BookItem> getBookItemList(BookWebService bookWebService, int pageNum) throws ZLNetworkException;

    class InitTask extends AsyncTask<Void, Void, List<BookItem>> {
        private boolean mIsNetWorkException;
        public InitTask() {
            mIsNetWorkException = false;
        }

        @Override
        protected List<BookItem> doInBackground(Void... params) {
            mBookRecommendItemList = null;
            mPageNum = 1;
            try {
                mBookRecommendItemList = getBookRecommendItemList(mOperationWebService);
                return getBookItemList(mBookWebService, mPageNum);
            } catch (ZLNetworkException e) {
                mIsNetWorkException = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<BookItem> bookItemList) {
            mProgressBar.setVisibility(View.GONE);
            if (mListView.isRefreshing()) {
                mListView.onRefreshComplete();
            }
            if (mIsNetWorkException && mAdapter.getCount() == 0) {
                mErrorLayout.setVisibility(View.VISIBLE);
                return;
            }
            if (bookItemList != null && bookItemList.isEmpty() && mAdapter.getCount() == 0) {
                mErrorLayout.setVisibility(View.VISIBLE);
                return;
            }
            if (mBookRecommendItemList != null && mBookRecommendItemList.size() >= 2) {
                mRecommends.setVisibility(View.VISIBLE);
                mLeftImageView.setImageUrl(mBookRecommendItemList.get(LEFT_RECOMMEND).getPictureUrl());
                mLeftImageView.setTag(mBookRecommendItemList.get(LEFT_RECOMMEND));
                mLeftImageView.setOnClickListener(ModelFragment.this);
                mRightImageView.setImageUrl(mBookRecommendItemList.get(RIGHT_RECOMMEND).getPictureUrl());
                mRightImageView.setTag(mBookRecommendItemList.get(RIGHT_RECOMMEND));
                mRightImageView.setOnClickListener(ModelFragment.this);
            }
            if (bookItemList != null && !bookItemList.isEmpty()) {
                mAdapter.clear();
                mAdapter.addAll(bookItemList);
                mAdapter.setLoadMoreListener(ModelFragment.this);
                mPageNum++;
            }
        }
    }

    class LoadMoreTask extends AsyncTask<Void, Void, List<BookItem>> {

        @Override
        protected List<BookItem> doInBackground(Void... params) {
            try {
                return getBookItemList(mBookWebService, mPageNum);
            } catch (ZLNetworkException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<BookItem> bookItemList) {
            if (bookItemList != null && !bookItemList.isEmpty()) {
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
