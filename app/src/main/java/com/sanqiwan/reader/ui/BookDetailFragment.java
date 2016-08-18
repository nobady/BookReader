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
import android.widget.*;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.adapter.BookVolumeAdapter;
import com.sanqiwan.reader.adapter.BucketListAdapter;
import com.sanqiwan.reader.adapter.CommentsAdapter;
import com.sanqiwan.reader.data.XBookManager;
import com.sanqiwan.reader.download.DownloadManager;
import com.sanqiwan.reader.engine.UserManager;
import com.sanqiwan.reader.model.BookDetail;
import com.sanqiwan.reader.model.CommentInfo;
import com.sanqiwan.reader.model.TOC;
import com.sanqiwan.reader.model.VolumeItem;
import com.sanqiwan.reader.preload.*;
import com.sanqiwan.reader.util.AsyncTaskUtil;
import com.sanqiwan.reader.util.CoverUtil;
import com.sanqiwan.reader.util.StarSumUtil;
import com.sanqiwan.reader.view.MyRatingImageStar;
import com.sanqiwan.reader.webimageview.WebImageView;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-8-1
 * Time: 下午8:47
 * To change this template use File | Settings | File Templates.
 */
public class BookDetailFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final int PAGE_SIZE = 5;
    private static final int COMMENTS_TOP = 1;
    private static final int DOWNLOAD_SUCCESSFUL = 100;
    private static final int DEFAULT_INT_VALUE = 0;
    private static final String BOOK_ID = "book_id";
    private static final String BOOK_NAME = "book_name";
    private static final String FINISHED = "finished";

    private TextView mIntroductionDetail;
    private ListView mBookLatestVolumes;
    private ListView mBookLatestComments;
    private View mFragmentView;

    private WebImageView mDetailImageView;
    private TextView mContinue;
    private TextView mAddBook;
    private TextView mDownloadPart;
    private TextView mBookNameDetail;
    private TextView mIsAuthorization;
    private TextView mBookSizeDetail;
    private TextView mAuthorNameDetail;
    private TextView mBookStatus;
    private TextView mIntroductionFold;
    private TextView mBookTags;

    private BookVolumeAdapter mBookVolumeAdapter;
    private CommentsAdapter mCommentAdapter;
    private ViewGroup mTopView;
    private TextView mDetailTitle;
    private EditText mContentView;
    private WebImageView mUserImage;
    private View mLatestVolumePanel;

    private BookDetailLoad mBookDetailLoad;
    private XBookManager mXBookManager;
    private BookDetail mBookDetail;
    private LatestTOCLoadTask mLatestTOCLoadTask;
    private LatestCommentLoadTask mLatestCommentTask;

    private DownloadManager mBookDownload;
    private String mBookName;
    private boolean mFinished;
    private long mBookId;
    private Context mContext;

    public static BookDetailFragment newFragment(long bookId, String bookName, boolean finished) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle arg = new Bundle();
        arg.putLong(BOOK_ID, bookId);
        arg.putString(BOOK_NAME, bookName);
        arg.putBoolean(FINISHED, finished);
        fragment.setArguments(arg);
        return fragment;
    }

    private void takeArguments() {
        Bundle args = getArguments();
        if (args != null) {
            mBookId = args.getLong(BOOK_ID);
            mBookName = args.getString(BOOK_NAME);
            mFinished = args.getBoolean(FINISHED);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBookDownload = DownloadManager.getInstance();
        mBookDownload.addCallback(mCallback);
        mBookDetailLoad = new BookDetailLoad();
        mXBookManager = new XBookManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        takeArguments();
        mContext = getContext();
        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.book_detail, container, false);

            initTopView();
            mContinue = (TextView) mFragmentView.findViewById(R.id.continue_detail);
            mAddBook = (TextView) mFragmentView.findViewById(R.id.add_detail);
            mBookDetailLoad.loadBookDetail(mBookId, new BookDetailTask.BookDetailCallback() {
                @Override
                public void onBookDetailLoaded(BookDetail bookDetail) {
                    if (getActivity() == null) {
                        return;
                    }
                    mBookDetail = bookDetail;
                    bindBookDetail();
                }

                @Override
                public void onBookDetailLoadFailed(long bookId) {
                    if (getActivity() == null) {
                        return;
                    }
                    mIntroductionDetail.setText(getText(R.string.no_wifi).toString());
                    String authorName = String.format(getResources().getString(R.string.author_format), "");
                    mAuthorNameDetail.setText(authorName);
                    String bookSize = String.format(getResources().getString(R.string.book_size), 0);
                    mBookSizeDetail.setText(bookSize);
                }
            });

            mBookNameDetail = (TextView) mFragmentView.findViewById(R.id.bookName_detail);
            mBookNameDetail.setText(mBookName == null ? "" : mBookName);
            mIsAuthorization = (TextView) mFragmentView.findViewById(R.id.authorization);
            mAuthorNameDetail = (TextView) mFragmentView.findViewById(R.id.writer_detail);
            mBookSizeDetail = (TextView) mFragmentView.findViewById(R.id.book_size);
            mFragmentView.findViewById(R.id.image_detail).setOnClickListener(this);
            mDownloadPart = (TextView) mFragmentView.findViewById(R.id.download_part_detail);
            mDetailImageView = (WebImageView) mFragmentView.findViewById(R.id.image_detail);
            mBookStatus = (TextView) mFragmentView.findViewById(R.id.status_detail);
            setBookState(mFinished);
            mBookTags = (TextView) mFragmentView.findViewById(R.id.tags);
            mIntroductionDetail = (TextView) mFragmentView.findViewById(R.id.introduction_content_detail);
            mIntroductionDetail.setOnClickListener(this);
            mIntroductionFold = (TextView) mFragmentView.findViewById(R.id.introduction_fold);
            mIntroductionFold.setOnClickListener(this);
            mBookLatestVolumes = (ListView) mFragmentView.findViewById(R.id.book_latest_volumes);
            mBookVolumeAdapter = new BookVolumeAdapter(mContext);
            mBookLatestComments = (ListView) mFragmentView.findViewById(R.id.latest_comments);
            mCommentAdapter = new CommentsAdapter(mContext);
            mLatestVolumePanel = mFragmentView.findViewById(R.id.latest_volume);
            mFragmentView.findViewById(R.id.comment_button).setOnClickListener(this);
            if (mXBookManager.hasBook(mBookId)) {
                initAddBookButton(DOWNLOAD_SUCCESSFUL);
            }

            int initProgress = mBookDownload.queryProgress(mBookId);
            if (initProgress > 0) {
                initAddBookButton(initProgress);
            }

            mContinue.setOnClickListener(this);
            mAddBook.setOnClickListener(this);
            mDetailImageView.setOnClickListener(this);
            mFragmentView.findViewById(R.id.all_volumes).setOnClickListener(this);
            mFragmentView.findViewById(R.id.all_comments).setOnClickListener(this);

            mBookLatestVolumes.setOnItemClickListener(this);
            //加载目录
            //mBookVolumeAdapter.setLoadMoreListener(mLoadMoreVolumesListener);
            //mBookLatestVolumes.setAdapter(mBookVolumeAdapter);
            //加载评论
            mCommentAdapter.setLoadMoreListener(mLoadMoreCommentsListener);
            mBookLatestComments.setAdapter(mCommentAdapter);

            mFragmentView.findViewById(R.id.comment_panel).setVisibility(View.VISIBLE);
            mFragmentView.findViewById(R.id.line_5).setVisibility(View.VISIBLE);
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

    private void initTopView() {
        mTopView = (ViewGroup) mFragmentView.findViewById(R.id.detail_top);
        mDetailTitle = (TextView) mTopView.findViewById(R.id.top_title);
        mDetailTitle.setText(R.string.book_detail);
        mTopView.findViewById(R.id.btn_return).setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mBookVolumeAdapter.getTOC() == null) {
            return;
        }
        VolumeItem item = mBookVolumeAdapter.getTOC().getItem(position);
        if (item != null) {
            ReaderActivity.openBook(mContext,
                    mBookId, item.getId());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_return:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.continue_detail:
                ReaderActivity.openBook(mContext, mBookId, DEFAULT_INT_VALUE);
                break;
            case R.id.add_detail:
                initAddBookButton(DEFAULT_INT_VALUE);
                mBookDownload.startDownload(mBookId, mBookName);
                break;
            case R.id.image_detail:
                ReaderActivity.openBook(mContext, mBookId, DEFAULT_INT_VALUE);
                break;
            case R.id.introduction_content_detail:
            case R.id.introduction_fold:
                setIntroductionShow();
                break;
            case R.id.all_volumes:
                MainActivity.openSubFragment(BookVolumeFragment.newFragment(mBookId));
                break;
            case R.id.all_comments:
                MainActivity.openSubFragment(CommentFragment.newFragment(mBookId));
                break;
            case R.id.comment_button:
                sendComment();
                break;
        }

    }

    private String mCommentContent;
    private SendCommentTask mSendTask;

    private void sendComment() {
        mCommentContent = null;
        mCommentContent = mContentView.getText().toString().trim();
        if (TextUtils.isEmpty(mCommentContent)) {
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
                MainActivity.gotoLogin(getActivity(), MineFragment.ACTION_FOR_LOGIN);
            }
        });
        builder.setNegativeButton(R.string.cancel_button, null);
        builder.create().show();
    }
    private void executeAsyncTask() {
        mSendTask = new SendCommentTask(CommentInfo.getCommentInfo(mBookId, mCommentContent), new SendCommentTask.Callback() {
            @Override
            public void onSuccess() {
                if (getActivity() == null) {
                    return;
                }
                mContentView.setText("");
                CommentInfo info = CommentInfo.getCommentInfo(mBookId, mCommentContent);
                info.setUserImageUrl(UserManager.getInstance().getUserInfo().getUserName());
                info.setUserImageUrl(UserManager.getInstance().getUserInfo().getAvatar());
                mCommentAdapter.insert(info, 0);
                Toast.makeText(mContext, getResources().getString(R.string.comment_success), Toast.LENGTH_SHORT).show();
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

    private boolean mIsShow = true;

    private void setIntroductionShow() {
        if (mIsShow) {
            mIsShow = false;
            mIntroductionDetail.setEllipsize(null); // 展开
            mIntroductionDetail.setSingleLine(mIsShow);
            mIntroductionFold.setText(getString(R.string.unfold_txt));//右下角文字
            mIntroductionFold.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.introduction_fold), null);
        } else {
            mIsShow = true;
            mIntroductionDetail.setEllipsize(TextUtils.TruncateAt.END); // 收缩
            mIntroductionDetail.setMaxLines(4);
            mIntroductionFold.setText(getString(R.string.fold_txt));  //右下角文字
            mIntroductionFold.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.introduction_unfold), null);

        }
    }

    private void initAddBookButton(int initProgress) {
        mAddBook.setEnabled(false);
        mAddBook.setText(getText(R.string.already_into_books));
        if (initProgress < DOWNLOAD_SUCCESSFUL) {
            mDownloadPart.setVisibility(View.VISIBLE);
        } else {
            mContinue.setText(getText(R.string.reading));
        }
    }

    private BucketListAdapter.LoadMoreListener mLoadMoreVolumesListener = new BucketListAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            //loadVolumes();
        }
    };

    private BucketListAdapter.LoadMoreListener mLoadMoreCommentsListener = new BucketListAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            loadComments();
        }
    };

    private void loadVolumes() {
        if (mLatestTOCLoadTask == null || mLatestTOCLoadTask.getStatus() == AsyncTask.Status.FINISHED) {
            mLatestTOCLoadTask = new LatestTOCLoadTask(mBookId, PAGE_SIZE, new LatestTOCLoadTask.Callback() {
                @Override
                public void onTOCLoaded(TOC toc) {
                    if (getActivity() == null) {
                        return;
                    }
                    if (toc != null && toc.getSize() > 0) {
                        mBookVolumeAdapter.addTOC(toc);
                    } else {
                        mLatestVolumePanel.setVisibility(View.GONE);
                    }
                    mBookVolumeAdapter.disableLoadMore();
                }

                @Override
                public void onTOCLoadFailed(long bookId) {
                    if (getActivity() == null) {
                        return;
                    }
                    mLatestVolumePanel.setVisibility(View.GONE);
                    mBookVolumeAdapter.disableLoadMore();
                }
            });
            AsyncTaskUtil.execute(mLatestTOCLoadTask);
        }
    }

    private void loadComments() {
        if (mLatestCommentTask == null || mLatestCommentTask.getStatus() == AsyncTask.Status.FINISHED) {
            mLatestCommentTask = new LatestCommentLoadTask(mBookId, COMMENTS_TOP, new LatestCommentLoadTask.Callback() {
                @Override
                public void onCommentLoaded(List<CommentInfo> infos) {
                    if (getActivity() == null) {
                        return;
                    }
                    if (infos != null && infos.size() > 0) {
                        mCommentAdapter.addAll(infos);
                    }
                    mCommentAdapter.disableLoadMore();
                }

                @Override
                public void onCommentLoadFailed(long bookId) {
                    if (getActivity() == null) {
                        return;
                    }
                    mCommentAdapter.disableLoadMore();
                }
            });
            AsyncTaskUtil.execute(mLatestCommentTask);
        }
    }

    private void downloadError(long bookId) {
        if (mBookId == bookId) {
            mAddBook.setEnabled(true);
            mAddBook.setText(getText(R.string.add_book_stacks).toString());
            mContinue.setText(getText(R.string.online_read).toString());
            mDownloadPart.setText(getText(R.string.percent).toString());
            mDownloadPart.setVisibility(View.GONE);
            Toast.makeText(mContext, getString(R.string.no_wifi), Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadFinished(long bookId) {
        if (bookId == mBookId) {
            mDownloadPart.setVisibility(View.GONE);
            mContinue.setText(getText(R.string.reading).toString());
        }
    }

    private void updateProgress(long bookId, int update) {
        if (mDownloadPart.getVisibility() == View.GONE && mBookId == bookId) {
            initAddBookButton(DEFAULT_INT_VALUE);
        }
        if (bookId == mBookId) {
            mDownloadPart.setText(update + getText(R.string.percent).toString());
        }
    }

    private void bindBookDetail() {
        if (mBookDetail == null || getActivity() == null) {
            return;
        }
        mBookName = mBookDetail.getBookName();
        mBookNameDetail.setText(mBookName);
        String isAuthorization;
        if (mBookDetail.getAuthorization()) {
            isAuthorization = getResources().getString(R.string.authorization);
        } else {
            isAuthorization = getResources().getString(R.string.non_authorization);
        }
        mIsAuthorization.setText(isAuthorization);
        setBookState(mBookDetail.getFinish());
        String authorName = String.format(getResources().getString(R.string.author_format), mBookDetail.getAuthorName());
        mAuthorNameDetail.setText(authorName);
        String bookSize = String.format(getResources().getString(R.string.book_size), mBookDetail.getBookSize());
        mBookSizeDetail.setText(bookSize);
        mBookTags.setText(formatTags(mBookDetail.getTags()));
        mDetailImageView.setDefaultImageResource(R.drawable.no_book_pic);
        if (!CoverUtil.hasNoCover(mBookDetail.getBookCover())) {
            mDetailImageView.setImageUrl(mBookDetail.getBookCover());
        }
        mIntroductionDetail.setText(mBookDetail.getDescription());
    }

    private void setBookState(boolean isFinish) {
        String state = isFinish ? getResources().getString(R.string.finished) : getResources().getString(R.string.writing);
        state = String.format(getResources().getString(R.string.state), state);
        mBookStatus.setText(state);
    }

    /**
     * 格式化标签，并且仅取一定数量（两个字的三个，有两个三个字的两个，有四个字的最多两个，大于四字的直接抛弃）返回显示
     * @param tag
     * @return
     */
    private String formatTags(String tag) {
        if (tag == null || tag.equals("")) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(',');
        float i = 0;
        splitter.setString(tag);
        for (String s : splitter) {
            if (s.length() > 4) {   //多于四个字的直接不显示
                continue;
            } else if (s.length() > 2) { //多于两个字，两个字占一个词的位置
                i += 0.5 * (s.length() - 2);
            }
            builder.append(s);
            builder.append("、");
            if (i >= 2) {   //只显示三个词位
                break;
            }
            i++;
        }
        builder.deleteCharAt(builder.lastIndexOf("、"));
        return String.format(getResources().getString(R.string.tags), builder.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBookDownload.removeCallback(mCallback);
    }

    private DownloadManager.Callback mCallback = new DownloadManager.Callback() {

        @Override
        public void onUpdateProgress(long bookId, int progress) {
            updateProgress(bookId, progress);
        }

        @Override
        public void onDownloadFinished(long bookId) {
            downloadFinished(bookId);
        }

        @Override
        public void onDownloadError(long bookId) {
            downloadError(bookId);
        }
    };
}
