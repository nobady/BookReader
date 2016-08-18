package com.sanqiwan.reader.preload;

import android.os.AsyncTask;
import com.sanqiwan.reader.AppContext;
import com.sanqiwan.reader.data.OnlineBooks;
import com.sanqiwan.reader.model.BookItem;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-8-6
 * Time: 下午3:12
 * To change this template use File | Settings | File Templates.
 */
public abstract class OnlineBookLoadTask extends AsyncTask<Void, Void, List<BookItem>> {
    public interface OnlineBookCallback {
        public void onOnlineBookLoaded(List<BookItem> onlineBookItemList);
        public void onOnlineBookLoadFailed();
    }

    private OnlineBookCallback mCallback;
    private OnlineBooks mOnlineBooks;

    public OnlineBookLoadTask(OnlineBookCallback callback) {
        mCallback = callback;
        mOnlineBooks = new OnlineBooks(AppContext.getInstance());
    }

    @Override
    protected void onPostExecute(List<BookItem> onlineBookItemList) {
        if (mCallback == null) {
            return;
        }
        if (onlineBookItemList == null) {
            mCallback.onOnlineBookLoadFailed();
        } else {
            mCallback.onOnlineBookLoaded(onlineBookItemList);
        }
    }

    protected void saveBooks(String category, String type, List<BookItem> items) {
        List<BookItem> onlineBookItemList = items;

        Set<Long> bookIDSet = mOnlineBooks.queryNewBooks(category);
        BookItem temp = null;
        if (onlineBookItemList != null && onlineBookItemList.size() > 0) {
            for (BookItem item : onlineBookItemList) {
                //the bookId exist in table
                if (bookIDSet.size() > 0 && bookIDSet.contains(item.getBookId())) {

                    if (type == null) {
                        temp.setUpdateDate(item.getUpdateDate());
                        mOnlineBooks.updateNewUpdateBooks(item);
                    } else {
                        temp.setCommends(item.getCommends());
                        mOnlineBooks.updateNewTopBooks(item);
                    }
                } else {
                    mOnlineBooks.insert(item);
                }
            }
        }
    }

}
