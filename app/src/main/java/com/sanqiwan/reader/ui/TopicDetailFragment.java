package com.sanqiwan.reader.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.adapter.OnlineBooksAdapter;
import com.sanqiwan.reader.model.BookDetail;
import com.sanqiwan.reader.model.BookItem;
import com.sanqiwan.reader.model.Topic;
import com.sanqiwan.reader.util.UIUtil;
import com.sanqiwan.reader.webimageview.WebImageView;
import com.sanqiwan.reader.webservice.BookWebService;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/22/13
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class TopicDetailFragment extends BaseFragment {
    private static final String TOPIC = "topic";
    private static final int LOAD_BOOK = 1;
    private static final int FINISH_LOAD_BOOK = 2;
    private Topic mTopic;
    private ListView mTopicListView;
    private WebImageView mBannerView;

    private TextView mTopTitle, mEmptyView;
    private ImageView mReturnImageView;
    private OnlineBooksAdapter mTopicAdapter;
    private Context mContext;
    private View mView;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == LOAD_BOOK && mTopicAdapter != null && msg.obj instanceof BookItem) {
                mTopicAdapter.add((BookItem) msg.obj);
            } else if (msg.what == FINISH_LOAD_BOOK) {
                if (mTopicAdapter.getElementCount() == 0) {
                    mEmptyView.setText(R.string.no_wifi);
                }
            }
        }
    };

    public static TopicDetailFragment newFragment(Topic topic) {
        TopicDetailFragment fragment = new TopicDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(TOPIC, topic);
        fragment.setArguments(args);
        return fragment;
    }

    private void takeArguments() {
        Bundle args = getArguments();
        if (args != null) {
            mTopic = args.getParcelable(TOPIC);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        takeArguments();
        mContext = getContext();
        if (mView == null) {
            mView = inflater.inflate(R.layout.topic_detail, null);
            initView();
            loadWebData();
        }
        if (mView.getParent() != null) {
            ((ViewGroup) mView.getParent()).removeAllViews();
        }
        return mView;

    }

    private void initView() {
        mTopicListView = (ListView) mView.findViewById(R.id.topic_detail_list);

        mEmptyView = (TextView) mView.findViewById(R.id.empty_view);
        mTopicListView.setEmptyView(mEmptyView);

        mTopicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position -= mTopicListView.getHeaderViewsCount();
                if (position >= 0 && position < mTopicAdapter.getElementCount()) {
                    openBookDetailFragment(position);
                }
            }
        });
        mBannerView = new WebImageView(mContext);
        mBannerView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, UIUtil.dipToPixel(160)));
        mBannerView.setScaleType(ImageView.ScaleType.FIT_XY);
        mBannerView.setDefaultImageResource(R.drawable.head_bg);
        mBannerView.setImageUrl(mTopic.getBannerUrl());
        mTopicListView.addHeaderView(mBannerView);
        mTopTitle = (TextView) mView.findViewById(R.id.top_title);
        mTopTitle.setText(mTopic.getTitle());
        mReturnImageView = (ImageView) mView.findViewById(R.id.btn_return);
        mReturnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void openBookDetailFragment(int position) {
        long bookId = mTopicAdapter.getElement(position).getBookId();
        String bookName = mTopicAdapter.getElement(position).getBookName();
        boolean isFinish = mTopicAdapter.getElement(position).isFinish();

        MainActivity.openSubFragment(BookDetailFragment.newFragment(bookId, bookName, isFinish));
    }

    private void loadWebData() {
        mTopicAdapter = new OnlineBooksAdapter(mContext);
        mTopicListView.setAdapter(mTopicAdapter);

        loadTopicBooksFromWebUseThread(mTopic.getBookList());
    }

    private void loadTopicBooksFromWebUseThread(final List<Long> bookIdList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BookWebService bookWebService = new BookWebService();
                for (int i = 0; i < bookIdList.size(); i++) {
                    BookDetail info = null;
                    try {
                        info = bookWebService.getBookDetail(bookIdList.get(i));
                    } catch (ZLNetworkException e) {
                    }
                    if (notNullBookDetail(info)) {
                        Message msg = handler.obtainMessage(LOAD_BOOK, BookItem.toBookItem(info));
                        handler.sendMessage(msg);
                    }
                }
                handler.sendEmptyMessage(FINISH_LOAD_BOOK);
            }
        }).start();
    }

    private boolean notNullBookDetail(BookDetail bookDetail) {
        return bookDetail != null && bookDetail.getBookCover() != null && bookDetail.getBookName() != null;
    }

}