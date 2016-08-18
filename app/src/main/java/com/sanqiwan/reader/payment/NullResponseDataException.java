package com.sanqiwan.reader.payment;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-8
 * Time: 上午10:06
 * To change this template use File | Settings | File Templates.
 */
public class NullResponseDataException  extends RuntimeException  {

    public NullResponseDataException() {
    }

    public NullResponseDataException(String detailMessage) {
        super(detailMessage);
    }
}