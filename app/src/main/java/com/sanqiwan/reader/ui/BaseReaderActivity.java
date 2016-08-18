package com.sanqiwan.reader.ui;

import android.view.WindowManager;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidLibrary;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 10/30/13
 * Time: 8:50 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseReaderActivity extends BaseActivity {

    public int getScreenBrightness() {
        final int level = (int) (100 * getWindow().getAttributes().screenBrightness);
        return (level >= 0) ? level : 50;
    }

    public void setScreenBrightness(int percent) {
        if (percent < 1) {
            percent = 1;
        } else if (percent > 100) {
            percent = 100;
        }
        final WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.screenBrightness = percent / 100.0f;
        getWindow().setAttributes(attrs);
        getZLibrary().ScreenBrightnessLevelOption.setValue(percent);
    }

    protected ZLAndroidLibrary getZLibrary() {
        return (ZLAndroidLibrary) ZLAndroidLibrary.Instance();
    }
}
