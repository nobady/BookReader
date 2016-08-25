package com.sanqiwan.reader.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * Created with IntelliJ IDEA.
 * User: sam
 * Date: 11/8/13
 * Time: 8:17 PM
 * To change this template use File | Settings | File Templates.
 */
@Root(name = "data",strict = false)
public class AccountResult {
    private int mCode;
    private String mMessage;

    public AccountResult() {
    }

    public AccountResult(int code, String msg) {
        mCode = code;
        mMessage = msg;
    }

    public void setCode(int code) {
        mCode = code;
    }

    public void setMessage(String msg) {
        mMessage = msg;
    }

    public int getCode() {
        return mCode;
    }

    public String getMessage() {
        return mMessage;
    }
}

