package com.sanqiwan.reader.util;

import android.widget.Toast;
import com.sanqiwan.reader.AppContext;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 12/3/13
 * Time: 9:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToastUtil {
    public static void showToast(int resId) {
        Toast.makeText(AppContext.getInstance(), resId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(CharSequence text) {
        Toast.makeText(AppContext.getInstance(), text, Toast.LENGTH_SHORT).show();
    }
}
