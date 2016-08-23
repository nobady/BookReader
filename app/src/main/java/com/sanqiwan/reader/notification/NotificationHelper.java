package com.sanqiwan.reader.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import com.sanqiwan.reader.AppContext;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.ui.MainActivity;


/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-22
 * Time: 下午12:12
 * To change this template use File | Settings | File Templates.
 */
public class NotificationHelper {

    private static volatile NotificationHelper sNotificationHelper;
    //获取系统的NotificationManager服务
    private NotificationManager mNotifyManager;
    //创建Notification对象
    private NotificationCompat.Builder mNotifyBuilder;
    private Context mContext;

    private NotificationHelper() {
        mContext = AppContext.getInstance();
        mNotifyBuilder = new NotificationCompat.Builder(mContext);
    }

    public static NotificationHelper getInstance() {
        if (sNotificationHelper == null) {
            synchronized (NotificationHelper.class) {
                if (sNotificationHelper == null) {
                    sNotificationHelper = new NotificationHelper();
                }
            }
        }
        return sNotificationHelper;
    }

    private void initNotifyManager() {
        mNotifyManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
    }

    private void initNotifyBuilder(String title, String ticker) {
        mNotifyBuilder.setAutoCancel(true);
        mNotifyBuilder.setSmallIcon(R.drawable.qw_icon);
        //设置Notification的文本内容,会显示在状态栏中
        mNotifyBuilder.setContentTitle(title);
        mNotifyBuilder.setTicker(ticker);
    }

    private String getStringRes(int resId) {
        return AppContext.getInstance().getResources().getString(resId);
    }

    public void wakeUpNotify() {
        int notifyId = 8888;
        if (mNotifyManager == null) {
            initNotifyManager();
        }
        initNotifyBuilder(getStringRes(R.string.app_name), getStringRes(R.string.app_name));
        mNotifyBuilder.setContentText(getStringRes(R.string.wake_up_content_txt));
        mNotifyBuilder.setProgress(0, 0, false);
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mNotifyBuilder.setContentIntent(PendingIntent.getActivity(mContext, 0, intent, 0));
        mNotifyBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        Notification notification = mNotifyBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotifyManager.notify(notifyId, notification);
    }
}
