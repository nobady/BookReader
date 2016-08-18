package com.sanqiwan.reader.preload;

import android.os.AsyncTask;
import com.sanqiwan.reader.model.Chapter;
import com.sanqiwan.reader.model.TOC;
import com.sanqiwan.reader.model.VolumeItem;
import com.sanqiwan.reader.model.XBook;
import com.sanqiwan.reader.util.AsyncTaskUtil;
import com.sanqiwan.reader.webservice.BookWebService;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/26/13
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChapterLoader {

    public interface Callback {
        public void onChapterLoaded(Chapter chapter);

        public void onChapterLoadFailed(long bookId, int index);
    }

    public interface PreloadCallback {
        public void onChapterPreloaded(int index, Chapter chapter);
    }

    private XBook mXBook;
    private TOC mTOC;
    private PreloadCallback mPreloadCallback;

    public ChapterLoader() {
    }

    public void setBook(XBook book) {
        mXBook = book;
        mTOC = book.getTOC();
    }

    public void setPreloadCallback(PreloadCallback callback) {
        mPreloadCallback = callback;
    }

    public void loadChapter(long chapterId, Callback callback) {
        AsyncTaskUtil.execute(new ChapterLoadTask(chapterId, false, callback));
    }

    private static class Progress {

        int mIndex;
        Chapter mChapter;
        boolean mIsPreloaded;

        private Progress(int index, Chapter chapter, boolean preloaded) {
            mIndex = index;
            mChapter = chapter;
            mIsPreloaded = preloaded;
        }
    }

    private class ChapterLoadTask extends AsyncTask<Void, Progress, Void> {

        private long mBookId;
        private Callback mCallback;
        private BookWebService mBookWebService;
        private int mIndex;
        private boolean mPreloadOnly;

        public ChapterLoadTask(long chapterId, boolean preloadOnly, Callback callback) {
            mIndex = mTOC.getItemIndexById(chapterId);
            mPreloadOnly = preloadOnly;
            mBookId = mTOC.getBookId();

            mCallback = callback;
            mBookWebService = new BookWebService();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //在这里做VIP章节的加载处理
            int index = mIndex;
            Chapter chapter;
            if (!mPreloadOnly) {
                chapter = getChapter(index);
                publishProgress(new Progress(index, chapter, false));
            }

            // load next chapter
            index = mIndex + 1;
            if (mTOC.getItem(index) != null && !mTOC.getItem(index).isVip()) {
                chapter = getChapter(index);
                publishProgress(new Progress(index, chapter, true));
            }

            // load previous chapter
            index = mIndex - 1;
            if (mTOC.getItem(index) != null && !mTOC.getItem(index).isVip()) {
                chapter = getChapter(index);
                publishProgress(new Progress(index, chapter, true));
            }
            return null;
        }

        private Chapter getChapter(int index) {

            VolumeItem item = mTOC.getItem(index);
            if (item == null) {
                return null;
            }

            long chapterId = item.getId();
            Chapter chapter = mXBook.getChapterById(chapterId);
            if (chapter == null) {
                try {
                    chapter = mBookWebService.getChapter(mBookId, chapterId);
                } catch (ZLNetworkException e) {
                }
            }
            return chapter;
        }

        @Override
        protected void onProgressUpdate(Progress... values) {

            Progress progress = values[0];

            if (progress.mIsPreloaded) {
                Chapter chapter = progress.mChapter;
                mXBook.addChapter(chapter);
                if (mPreloadCallback != null) {
                    mPreloadCallback.onChapterPreloaded(progress.mIndex, chapter);
                }
            } else {
                if (mCallback == null) {
                    return;
                }

                Chapter chapter = progress.mChapter;
                if (chapter == null) {
                    mCallback.onChapterLoadFailed(mBookId, progress.mIndex);
                } else {
                    mCallback.onChapterLoaded(chapter);
                }
            }
        }
    }
}
