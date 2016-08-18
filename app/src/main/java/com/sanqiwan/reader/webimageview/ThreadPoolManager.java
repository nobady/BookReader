package com.sanqiwan.reader.webimageview;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolManager {
    private ExecutorService mExecutorService;
    private static final int THREAD_NUM = 4;

    private ThreadPoolManager() {
        mExecutorService = Executors.newFixedThreadPool(THREAD_NUM);
    }

    private volatile static ThreadPoolManager sInstance;

    public static ThreadPoolManager getInstance() {
        if (sInstance == null) {
            synchronized (ThreadPoolManager.class) {
                if (sInstance == null) {
                    sInstance = new ThreadPoolManager();
                }
            }
        }
        return sInstance;
    }

    public void addTask(Runnable runnable) {
        mExecutorService.execute(runnable);
    }
}
