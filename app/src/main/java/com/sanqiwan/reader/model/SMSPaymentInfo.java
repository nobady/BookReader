package com.sanqiwan.reader.model;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-7
 * Time: 上午11:11
 * To change this template use File | Settings | File Templates.
 */
public class SMSPaymentInfo {
    public static final int SMS_RATIO = 40;
    private int mUserId;
    private int mPayMoney;
    private String mPhoneNum;
    private int mPayType;

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        this.mUserId = userId;
    }

    public int getPayMoney() {
        return mPayMoney;
    }

    public void setPayMoney(int payMoney) {
        this.mPayMoney = payMoney;
    }

    public String getPhoneNum() {
        return mPhoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.mPhoneNum = phoneNum;
    }

    public int getPayType() {
        return mPayType;
    }

    public void setPayType(int payType) {
        this.mPayType = payType;
    }
}
