package com.sanqiwan.reader.ui;

import android.content.Context;
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
import com.sanqiwan.reader.adapter.RankBooksAdapter;
import com.sanqiwan.reader.model.BookItem;
import com.sanqiwan.reader.model.BookRecommendItem;
import com.sanqiwan.reader.model.RankItem;
import com.sanqiwan.reader.pulltorefresh.library.PullToRefreshBase;
import com.sanqiwan.reader.pulltorefresh.library.PullToRefreshListView;
import com.sanqiwan.reader.util.AsyncTaskUtil;
import com.sanqiwan.reader.util.DeviceUtil;
import com.sanqiwan.reader.util.UIUtil;
import com.sanqiwan.reader.webimageview.WebImageView;
import com.sanqiwan.reader.webservice.BookWebService;
import com.sanqiwan.reader.webservice.OperationWebService;
import com.sanqiwan.reader.webservice.RankListWebService;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

import java.util.List;

/**
 * User: sam
 * Date: 14-04-10
 */
public class RankBookListFragment extends BaseFragment implements BucketListAdapter.LoadMoreListener, AdapterView.OnItemClickListener, View.OnClickListener, PullToRefreshListView.OnRefreshListener {

    private final static String KEY_RANK = "rank";

    private View mView;
    private LinearLayout mErrorLayout;
    private PullToRefreshListView mListView;
    private ProgressBar mProgressBar;
    private RankBooksAdapter mAdapter;
    private Context mContext;
    private LoadMoreTask mLoadMoreTask;
    private int mPageNum = 1;
    private Button mRetryBtn;
    private RankListWebService mRankWebService;
    private RankItem mRankItem;
    private TextView mTopTitle;
    private ImageView mReturnImageView;


    public static RankBookListFragment newInstance(RankItem rank) {
        RankBookListFragment fragment = new RankBookListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_RANK, rank);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void takeArguments() {
        Bundle args = getArguments();
        if (args != null) {
            mRankItem = args.getParcelable(KEY_RANK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            takeArguments();
            mContext = getContext();
            mView = inflater.inflate(R.layout.rank_book_list_fragment_layout, container, false);
            mErrorLayout = (LinearLayout) mView.findViewById(R.id.error_layout);
            mListView = (PullToRefreshListView) mView.findViewById(R.id.new_finish_listview);
            mAdapter = new RankBooksAdapter(mContext);
            mListView.setAdapter(mAdapter);
            mListView.setOnRefreshListener(this);
            mListView.setOnItemClickListener(this);
            mProgressBar = (ProgressBar) mView.findViewById(R.id.progress_bar);
            mRetryBtn = (Button) mErrorLayout.findViewById(R.id.retry_btn);
            mRetryBtn.setOnClickListener(this);
            mTopTitle = (TextView) mView.findViewById(R.id.top_title);
            if (mRankItem != null) {
                mTopTitle.setText(mRankItem.getName());
            }
            mReturnImageView = (ImageView) mView.findViewById(R.id.btn_return);
            mReturnImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
            mRankWebService = new RankListWebService();
            AsyncTaskUtil.execute(new InitTask());
        }
        if (mView.getParent() != null) {
            ((ViewGroup) mView.getParent()).removeAllViews();
        }
        return mView;
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

    class InitTask extends AsyncTask<Void, Void, List<BookItem>> {
        private boolean mIsNetWorkException;
        public InitTask() {
            mIsNetWorkException = false;
        }

        @Override
        protected List<BookItem> doInBackground(Void... params) {
            mPageNum = 1;
            if (mRankItem == null ) {
                return null;
            }
            try {
                return mRankWebService.getRankBookList(mRankItem.getId(), mPageNum);
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

            if (bookItemList != null && !bookItemList.isEmpty()) {
                mAdapter.clear();
                mAdapter.addAll(bookItemList);
                mAdapter.setLoadMoreListener(RankBookListFragment.this);
                mPageNum++;
            }
        }
    }

    class LoadMoreTask extends AsyncTask<Void, Void, List<BookItem>> {

        @Override
        protected List<BookItem> doInBackground(Void... params) {
            if (mRankItem == null ) {
                return null;
            }
            try {
                return mRankWebService.getRankBookList(mRankItem.getId(), mPageNum);
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
