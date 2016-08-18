package com.sanqiwan.reader.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.adapter.BookVolumeAdapter;
import com.sanqiwan.reader.adapter.BucketListAdapter;
import com.sanqiwan.reader.model.TOC;
import com.sanqiwan.reader.model.VolumeItem;
import com.sanqiwan.reader.preload.TOCLoadTask;
import com.sanqiwan.reader.util.AsyncTaskUtil;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-23
 * Time: 下午6:21
 * To change this template use File | Settings | File Templates.
 */
public class BookVolumeFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String BOOK_ID = "book_id";
    private ViewGroup mTopView;
    private TextView mVolumeTitle;
    private long mBookId;
    private View mFragmentView;

    private ListView mBookVolumes;
    private TextView mEmptyView;
    private BookVolumeAdapter mVolumeAdapter;
    private Context mContext;
    private int mPageIndex = 1;
    private TOCLoadTask mTOCLoadTask;

    public static BookVolumeFragment newFragment(long bookId) {
        BookVolumeFragment fragment = new BookVolumeFragment();
        Bundle args = new Bundle();
        args.putLong(BOOK_ID, bookId);
        fragment.setArguments(args);
        return fragment;
    }

    private void takeArguments() {
        Bundle args = getArguments();
        if (args != null) {
            mBookId = args.getLong(BOOK_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        takeArguments();

        mContext = getContext();
        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.volume, container, false);

            initTopView();
            mBookVolumes = (ListView) mFragmentView.findViewById(R.id.book_volumes);
            mEmptyView = (TextView) mFragmentView.findViewById(R.id.empty_view);
            mVolumeAdapter = new BookVolumeAdapter(mContext);
            mBookVolumes.setEmptyView(mEmptyView);
            mBookVolumes.setOnItemClickListener(this);
            //加载目录
            mVolumeAdapter.setLoadMoreListener(mLoadMoreVolumesListener);
            mBookVolumes.setAdapter(mVolumeAdapter);
        }
        if (mFragmentView.getParent() != null) {
            ((ViewGroup) mFragmentView.getParent()).removeAllViews();
        }
        return mFragmentView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mVolumeAdapter.getTOC() == null) {
            return;
        }
        VolumeItem item = mVolumeAdapter.getTOC().getItem(position);
        if (item != null) {
            ReaderActivity.openBook(mContext,
                    mBookId, item.getId());
        }
    }

    private BucketListAdapter.LoadMoreListener mLoadMoreVolumesListener = new BucketListAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            loadMoreVolumes();
        }
    };

    private void loadMoreVolumes() {
        if (mBookId == 0) {
            mVolumeAdapter.disableLoadMore();
            mEmptyView.setText(getString(R.string.no_volume));
            return;
        }

        if (mTOCLoadTask == null || mTOCLoadTask.getStatus() == AsyncTask.Status.FINISHED) {
            mTOCLoadTask = new TOCLoadTask(mBookId, mPageIndex, new TOCLoadTask.Callback() {
                @Override
                public void onTOCLoaded(TOC toc) {
                    if (getActivity() == null) {
                        return;
                    }
                    if (toc != null && toc.getSize() > 0) {
                        mPageIndex++;
                        mVolumeAdapter.addTOC(toc);
                    } else {
                        mVolumeAdapter.disableLoadMore();
                    }
                }

                @Override
                public void onTOCLoadFailed(long bookId) {
                    if (getActivity() == null) {
                        return;
                    }
                    mVolumeAdapter.disableLoadMore();
                }
            });
            AsyncTaskUtil.execute(mTOCLoadTask);
        }
    }

    private void initTopView() {
        mTopView = (ViewGroup) mFragmentView.findViewById(R.id.volume_top_bar);
        mVolumeTitle = (TextView) mTopView.findViewById(R.id.top_title);
        mVolumeTitle.setText(R.string.directory);
        mTopView.findViewById(R.id.btn_return).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_return:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

}