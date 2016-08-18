package com.sanqiwan.reader.preload;

import android.os.AsyncTask;
import com.sanqiwan.reader.model.TOC;
import com.sanqiwan.reader.webservice.BookWebService;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

public class LatestTOCLoadTask extends AsyncTask<Void, Void, TOC> {

    public static interface Callback {
        public void onTOCLoaded(TOC toc);

        public void onTOCLoadFailed(long bookId);
    }

    private long mBookId;
    private Callback mCallback;
    private int mPageSize;
    private BookWebService mBookWebService;

    public LatestTOCLoadTask(long bookId, int pageSize, Callback callback) {
        mBookId = bookId;
        mPageSize = pageSize;
        mCallback = callback;
        mBookWebService = new BookWebService();
    }

    @Override
    protected TOC doInBackground(Void... params) {
        try {
            return mBookWebService.getLatestChapter(mBookId, mPageSize);
        } catch (ZLNetworkException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(TOC toc) {
        if (mCallback == null) {
            return;
        }
        if (toc == null) {
            mCallback.onTOCLoadFailed(mBookId);
        } else {
            mCallback.onTOCLoaded(toc);
        }
    }
}