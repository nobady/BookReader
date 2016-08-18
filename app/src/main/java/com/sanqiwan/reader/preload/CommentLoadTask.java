package com.sanqiwan.reader.preload;

import android.os.AsyncTask;
import com.sanqiwan.reader.model.CommentInfo;
import com.sanqiwan.reader.webservice.BookWebService;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

import java.util.List;

public class CommentLoadTask extends AsyncTask<Void, Void, List<CommentInfo>> {

    public static interface Callback {
        public void onCommentLoaded(List<CommentInfo> infos);
        public void onCommentLoadFailed(long bookId);
    }

    private long mBookId;
    private long mSinceId;
    private long mMaxId;
    private int mPage;
    private Callback mCallback;
    private BookWebService mBookWebService;

    public CommentLoadTask(long bookId, long sicne, long max, int page, Callback callback) {
        mBookId = bookId;
        mSinceId = sicne;
        mMaxId = max;
        mPage = page;
        mCallback = callback;
        mBookWebService = new BookWebService();
    }

    @Override
    protected List<CommentInfo> doInBackground(Void... params) {
        try {
            if (mSinceId > 0) {
                return mBookWebService.getCommentListBySinceId(mBookId, mSinceId, mPage);
            } else {
                return mBookWebService.getCommentListByMaxId(mBookId, mMaxId, mPage);
            }
        } catch (ZLNetworkException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<CommentInfo> commentInfos) {
        if (mCallback == null) {
            return;
        }
        if (commentInfos == null) {
            mCallback.onCommentLoadFailed(mBookId);
        } else {
            mCallback.onCommentLoaded(commentInfos);
        }
    }
}