package com.sanqiwan.reader.preload;

import android.os.AsyncTask;
import com.sanqiwan.reader.engine.IChapterLoader;
import com.sanqiwan.reader.engine.SubscriptionManager;
import com.sanqiwan.reader.engine.UserManager;
import com.sanqiwan.reader.model.Chapter;
import com.sanqiwan.reader.model.VolumeItem;
import com.sanqiwan.reader.model.XBook;
import com.sanqiwan.reader.util.AsyncTaskUtil;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

/**
 * Created with IntelliJ IDEA.
 * User: jwb
 * Date: 13-11-20
 * Time: 上午11:09
 * To change this template use File | Settings | File Templates.
 */
public class Subscriber implements IChapterLoader {

    public static interface Callback {
        public void onSubscribeFailed(long bookId, int index, boolean showTips);

        public void onChapterLoaded(Chapter chapter);
        public void onChapterLoadFailed(long bookId, int index);

        public void onShowVipTips(long bookId, int index);
    }

    private XBook mBook;
    private ChapterLoader mChapterLoader;
    private Callback mCallback;

    public Subscriber(Callback callback) {
        mCallback = callback;
        mChapterLoader = new ChapterLoader();
    }

    public void setBook(XBook book) {
        mBook = book;
        mChapterLoader.setBook(book);
    }

    @Override
    public void loadChapter(long bookId, long chapterId, ChapterLoader.Callback callback) {
        loadChapter(bookId, chapterId);
    }

    private ChapterLoader.Callback mChapterLoaderCallback = new ChapterLoader.Callback() {
        @Override
        public void onChapterLoaded(Chapter chapter) {
            mCallback.onChapterLoaded(chapter);
        }

        @Override
        public void onChapterLoadFailed(long bookId, int index) {
            mCallback.onChapterLoadFailed(bookId, index);
        }
    };

    private void loadChapter(long bookId, long chapterId) {
        if (!isVipChapter(chapterId)) {
            mChapterLoader.loadChapter(chapterId, mChapterLoaderCallback);
        } else {
            if (!UserManager.getInstance().isLogin()) {
                mCallback.onShowVipTips(bookId, mBook.getIndexById(chapterId));
            } else {
                if (mBook != null) {
                    VolumeItem volumeItem = mBook.getTOC().getItemById(chapterId);
                    checkSubscribe(bookId, volumeItem.getId(), mCallback);
                }
            }
        }
    }

    private boolean isVipChapter(long chapterId) {
        return mBook.getTOC().getItemById(chapterId).isVip();
    }

    private void checkSubscribe(long bookId, long chapterId, Callback callback) {
        AsyncTaskUtil.execute(new CheckSubscribeAsynTask(bookId, chapterId, callback));
    }

    private class CheckSubscribeAsynTask extends AsyncTask<Void, Void, Boolean> {
        private long mBookId;
        private long mChapterId;
        private Callback mCallback;
        public CheckSubscribeAsynTask(long bookId, long chapterId, Callback callback) {
            mBookId = bookId;
            mChapterId = chapterId;
            mCallback = callback;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return isSubscription(mBookId, mChapterId);
            } catch (ZLNetworkException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean isSubscription) {
            if (isSubscription == null) {
                mCallback.onSubscribeFailed(mBookId, mBook.getIndexById(mChapterId), false);
            } else {
                if (isSubscription) {
                    mChapterLoader.loadChapter(mChapterId, mChapterLoaderCallback);
                } else {
                    mCallback.onSubscribeFailed(mBookId, mBook.getIndexById(mChapterId), true);
                }
            }
        }
    }

    private boolean isSubscription(long bookId, long chapterId) throws ZLNetworkException {
        if (UserManager.getInstance().getUserInfo() != null) {
            return SubscriptionManager.getsInstance().isSubscriptionOfVip(bookId, chapterId, UserManager.getInstance().getUserInfo().getUid());
        }
        return false;
    }

}
