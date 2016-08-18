package com.sanqiwan.reader.preload;

import android.os.AsyncTask;
import com.sanqiwan.reader.cache.ChapterCache;
import com.sanqiwan.reader.model.Chapter;
import com.sanqiwan.reader.webservice.BookWebService;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

public class ChapterLoadTask extends AsyncTask<Void, Void, Chapter> {

    public static interface Callback {
        public void onChapterLoaded(Chapter chapter);

        public void onChapterLoadFailed(long bookId, long chapterId);
    }

    private long mBookId;
    private long mChapterId;
    private Callback mCallback;
    private BookWebService mBookWebService;

    public ChapterLoadTask(long bookId, long chapterId, Callback callback) {
        mBookId = bookId;
        mChapterId = chapterId;
        mCallback = callback;
        mBookWebService = new BookWebService();
    }

    @Override
    protected Chapter doInBackground(Void... params) {

        Chapter chapter = ChapterCache.getInstance().getChapter(mBookId, mChapterId);
        if (chapter == null) {
            try {
                chapter = mBookWebService.getChapter(mBookId, mChapterId);
            } catch (ZLNetworkException e) {
            }
            ChapterCache.getInstance().addChapter(chapter);
        }
        return chapter;
    }

    @Override
    protected void onPostExecute(Chapter chapter) {
        if (mCallback == null) {
            return;
        }
        if (chapter == null) {
            mCallback.onChapterLoadFailed(mBookId, mChapterId);
        } else {
            mCallback.onChapterLoaded(chapter);
        }
    }
}