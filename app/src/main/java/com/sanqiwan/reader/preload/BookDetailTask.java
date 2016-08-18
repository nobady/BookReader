package com.sanqiwan.reader.preload;

import android.os.AsyncTask;
import com.sanqiwan.reader.model.BookDetail;
import com.sanqiwan.reader.webservice.BookWebService;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-8-1
 * Time: 下午2:46
 * To change this template use File | Settings | File Templates.
 */
public class BookDetailTask extends AsyncTask<Void, Void, BookDetail> {
    public interface BookDetailCallback {
        public void onBookDetailLoaded(BookDetail bookDetail);
        public void onBookDetailLoadFailed(long bookId);
    }

    private long mBookId;
    private BookDetailCallback mCallback;
    private BookWebService mBookWebService;

    public BookDetailTask(long bookId, BookDetailCallback callback) {
        mBookId = bookId;
        mCallback = callback;
        mBookWebService = new BookWebService();
    }

    @Override
    protected BookDetail doInBackground(Void... params) {
        try {
            return mBookWebService.getBookDetail(mBookId);
        } catch (ZLNetworkException e) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(BookDetail bookDetail) {
        if (mCallback == null) {
            return;
        }
        if (bookDetail == null) {
            mCallback.onBookDetailLoadFailed(mBookId);
        } else {
            mCallback.onBookDetailLoaded(bookDetail);
        }
    }
}
