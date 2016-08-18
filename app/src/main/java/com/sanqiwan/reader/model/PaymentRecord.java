package com.sanqiwan.reader.model;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 11/11/13
 * Time: 9:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaymentRecord {
    private String mChannel;
    private long mTime;
    private int mAmount;

    public String getChannel() {
        return mChannel;
    }

    public void setChannel(String channel) {
        mChannel = channel;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public int getAmount() {
        return mAmount;
    }

    public void setAmount(int amount) {
        mAmount = amount;
    }
}
