package com.sanqiwan.reader.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.adapter.CommentsAdapter;
import com.sanqiwan.reader.adapter.BucketListAdapter;
import com.sanqiwan.reader.engine.UserManager;
import com.sanqiwan.reader.model.CommentInfo;
import com.sanqiwan.reader.preload.CommentLoadTask;
import com.sanqiwan.reader.preload.SendCommentTask;
import com.sanqiwan.reader.pulltorefresh.library.PullToRefreshBase;
import com.sanqiwan.reader.pulltorefresh.library.PullToRefreshListView;
import com.sanqiwan.reader.util.AsyncTaskUtil;
import com.sanqiwan.reader.webimageview.WebImageView;


import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-23
 * Time: 下午10:06
 * To change this template use File | Settings | File Templates.
 */
public class CommentFragment extends BaseFragment implements View.OnClickListener, PullToRefreshListView.OnRefreshListener {

    private static final int PAGE = 1;
    private static final String BOOK_ID = "book_id";
    private View mFragmentView;
    private ViewGroup mTopView;
    private TextView mTopTitle;
    private EditText mContentView;
    private WebImageView mUserImage;
    private Context mContext;
    private long mBookId;
    private PullToRefreshListView mCommentList;
    private TextView mEmptyView;
    private CommentsAdapter mCommentAdapter;
    private CommentLoadTask mCommentTask;

    public static CommentFragment newFragment(long bookId) {
        CommentFragment fragment = new CommentFragment();
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
            mFragmentView = inflater.inflate(R.layout.comment, container, false);
            initTopView();

            mCommentList = (PullToRefreshListView) mFragmentView.findViewById(R.id.book_comments);
            mEmptyView = (TextView) mFragmentView.findViewById(R.id.empty_view);
            mCommentList.setEmptyView(mEmptyView);
            mCommentAdapter = new CommentsAdapter(mContext);
            mCommentList.setOnRefreshListener(this);
            mFragmentView.findViewById(R.id.comment_button).setOnClickListener(this);
            mCommentAdapter.setLoadMoreListener(mLoadMoreListener);
            mCommentList.setAdapter(mCommentAdapter);
            mFragmentView.findViewById(R.id.comment_panel).setVisibility(View.VISIBLE);
            mContentView = (EditText) mFragmentView.findViewById(R.id.comment_content);
            mContentView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    sendComment();
                    return true;
                }
            });
            mUserImage = (WebImageView) mFragmentView.findViewById(R.id.user_image);
            mUserImage.setDefaultImageResource(R.drawable.comment_user_image);
            if (UserManager.getInstance().isLogin()) {
                mUserImage.setImageUrl(UserManager.getInstance().getUserInfo().getAvatar());
            }
        }
        if (mFragmentView.getParent() != null) {
            ((ViewGroup) mFragmentView.getParent()).removeAllViews();
        }
        return mFragmentView;
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        refresh();
    }

    private void refresh() {
        refreshComment();
    }

    private BucketListAdapter.LoadMoreListener mLoadMoreListener = new BucketListAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            loadMoreComments();
        }
    };

    private void loadMoreComments() {
        if (mBookId == 0) {
            mCommentAdapter.disableLoadMore();
            mEmptyView.setText(getString(R.string.no_comment));
            return;
        }

        if (mCommentTask == null || mCommentTask.getStatus() == AsyncTask.Status.FINISHED) {
            mCommentTask = new CommentLoadTask(mBookId, 0, getMaxId(), PAGE, new CommentLoadTask.Callback() {
                @Override
                public void onCommentLoaded(List<CommentInfo> infos) {
                    if (getActivity() == null) {
                        return;
                    }
                    if (infos != null && infos.size() > 0) {
                        mCommentAdapter.addAll(infos);
                    } else {
                        mCommentAdapter.disableLoadMore();
                    }
                }

                @Override
                public void onCommentLoadFailed(long bookId) {
                    if (getActivity() == null) {
                        return;
                    }
                    mCommentAdapter.disableLoadMore();
                }
            });
            AsyncTaskUtil.execute(mCommentTask);
        }
    }

    private void refreshComment() {
        if (mBookId == 0) {
            mCommentAdapter.disableLoadMore();
            mEmptyView.setText(getString(R.string.no_comment));
            return;
        }

        if (mCommentTask == null || mCommentTask.getStatus() == AsyncTask.Status.FINISHED) {   //执行下拉刷新
            mCommentTask = new CommentLoadTask(mBookId, getSince(), 0, PAGE, new CommentLoadTask.Callback() {
                @Override
                public void onCommentLoaded(List<CommentInfo> infos) {
                    if (getActivity() == null) {
                        return;
                    }
                    if (infos != null && infos.size() > 0) {
                        mCommentAdapter.setNotifyOnChange(false);
                        for (int i = 0; i < infos.size(); i++) {
                            if (i == infos.size() - 1) {
                                mCommentAdapter.setNotifyOnChange(true);
                            }
                            mCommentAdapter.insert(infos.get(i), i);
                        }
                    }
                    mCommentList.onRefreshComplete();
                }

                @Override
                public void onCommentLoadFailed(long bookId) {
                    if (getActivity() == null) {
                        return;
                    }
                    mCommentList.onRefreshComplete();
                }
            });
            AsyncTaskUtil.execute(mCommentTask);
        }
    }

    private void initTopView() {
        mTopView = (ViewGroup) mFragmentView.findViewById(R.id.comment_top);
        mTopTitle = (TextView) mTopView.findViewById(R.id.top_title);
        mTopTitle.setText(R.string.comment_txt);
        mTopView.findViewById(R.id.btn_return).setOnClickListener(this);
    }

    private String mContent;
    private SendCommentTask mSendTask;

    private void sendComment() {
        mContent = null;
        mContent = mContentView.getText().toString().trim();
        if (TextUtils.isEmpty(mContent)) {
            return;
        }
        if (!UserManager.getInstance().isLogin()) {
            showLoginTipDialog();
            return;
        }
        hideSoftKeyboard();
        mContentView.setText(getString(R.string.sending_tip));

        if (mSendTask == null || mSendTask.getStatus() == AsyncTask.Status.FINISHED) {
            executeAsyncTask();
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.prohibit_submit_tips), Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoginTipDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.login_msg);
        builder.setTitle(R.string.not_login);
        builder.setPositiveButton(R.string.login_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.openMainActivity(getActivity(), MainActivity.MINE_FRAGMENT_ID);
            }
        });
        builder.setNegativeButton(R.string.cancel_button, null);
        builder.create().show();
    }

    private void executeAsyncTask() {
        mSendTask = new SendCommentTask(CommentInfo.getCommentInfo(mBookId, mContent), new SendCommentTask.Callback() {
            @Override
            public void onSuccess() {
                if (getActivity() == null) {
                    return;
                }
                mContentView.setText("");
                refresh();
            }

            @Override
            public void onFailed() {
                if (getActivity() == null) {
                    return;
                }
                mContentView.setText(getString(R.string.comment_hint));
                Toast.makeText(mContext, getResources().getString(R.string.comment_failed), Toast.LENGTH_SHORT).show();
            }
        });
        AsyncTaskUtil.execute(mSendTask);
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mContentView.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_return:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.comment_button:
                sendComment();
        }
    }

    private long getSince() {
        long since = 0;
        if (mCommentAdapter.getElementCount() > 0) {
            since = mCommentAdapter.getElement(0).getId();
        }
        return since;
    }

    private long getMaxId() {
        long maxId = 0;
        if (mCommentAdapter.getElementCount() > 0) {
            maxId = mCommentAdapter.getElement(mCommentAdapter.getElementCount() - 1).getId();
        }
        return maxId;
    }
}