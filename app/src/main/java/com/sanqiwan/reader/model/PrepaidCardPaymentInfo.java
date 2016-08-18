package com.sanqiwan.reader.model;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-7
 * Time: 上午11:07
 * To change this template use File | Settings | File Templates.
 */
public class PrepaidCardPaymentInfo {
    public static final int PREPAID_CARD_RATIO = 90;
    private int mUserId;
    private int mPayMoney;
    private String mCardNum;
    private String mCardPassword;
    private int mPayType;

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        this.mUserId = userId;
    }

    public String getCardNum() {
        return mCardNum;
    }

    public void setCardNum(String cardNum) {
        this.mCardNum = cardNum;
    }

    public int getPayMoney() {
        return mPayMoney;
    }

    public void setPayMoney(int payMoney) {
        this.mPayMoney = payMoney;
    }

    public String getCardPassword() {
        return mCardPassword;
    }

    public void setCardPassword(String cardPassword) {
        this.mCardPassword = cardPassword;
    }

    public int getPayType() {
        return mPayType;
    }

    public void setPayType(int payType) {
        this.mPayType = payType;
    }
}
