package com.sanqiwan.reader.webservice;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/25/13
 * Time: 10:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class WebServiceConfig {

    public static final String SERVER_HOST = "http://sp.qwsy.com/book_37wan.aspx";
    public static final String SPID = "10016";
    public static final String SECRET_KEY = "c5d5a48ebfd2526151bc3573b7cf4e96";

    public String getHost() {
        return SERVER_HOST;
    }

    public String getSpid() {
        return SPID;
    }

    public String getSecretKey() {
        return SECRET_KEY;
    }
}
