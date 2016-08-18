package com.sanqiwan.reader.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 8/6/13
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class IOUtil {

    public static void deleteFile(File file) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                if (child.isFile()) {
                    child.delete();
                } else {
                    deleteFile(child);
                }
            }
        }
        file.delete();
    }

    public static int getFileCount(File file) {
        if (file.isFile()) {
            return 1;
        }

        int count = 0;
        File[] children = file.listFiles();

        if (children != null) {
            for (File child : children) {
                if (child.isFile()) {
                    count++;
                } else {
                    count += getFileCount(child);
                }
            }
        }

        return count;
    }

    public static void closeStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
