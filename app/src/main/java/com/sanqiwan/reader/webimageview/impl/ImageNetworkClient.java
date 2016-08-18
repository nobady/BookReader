package com.sanqiwan.reader.webimageview.impl;

import com.sanqiwan.reader.util.IOUtil;
import com.sanqiwan.reader.webimageview.INetworkClient;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;
import org.geometerplus.zlibrary.core.network.ZLNetworkManager;
import org.geometerplus.zlibrary.core.network.ZLNetworkRequest;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 8/30/13
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageNetworkClient implements INetworkClient {

    private static final int BUFFER_SIZE = 1024;

    @Override
    public void loadBitmap(String imageUrl, final File file) throws Exception {
        ZLNetworkRequest networkRequest = new ZLNetworkRequest(imageUrl) {
            @Override
            public void handleStream(InputStream inputStream, int length) throws IOException, ZLNetworkException {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = new BufferedInputStream(inputStream, length);
                    out = new BufferedOutputStream(new FileOutputStream(file), length);
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int len = 0;

                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                } finally {
                    IOUtil.closeStream(in);
                    IOUtil.closeStream(out);
                }
            }
        };
        ZLNetworkManager.Instance().perform(networkRequest);
    }
}
