package com.sanqiwan.reader.preload;

import android.os.AsyncTask;
import com.sanqiwan.reader.model.CommentInfo;
import com.sanqiwan.reader.payment.NullPostDataException;
import com.sanqiwan.reader.webservice.BookWebService;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-24
 * Time: 下午9:13
 * To change this template use File | Settings | File Templates.
 */
public class SendCommentTask extends AsyncTask<Void, Void, Boolean> {
    public static interface Callback {
        public void onSuccess();
        public void onFailed();
    }

    private CommentInfo mInfo;
    private Callback mCallback;
    private BookWebService mBookWebService;

    public SendCommentTask(CommentInfo info, Callback callback) {
        mInfo = info;
        mCallback = callback;
        mBookWebService = new BookWebService();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
           return mBookWebService.sendComment(mInfo);
        } catch (ZLNetworkException e) {
            e.printStackTrace();
        } catch (NullPostDataException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean isSuccess) {
        if (mCallback == null) {
            return;
        }
        if (isSuccess) {
            mCallback.onSuccess();
        } else {
            mCallback.onFailed();
        }
    }
}
