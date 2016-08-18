package com.sanqiwan.reader.download;

import android.util.Log;
import com.sanqiwan.reader.notification.DownloadNotify;
import com.sanqiwan.reader.util.UIUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-7-23
 * Time: 上午10:35
 * To change this template use File | Settings | File Templates.
 */
public class DownloadManager {
    public interface Callback {
        public void onUpdateProgress(long bookId, int progress);
        public void onDownloadFinished(long bookId);
        public void onDownloadError(long bookId);
    }

    private static final int ERROR = 0;
    public static volatile DownloadManager sInstance;
    private ConcurrentHashMap<Long, DownloadProgress> mDownloadProgress;
    //推荐页面下载进度条处理，包括设置进度，下载完成后动画，以及按钮处理
    private List<Callback> mCallbacks;

    private DownloadManager() {
        mCallbacks = new ArrayList<Callback>();
        mDownloadProgress = new ConcurrentHashMap<Long, DownloadProgress>();
    }

    public static DownloadManager getInstance() {
        if (sInstance == null) {
            synchronized (DownloadManager.class) {
                if (sInstance == null) {
                    sInstance = new DownloadManager();
                }
            }
        }
        return sInstance;
    }

    public void startDownload(long bookId, final String bookName) {
        XBookDownloader bookDownloader = new XBookDownloader();
        XBookDownloader.Callback callback = new XBookDownloader.Callback() {

            @Override
            public void onDownloadStarted(long bookId) {
            }

            @Override
            public void onProgressUpdate(final long bookId, final int progress) {
                if (mDownloadProgress.containsKey(bookId)) {
                    mDownloadProgress.get(bookId).setProgress(progress);
                } else {
                    DownloadProgress downloadProgress = new DownloadProgress();
                    downloadProgress.setBookId(bookId);
                    downloadProgress.setProgress(progress);
                    downloadProgress.setBookName(bookName);
                    mDownloadProgress.put(bookId, downloadProgress);
                }
                UIUtil.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        for (Callback call : mCallbacks) {
                            call.onUpdateProgress(bookId, progress);
                        }
                        DownloadProgress downloadProgress = mDownloadProgress.get(bookId);
                        if (downloadProgress != null) {
                            DownloadNotify.getDownloadNotify().updateProgress(bookId, downloadProgress.getBookName(), progress);
                        }
                    }
                });
            }

            @Override
            public void onDownloadFinished(final long bookId) {
                UIUtil.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        for (Callback call : mCallbacks) {
                            call.onDownloadFinished(bookId);
                        }
                        removeDownloadRecord(bookId);
                    }
                });
            }

            @Override
            public void onDownloadError(final long bookId) {
                Log.d("book download error", "error bookId-" + bookId + ",progress-" + mDownloadProgress.get(bookId).getProgress());
                mDownloadProgress.remove(bookId);
                UIUtil.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        for (Callback call : mCallbacks) {
                            call.onDownloadError(bookId);
                        }
                    }
                });
            }
        };
        bookDownloader.setCallback(callback);
        bookDownloader.startDownload(bookId);
    }

    public void addCallback(Callback callback) {
        if (callback != null) {
            mCallbacks.add(callback);
        }
    }

    public void removeCallback(Callback callback) {
        if (callback != null) {
            mCallbacks.remove(callback);
        }
    }

    public void removeDownloadRecord(long bookId) {
        if (mDownloadProgress == null) {
            return;
        }
        if (mDownloadProgress.containsKey(bookId)) {
            DownloadProgress download = mDownloadProgress.get(bookId);
            mDownloadProgress.remove(bookId, download);
        }
    }

    public int queryProgress(long bookId) {
        if (mDownloadProgress == null) {
            return ERROR;
        }
        if (mDownloadProgress.containsKey(bookId)) {
            return mDownloadProgress.get(bookId).getProgress();
        }
        return ERROR;
    }

    public class DownloadProgress {
        private long mBookId;
        private int mProgress;
        private String mBookName;

        public long getBookId() {
            return mBookId;
        }

        public void setBookId(long bookId) {
            this.mBookId = bookId;
        }

        public int getProgress() {
            return mProgress;
        }

        public void setProgress(int progress) {
            this.mProgress = progress;
        }

        public String getBookName() {
            return mBookName;
        }

        public void setBookName(String bookName) {
            this.mBookName = bookName;
        }
    }
}
