package com.sanqiwan.reader.util;

import android.content.Context;
import android.widget.Toast;
import com.sanqiwan.reader.R;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 11/28/13
 * Time: 8:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateUtil {
    public static void checkUpdate(final Context context) {
        UmengUpdateAgent.forceUpdate(context);
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case 0: // has update
                        UmengUpdateAgent.showUpdateDialog(context, updateInfo);
                        break;
                    case 1: // has no update
                        Toast.makeText(context, R.string.no_update_tip, Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case 2: // none wifi
                        Toast.makeText(context, R.string.no_wifi_tip, Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case 3: // time out
                        Toast.makeText(context, R.string.time_out_tip, Toast.LENGTH_SHORT)
                                .show();
                        break;
                }
                UmengUpdateAgent.setUpdateAutoPopup(true);
            }
        });
    }
}
