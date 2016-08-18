package com.sanqiwan.reader.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 8/21/13
 * Time: 10:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class SecurityUtil {


    public static String md5(String source) {
        try {
            return md5(source.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return StringUtil.EMPTY_STRING;
    }


    public static String md5(byte[] source) {

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(source);
            byte tmp[] = md.digest();

            return StringUtil.byteToString(tmp);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return StringUtil.EMPTY_STRING;
    }


}
