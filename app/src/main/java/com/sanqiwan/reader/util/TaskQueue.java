package com.sanqiwan.reader.util;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-8-7
 * Time: 下午3:20
 * To change this template use File | Settings | File Templates.
 */
public class TaskQueue {

    private BlockingQueue<Task> mTaskQueue;
    private Set<String> mTaskKeySet;
    private boolean mRunning = false;

    private static class Task {
        String mKey;
        Runnable mRunnable;

        private Task(String key, Runnable runnable) {
            this.mKey = key;
            this.mRunnable = runnable;
        }
    }

    public TaskQueue() {
        mTaskQueue = new LinkedBlockingQueue<Task>();
        mTaskKeySet = new HashSet<String>();
    }

    public void start()  {
        if (!mRunning) {
            new Thread() {
                @Override
                public void run() {
                    loop();
                }
            }.start();
        }
    }

    public void offerTask(String key, Runnable runnable) {
        if (!isTaskExist(key)) {
            mTaskKeySet.add(key);
            mTaskQueue.offer(new Task(key, runnable));
        }
    }

    public boolean isTaskExist(String key) {
        synchronized (mTaskKeySet) {
            if (mTaskKeySet.contains(key)) {
                return true;
            }
        }
        return false;
    }

    private void loop() {
        mRunning = true;
        while (true) {
            try {
                Task task = mTaskQueue.take();
                task.mRunnable.run();
                synchronized (mTaskKeySet) {
                    mTaskKeySet.remove(task.mKey);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
