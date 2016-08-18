package com.sanqiwan.reader.preload;

import android.os.AsyncTask;
import com.sanqiwan.reader.model.CommentInfo;
import com.sanqiwan.reader.webservice.BookWebService;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

import java.util.List;

public class LatestCommentLoadTask extends AsyncTask<Void, Void, List<CommentInfo>> {

    public static interface Callback {
        public void onCommentLoaded(List<CommentInfo> infos);
        public void onCommentLoadFailed(long bookId);
    }

    private long mBookId;
    private int mCommentsTop;
    private Callback mCallback;
    private BookWebService mBookWebService;

    public LatestCommentLoadTask(long bookId, int commentsTop, Callback callback) {
        mBookId = bookId;
        mCommentsTop = commentsTop;
        mCallback = callback;
        mBookWebService = new BookWebService();
    }

    @Override
    protected List<CommentInfo> doInBackground(Void... params) {
        try {
                return mBookWebService.getLatestComment(mBookId, mCommentsTop);
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