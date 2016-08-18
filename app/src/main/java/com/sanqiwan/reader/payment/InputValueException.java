package com.sanqiwan.reader.payment;

/**
 * Created with IntelliJ IDEA.
 * User: IBM
 * Date: 13-11-8
 * Time: 上午10:03
 * To change this template use File | Settings | File Templates.
 */
public class InputValueException extends RuntimeException  {

    public InputValueException() {
    }

    public InputValueException(String detailMessage) {
        super(detailMessage);
    }
}
