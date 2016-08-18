package com.sanqiwan.reader;

import android.content.Context;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/22/13
 * Time: 2:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppContext {

    private static Context sContext;

    public static void init(Context context) {
        sContext = context;
    }

    public static Context getInstance() {
        return sContext;
    }

    private AppContext() {

    }
}
