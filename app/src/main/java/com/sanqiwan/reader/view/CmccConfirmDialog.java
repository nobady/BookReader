package com.sanqiwan.reader.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import com.sanqiwan.reader.R;
import com.sanqiwan.reader.engine.UserManager;
import com.sanqiwan.reader.model.PaymentResponseInfo;
import com.sanqiwan.reader.util.SMSUtil;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-20
 * Time: 上午11:59
 * To change this template use File | Settings | File Templates.
 */
public class CmccConfirmDialog extends Dialog implements View.OnClickListener {
    public interface CallBack {
        public void onOpenPaymentSuccessFragment(PaymentResponseInfo responseInfo);
    }
    private static final int RATIO = 50;
    private Context mContext;
    private int mPaymentValue;
    private CallBack mCallback;

    public CmccConfirmDialog(Context context, int layout, int style, int paymentValue, CallBack callback) {
        super(context, style);
        setContentView(layout);
        mContext = context;
        mPaymentValue = paymentValue;
        mCallback = callback;
        findViewById(R.id.closed_btn).setOnClickListener(this);
        findViewById(R.id.dialog_cancel).setOnClickListener(this);
        findViewById(R.id.dialog_confirm).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_cancel:
                dismiss();
                break;
            case R.id.dialog_confirm:
                dismiss();
                sendSMS();
                openPaymentSuccessFragment();
                break;
            case R.id.closed_btn:
                dismiss();
                break;
        }
    }

    private void sendSMS() {
        String body = mPaymentValue * 10 + mContext.getString(R.string.cmcc_payment_delimiter) + UserManager.getInstance().getUserId();
        SMSUtil.sendSMS(mContext.getResources().getString(R.string.cmcc_payment_num), body);
    }

    private void openPaymentSuccessFragment() {
        PaymentResponseInfo responseInfo = new PaymentResponseInfo();
        responseInfo.setIsSuccess(true);
        responseInfo.setMsg(mContext.getString(R.string.cmcc_payment_msg));
        responseInfo.setPayMoney(convertMoney(mPaymentValue));
        mCallback.onOpenPaymentSuccessFragment(responseInfo);
    }

    private int convertMoney(int value) {
        return value * RATIO;
    }
}
