package com.sanqiwan.reader.preload;

import com.sanqiwan.reader.model.XBook;
import com.sanqiwan.reader.util.UIUtil;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 12/2/13
 * Time: 5:31 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BookLoader {
    public static interface Callback {
        public void onBookLoaded(XBook book);
        public void onBookLoadFailed(long bookId);
    }

    private long mBookId;
    private Callback mCallback;

    public BookLoader(long bookId, Callback callback) {
        mBookId = bookId;
        mCallback = callback;
    }

    protected long getBookId() {
        return mBookId;
    }

    public void executeLoad() {
        new Thread() {
            @Override
            public void run() {
                final XBook book = doInBackground();
                UIUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onPostExecute(book);
                    }
                });
            }
        }.start();
    }

    protected abstract XBook doInBackground();

    protected void onPostExecute(XBook book) {

        if (mCallback != null) {
            if (book == null) {
                mCallback.onBookLoadFailed(mBookId);
            } else {
                mCallback.onBookLoaded(book);
            }
        }
    }
}
