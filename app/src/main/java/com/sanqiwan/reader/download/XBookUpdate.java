package com.sanqiwan.reader.download;

import com.sanqiwan.reader.data.XBookManager;
import com.sanqiwan.reader.format.xbook.Constants;
import com.sanqiwan.reader.format.xbook.XBookWriter;
import com.sanqiwan.reader.model.BooksUpdateChapter;
import com.sanqiwan.reader.model.Chapter;
import com.sanqiwan.reader.model.TOC;
import com.sanqiwan.reader.model.VolumeItem;
import com.sanqiwan.reader.preference.Settings;
import com.sanqiwan.reader.webservice.BookWebService;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 8/10/13
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class XBookUpdate {
    private BookWebService mBookWebService;
    private XBookManager mXBookManager;

    public interface Callback {
        public void onBookUpdated(long bookId);
        public void onBookUpdateFailed(long bookId);
        public void onProgressUpdate(long bookId, int progress);
    }

    public XBookUpdate() {
        mBookWebService = new BookWebService();
        mXBookManager = new XBookManager();
    }

    public void update(final long bookId, final long chapterId, final Callback callback) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                callback.onProgressUpdate(bookId, 20);
                BookWebService bookWebService = new BookWebService();
                Chapter chapter = null;
                try {
                    chapter = bookWebService.getChapter(bookId, chapterId);
                } catch (ZLNetworkException e) {
                }
                callback.onProgressUpdate(bookId, 40);
                if (chapter == null) {
                    if (callback != null) {
                        callback.onBookUpdateFailed(bookId);
                    }
                    return;
                }

                File bookFile = Settings.getInstance().getBookDownloadFile(bookId);
                File contentDir = new File(bookFile, Constants.CONTENT);
                File chapterFile = new File(contentDir, String.valueOf(chapterId));
                XBookWriter xBookWriter = new XBookWriter();
                xBookWriter.writeChapter(chapter, chapterFile);
                callback.onProgressUpdate(bookId, 60);
                TOC toc = null;
                try {
                    toc = bookWebService.getTOC(bookId);
                } catch (ZLNetworkException e) {
                }
                if (toc == null) {
                    if (callback != null) {
                        callback.onBookUpdateFailed(bookId);
                    }
                    return;
                }
                callback.onProgressUpdate(bookId, 80);
                File tocFile = new File(bookFile, Constants.TOC);
                xBookWriter.writeTOC(toc, tocFile);
                if (callback != null) {
                    callback.onBookUpdated(bookId);
                    callback.onProgressUpdate(bookId, 100);
                }
            }
        };

        new Thread(runnable).start();
    }

    public void update(final long bookId, final List<VolumeItem> items, final Callback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateChapters(bookId, items, callback);
            }
        };

        new Thread(runnable).start();
    }

    private void updateChapters(final long bookId, final List<VolumeItem> items, final Callback callback) {
        if (items == null || items.isEmpty()) {
            return;
        }
        File bookFile = Settings.getInstance().getBookDownloadFile(bookId);
        XBookWriter xBookWriter = new XBookWriter();
        callback.onProgressUpdate(bookId, 10);
        final int size = items.size();
        for (int i = 0; i < size; i++) {
            long chapterId = items.get(i).getId();
            Chapter chapter = null;
            try {
                chapter = mBookWebService.getChapter(bookId, chapterId);
            } catch (ZLNetworkException e) {
            }
            if (chapter == null) {
                if (callback != null) {
                    callback.onBookUpdateFailed(bookId);
                }
                return;
            }
            File contentDir = new File(bookFile, Constants.CONTENT);
            File chapterFile = new File(contentDir, String.valueOf(chapterId));
            xBookWriter.writeChapter(chapter, chapterFile);
            if (callback != null) {
                callback.onProgressUpdate(bookId, 10 + 60 * (i + 1) / size);
            }
        }
        TOC toc = null;
        try {
            toc = mBookWebService.getTOC(bookId);
        } catch (ZLNetworkException e) {
        }
        callback.onProgressUpdate(bookId, 80);
        if (toc == null) {
            if (callback != null) {
                callback.onBookUpdateFailed(bookId);
            }
            return;
        }
        File tocFile = new File(bookFile, Constants.TOC);
        xBookWriter.writeTOC(toc, tocFile);
        if (callback != null) {
            callback.onBookUpdated(bookId);
            callback.onProgressUpdate(bookId, 100);
        }
    }

    public void update(final long bookId, final Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final long chapterId = mXBookManager.getLastChapterId(bookId);
                BooksUpdateChapter booksUpdateChapter = null;
                try {
                    booksUpdateChapter = mBookWebService.getBooksUpdateChapterId(bookId, chapterId);
                } catch (ZLNetworkException e) {
                }
                if (booksUpdateChapter == null) {
                    return;
                }
                updateChapters(bookId, booksUpdateChapter.getItems(), callback);
            }
        }).start();
    }
}
