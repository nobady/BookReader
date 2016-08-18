package com.sanqiwan.reader.model;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-7
 * Time: 下午3:25
 * To change this template use File | Settings | File Templates.
 */
public class PaymentResponseInfo {
    private boolean mIsSuccess;
    private String mOrderInfo;
    private String mMsg;
    private int mPayMoney;

    public boolean isSuccess() {
        return mIsSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.mIsSuccess = isSuccess;
    }

    public String getOrderInfo() {
        return mOrderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.mOrderInfo = orderInfo;
    }

    public String getMsg() {
        return mMsg;
    }

    public void setMsg(String msg) {
        this.mMsg = msg;
    }

    public int getPayMoney() {
        return mPayMoney;
    }

    public void setPayMoney(int payMoney) {
        this.mPayMoney = payMoney;
    }
}
