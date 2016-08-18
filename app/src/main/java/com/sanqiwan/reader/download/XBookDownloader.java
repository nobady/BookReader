package com.sanqiwan.reader.download;

import android.graphics.Bitmap;
import com.sanqiwan.reader.cache.ChapterCache;
import com.sanqiwan.reader.cache.TOCCache;
import com.sanqiwan.reader.data.XBookManager;
import com.sanqiwan.reader.model.*;
import com.sanqiwan.reader.util.ImageLoadUtil;
import com.sanqiwan.reader.webservice.BookWebService;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 8/9/13
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class XBookDownloader {

    public interface Callback {
        public void onDownloadStarted(long bookId);
        public void onProgressUpdate(long bookId, int progress);
        public void onDownloadFinished(long bookId);
        public void onDownloadError(long bookId);
    }

    private Callback mCallback;
    private static boolean sDownloadVipChapter = false;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void startDownload(long bookId) {
        // TODO: limit the count of thread
        new DownloadThread(bookId).start();

    }

    private class DownloadThread extends Thread {
        private long mBookId;

        private DownloadThread(long bookId) {
            super("book-downloader-" + bookId);
            mBookId = bookId;
        }

        @Override
        public void run() {
            if (mCallback != null) {
                mCallback.onDownloadStarted(mBookId);
            }
            if (mCallback != null) {
                mCallback.onProgressUpdate(mBookId, 10);
            }
            XBook book = new XBook();
            BookWebService bookWebService = new BookWebService();
            // book detail
            BookDetail bookDetail = null;
            try {
                bookDetail = bookWebService.getBookDetail(mBookId);
            } catch (ZLNetworkException e) {
            }
            if (bookDetail == null) {
                if (mCallback != null) {
                    mCallback.onDownloadError(mBookId);
                }
                return;
            }
            book.setBookDetail(bookDetail);
            book.setBookId(bookDetail.getBookId());
            if (mCallback != null) {
                mCallback.onProgressUpdate(mBookId, 15);
            }
            // Cover
            final BookResource bookResource = new BookResource();
            final Object lock = new Object();
            ImageLoadUtil.Callback callback = new ImageLoadUtil.Callback() {
                @Override
                public void onImageLoaded(String url, Bitmap bitmap) {
                    bookResource.setCover(bitmap);
                    synchronized (lock) {
                        lock.notify();
                    }
                }

                @Override
                public void onImageLoadFailed(String url) {
                    synchronized (lock) {
                        lock.notify();
                    }
                }
            };
            ImageLoadUtil.loadImage(bookDetail.getBookCover(), callback);
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            book.setBookResource(bookResource);
            if (mCallback != null) {
                mCallback.onProgressUpdate(mBookId, 20);
            }
            // TOC
            // TODO: 处理目录分页的情形
            TOC toc = TOCCache.getInstance().getTOC(mBookId, -1);
            if (toc == null) {
                try {
                    toc = bookWebService.getTOC(mBookId);
                } catch (ZLNetworkException e) {
                }
                TOCCache.getInstance().addTOC(toc, -1);
            }
            if (toc == null) {
                if (mCallback != null) {
                    mCallback.onDownloadError(mBookId);
                }
                return;
            }
            book.setTOC(toc);
            if (mCallback != null) {
                mCallback.onProgressUpdate(mBookId, 30);
            }
            // content
            int size = toc.getSize();
            BookContent bookContent = new BookContent();
            book.setBookContent(bookContent);
            for (int i = 0; i < size; i++) {
                VolumeItem item = toc.getItem(i);
                //跳过vip章节下载
                if (!sDownloadVipChapter && item.isVip()) {
                    continue;
                }
                Chapter chapter = ChapterCache.getInstance().getChapter(mBookId, item.getId());
                if (chapter == null) {
                    try {
                        chapter = bookWebService.getChapter(mBookId, item.getId());
                    } catch (ZLNetworkException e) {
                    }
                    ChapterCache.getInstance().addChapter(chapter);
                }
                /*
                if (chapter == null) {
                    if (mCallback != null) {
                        mCallback.onDownloadError(mBookId);
                    }
                    return;
                }
                */
                bookContent.addChapter(chapter);

                if (mCallback != null) {
                    mCallback.onProgressUpdate(mBookId, 30 + 60 * (i + 1) / size);
                }
            }

            new XBookManager().addBook(book);
            if (mCallback != null) {
                mCallback.onProgressUpdate(mBookId, 100);
            }
            if (mCallback != null) {
                mCallback.onDownloadFinished(mBookId);
            }
        }
    }
}
