package com.sanqiwan.reader.webimageview;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 8/30/13
 * Time: 4:00 PM
 * To change this template use File | Settings | File Templates.
 */
public interface INetworkClient {
    public void loadBitmap(String url, File file) throws Exception;
}
