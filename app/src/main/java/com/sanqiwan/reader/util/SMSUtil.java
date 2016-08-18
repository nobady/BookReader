package com.sanqiwan.reader.util;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import com.sanqiwan.reader.AppContext;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-12
 * Time: 下午5:06
 * To change this template use File | Settings | File Templates.
 */
public class SMSUtil {

    private static final String TO = "smsto:";
    private static final String BODY = "sms_body";
    private static final String sentSmsAction = "SENT_SMS_ACTION";
    private static final String deliveredSmsAction = "DELIVERED_SMS_ACTION";

    public static void startSMSEditor(String smsUri, String body) {
        Context context = AppContext.getInstance();
        Uri smsToUri = Uri.parse(TO + smsUri);
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra(BODY, body);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public static void sendSMS(String smsto, String body) {
        SmsManager manager = SmsManager.getDefault();
        Intent sentIntent = new Intent(sentSmsAction);
        Intent deliveredIntent = new Intent(deliveredSmsAction);
        PendingIntent sentPi = PendingIntent.getBroadcast(AppContext.getInstance(), 0, sentIntent, 0);
        PendingIntent deliveryPi = PendingIntent.getBroadcast(AppContext.getInstance(), 0, deliveredIntent, 0);
        manager.sendTextMessage(smsto, null, body, sentPi, deliveryPi);
    }

}
