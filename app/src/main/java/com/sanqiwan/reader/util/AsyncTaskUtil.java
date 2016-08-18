package com.sanqiwan.reader.util;

import android.os.AsyncTask;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/31/13
 * Time: 9:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class AsyncTaskUtil {

    public static <Param, Progress, Result> void execute(AsyncTask<Param, Progress, Result> asyncTask, Param... params) {

        try {
            asyncTask.execute(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
