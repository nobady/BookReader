package com.sanqiwan.reader.net;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips] Created by tengfei.lv on 2016/8/3 15:20.
 */

public class CustomException extends RuntimeException {

    public static int SUCCESS = 0;
    public static int FAIL = 1;
    public static int TIMEOUT = 2;
    public static int NETWORK_UNAVAILABLE = 3;
    public static int NO_MORE_RECORD=10;

    private int Code;
    private String Message;

    public CustomException (int code, String message) {
        Code = code;
        Message = message;
    }

    public CustomException () {
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    @Override
    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
