package com.sanqiwan.reader.apps;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.adapter.BucketListAdapter;
import com.sanqiwan.reader.pulltorefresh.library.PullToRefreshBase;
import com.sanqiwan.reader.pulltorefresh.library.PullToRefreshListView;
import com.sanqiwan.reader.ui.BaseFragment;
import com.sanqiwan.reader.track.UMengEventId;
import com.sanqiwan.reader.util.UIUtil;
import com.sanqiwan.reader.view.ErrorView;
import com.sanqiwan.reader.view.LoadingView;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class AppListFragment extends BaseFragment implements PullToRefreshListView.OnRefreshListener, View.OnClickListener {

    private static final String FRAGMENT_TYPE = "fragment_type";
    private static final String ID = "id";
    private static final String BANNER = "banner";
    private static final String DESCRIBE = "describe";

    private PullToRefreshListView mPullToRefreshListView;
    private AppListAdapter mAppListAdapter;
    private LoadingView mLoadingView;
    private ErrorView mErrorView;
    private View mView;
    private static final int PAGE_SIZE = 20;
    private Context mContext;
    private DataLoader<AppInfo> mDataLoader;
    private List<AppInfo> mAppInfoList;

    private Thread mGetDateThread;
    private Thread mRefreshThread;
    private Thread mLoadMoreThread;

    private Runnable mGetDataRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                mAppInfoList = mDataLoader.getData(0, PAGE_SIZE);
            } catch (Exception e) {
            } finally {
                UIUtil.getHandler().post(mInitViewRunnable);
            }
            mGetDateThread = null;
        }
    };

    private Runnable mRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                mAppInfoList = mDataLoader.getData(0, PAGE_SIZE);
            } catch (Exception e) {
            } finally {
                UIUtil.getHandler().post(mRefreshViewRunnable);
            }
            mRefreshThread = null;
        }
    };

    private Runnable mLoadMoreRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                mAppInfoList = mDataLoader.getData(getSince(), PAGE_SIZE);
            } catch (Exception e) {
            } finally {
                UIUtil.getHandler().post(mLoadMoreViewRunnable);
            }
            mLoadMoreThread = null;
        }
    };

    private Runnable mInitViewRunnable = new Runnable() {
        @Override
        public void run() {
            onInitView();
        }
    };

    private Runnable mRefreshViewRunnable = new Runnable() {
        @Override
        public void run() {
            onPullToRefreshView();
        }
    };

    private Runnable mLoadMoreViewRunnable = new Runnable() {
        @Override
        public void run() {
            onLoadMoreView();
        }
    };

    public static AppListFragment newFragment(int fragmentType, long id, String banner, String describe) {
        AppListFragment listFragment = new AppListFragment();
        Bundle arg = new Bundle();
        arg.putInt(FRAGMENT_TYPE, fragmentType);
        arg.putLong(ID, id);
        arg.putString(BANNER, banner);
        arg.putString(DESCRIBE, describe);
        listFragment.setArguments(arg);
        return listFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            Bundle arg = getArguments();
        }
        if (mView == null) {
            mContext = getContext();
            mView = inflater.inflate(R.layout.app_list_fragment, container, false);
            mPullToRefreshListView = (PullToRefreshListView) mView.findViewById(R.id.pull_refresh_list);
            mPullToRefreshListView.setOnRefreshListener(this);
            mDataLoader = new AppListLoader();
            mLoadingView = (LoadingView) mView.findViewById(R.id.loading_view);
            mErrorView = (ErrorView) mView.findViewById(R.id.error_view);
            mErrorView.setRetryBtnClickListenner(this);
            startGetDataThread();
        }
        if (mView.getParent() != null) {
            ((ViewGroup) mView.getParent()).removeAllViews();
        }
        return mView;
    }

    private void startGetDataThread() {
        if (mGetDateThread == null) {
            mGetDateThread = new Thread(mGetDataRunnable);
            mGetDateThread.start();
        }
    }

    private void onRetry() {
        mErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
        startGetDataThread();
    }

    @Override
    public void onClick(View v) {
        onRetry();
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
        if (mRefreshThread == null) {
            mRefreshThread = new Thread(mRefreshRunnable);
            mRefreshThread.start();
        }
    }

    private BucketListAdapter.LoadMoreListener mLoadMoreListener = new BucketListAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (mLoadMoreThread == null) {
                mLoadMoreThread = new Thread(mLoadMoreRunnable);
                mLoadMoreThread.start();
            }
        }
    };

    private int getSince() {
        int since = 0;
        if (mAppListAdapter != null) {
            since = mAppListAdapter.getElementCount();
        }
        return since;
    }

    private void onInitView() {
        mLoadingView.setVisibility(View.GONE);
        if (isNetworkExceptionOrEmptyView()) {
            return;
        }
        onInitGameListAdapter();
    }

    private void onPullToRefreshView() {
        if (!isNetworkExceptionOrEmptyView()) {
            mErrorView.setVisibility(View.GONE);
            if (mAppListAdapter != null) {
                mAppListAdapter.clear();
                mAppListAdapter.addAll(mAppInfoList);
                mAppListAdapter.enableLoadMore();
            } else {
                onInitGameListAdapter();
            }
        }
        mPullToRefreshListView.onRefreshComplete();
    }

    private void onLoadMoreView() {
        MobclickAgent.onEvent(mContext, UMengEventId.APP_LIST_LOAD_MORE);
        if (isNetworkExceptionOrEmptyView()) {
            if (mAppListAdapter != null) {
                mAppListAdapter.disableLoadMore();
            }
            return;
        }
        if (mAppInfoList.isEmpty() && mAppListAdapter != null) {
            mAppListAdapter.disableLoadMore();
        } else if (mAppListAdapter == null) {
            onInitGameListAdapter();
        } else {
            mAppListAdapter.addAll(mAppInfoList);
        }
    }

    private void onInitGameListAdapter() {
        mAppListAdapter = new AppListAdapter(mContext, mAppInfoList);
        mAppListAdapter.setLoadMoreListener(mLoadMoreListener);
        mPullToRefreshListView.setAdapter(mAppListAdapter);
    }

    private boolean isNetworkExceptionOrEmptyView() {
        if (mAppInfoList == null && mAppListAdapter != null) {    //有数据时发生网络异常，提示用户
            return true;
        }
        if (mAppInfoList == null) {   //没有数据时发生网络异常
            mErrorView.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
