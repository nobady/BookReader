package com.sanqiwan.reader.payment.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.sanqiwan.reader.payment.*;
import com.sanqiwan.reader.view.CmccConfirmDialog;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-4
 * Time: 下午2:28
 * To change this template use File | Settings | File Templates.
 */
public class PaymentDetailView extends FrameLayout {

    private Context mContext;
    private IPayment mPayment;

    public PaymentDetailView(Context context) {
        this(context, null);
        mContext = context;
    }

    public PaymentDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }


    public void setPayment (IPayment payment, Context context, CmccConfirmDialog.CallBack callBack) {
        mPayment = payment;
        addView(context, callBack);
    }

    //返回当前的payment
    public IPayment getCurrentPayment() {
        return mPayment;
    }

    /**
     * 其实添加的是具体支付方式的view
     * 比如，选择的是支付宝支付，那么添加的view就是支付宝的金额选择view
     * 这个view是在支付宝支付页面完成的，这里通过接口来调用
     * @param context
     * @param callBack
     */
    private void addView(Context context, CmccConfirmDialog.CallBack callBack) {
        removeAllViews();
        addView(mPayment.getView(context, callBack));
    }

}
