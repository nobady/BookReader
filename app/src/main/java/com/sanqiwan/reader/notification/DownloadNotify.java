package com.sanqiwan.reader.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.sanqiwan.reader.AppContext;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.ui.ReaderActivity;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-22
 * Time: 下午12:12
 * To change this template use File | Settings | File Templates.
 */
public class DownloadNotify {

    private static DownloadNotify sDownloadNotify;
    //获取系统的NotificationManager服务
    private NotificationManager mNotifyManager;
    //创建Notification对象
    private static Notification NOTIFY;
    private static Context sContext;
    private static ConcurrentHashMap<Long, PendingIntent> mPendingMap;

    private DownloadNotify() {
        NOTIFY = new Notification();
        sContext = AppContext.getInstance();
        mPendingMap = new ConcurrentHashMap<Long, PendingIntent>();
    }

    public static DownloadNotify getDownloadNotify() {
        if (sDownloadNotify == null) {
            synchronized (DownloadNotify.class) {
                if (sDownloadNotify == null) {
                    sDownloadNotify = new DownloadNotify();
                }
            }
        }
        return sDownloadNotify;
    }

    @TargetApi (Build.VERSION_CODES.JELLY_BEAN)
    private void startNotification(long bookId, String bookName, int initProgress) {

        mNotifyManager = (NotificationManager) sContext.getSystemService(sContext.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(sContext);
        setPendingIntent(bookId);
//        builder.setContentInfo("补充内容");
        builder.setContentText(initProgress + sContext.getResources().getString(R.string.percent));
        builder.setContentTitle(bookName);
        builder.setSmallIcon(R.drawable.qw_icon);
//        builder.setTicker("新消息");
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentIntent( mPendingMap.get(bookId));
        Notification notification = builder.build();
        mNotifyManager.notify(getNotificationID(bookId), notification);

//
//        setPendingIntent(bookId);
//        NOTIFY.flags = Notification.FLAG_AUTO_CANCEL;
//        //设置Notification的发送时间
//        NOTIFY.when = System.currentTimeMillis();
//
//        NOTIFY.icon = R.drawable.qw_icon;
//        //设置Notification事件信息
//        NOTIFY.setLatestEventInfo(sContext, bookName,
//                initProgress + sContext.getResources().getString(R.string.percent), mPendingMap.get(bookId));
//        //设置Notification的文本内容,会显示在状态栏中
//        NOTIFY.tickerText = bookName;
//        mNotifyManager.notify(getNotificationID(bookId), NOTIFY);
    }
    @TargetApi (Build.VERSION_CODES.JELLY_BEAN)
    public void updateProgress(long bookId, String bookName, int update) {
        if (!mPendingMap.contains(bookId)) {
            startNotification(bookId, bookName, update);
            return;
        }
        Notification.Builder builder = new Notification.Builder(sContext);
        setPendingIntent(bookId);
        //        builder.setContentInfo("补充内容");
        builder.setContentText( update + sContext.getResources().getString(R.string.percent).toString());
        builder.setContentTitle(bookName);
        builder.setSmallIcon(R.drawable.qw_icon);
        //        builder.setTicker("新消息");
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentIntent( mPendingMap.get(bookId));
        Notification notification = builder.build();
//        NOTIFY.setLatestEventInfo(sContext, bookName,
//                update + sContext.getResources().getString(R.string.percent).toString(), mPendingMap.get(bookId));
        mNotifyManager.notify(getNotificationID(bookId), notification);

    }

    public void clearNotification(long bookId) {
        mNotifyManager.cancel(getNotificationID(bookId));
        mPendingMap.remove(bookId);
    }

    private int getNotificationID(long bookId) {
        return (int) bookId;
    }

    private void setPendingIntent(long bookId) {
        Intent intent = ReaderActivity.getOpenBookIntent(sContext, bookId, 0);
        PendingIntent pendingIntent = PendingIntent.getActivity(sContext, 0, intent, 0);
        mPendingMap.put(bookId, pendingIntent);
    }
}
