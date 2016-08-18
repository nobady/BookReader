package com.sanqiwan.reader.util;

/**
 * Created with IntelliJ IDEA.
 * User: jwb
 * Date: 13-11-26
 * Time: 下午4:29
 * To change this template use File | Settings | File Templates.
 */
public class StarSumUtil {

    public static int getStarSum(int visit) {
        if (visit < 5000) {
            return 1;
        } else if (5000 <= visit && visit < 20000) {
            return 2;
        } else if (20000 <= visit && visit < 40000) {
            return 3;
        } else if (40000 <= visit && visit < 60000) {
            return 4;
        } else {
            return 5;
        }
    }

}
