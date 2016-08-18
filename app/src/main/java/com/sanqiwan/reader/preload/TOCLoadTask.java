package com.sanqiwan.reader.preload;

import android.os.AsyncTask;
import com.sanqiwan.reader.cache.TOCCache;
import com.sanqiwan.reader.model.TOC;
import com.sanqiwan.reader.webservice.BookWebService;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

public class TOCLoadTask extends AsyncTask<Void, Void, TOC> {

    public static interface Callback {
        public void onTOCLoaded(TOC toc);
        public void onTOCLoadFailed(long bookId);
    }

    private long mBookId;
    private int mPageIndex;
    private Callback mCallback;
    private BookWebService mBookWebService;

    public TOCLoadTask(long bookId, int pageIndex, Callback callback) {
        mBookId = bookId;
        mPageIndex = pageIndex;
        mCallback = callback;
        mBookWebService = new BookWebService();
    }

    @Override
    protected TOC doInBackground(Void... params) {
        TOC toc = TOCCache.getInstance().getTOC(mBookId, mPageIndex);
        if (toc == null) {
            try {
                toc = mBookWebService.getTOC(mBookId, mPageIndex);
            } catch (ZLNetworkException e) {
            }
            TOCCache.getInstance().addTOC(toc, mPageIndex);
        }
        return toc;
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