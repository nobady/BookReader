package com.sanqiwan.reader.payment;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-8
 * Time: 上午10:04
 * To change this template use File | Settings | File Templates.
 */
public class NullPostDataException  extends RuntimeException  {

    public NullPostDataException() {
    }

    public NullPostDataException(String detailMessage) {
        super(detailMessage);
    }
}
