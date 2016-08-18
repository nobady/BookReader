package com.sanqiwan.reader.util;

import android.text.Html;
import android.text.format.Formatter;
import android.util.Log;
import com.sanqiwan.reader.AppContext;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;

/**
 * Created with IntelliJ IDEA.
 * User: chen
 * Date: 7/25/13
 * Time: 5:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringUtil {

    private static final String LOG_TAG = "StringUtil";
    public static final String EMPTY_STRING = "";
    private static final String ENCODING = "utf-8";
    private static int BUFFER_SIZE = 4096;
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};

    public static String toString(InputStream inputStream) {
        byte[] bytes = toByteArray(inputStream);
        return new String(bytes);
    }

    private static byte[] toByteArray(InputStream inputStream) {

        if (inputStream == null) {
            return EMPTY_BYTE_ARRAY;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[BUFFER_SIZE];

        int count;
        try {
            while ((count = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, count);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        return outputStream.toByteArray();
    }

    public static boolean saveStringToFile(String content, File file) {
        OutputStream outputStream = null;
        try {
            ensureDirectory(file);
            outputStream = new FileOutputStream(file);
            outputStream.write(content.getBytes(ENCODING));
            return true;
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            IOUtil.closeStream(outputStream);
        }

        return false;
    }

    private static void ensureDirectory(File file) {
        File parent = file.getParentFile();
        if (!parent.exists()) {
            boolean success = parent.mkdirs();
            if (!success) {
                parent.mkdir();
            }
        }
    }

    public static String loadStringFromFile(File file) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return toString(inputStream);
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            IOUtil.closeStream(inputStream);
        }

        return EMPTY_STRING;
    }

    public static String byteToString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return EMPTY_STRING;
        }

        int length = bytes.length;
        char chars[] = new char[length * 2];
        for (int i = 0; i < length; i++) {
            byte b = bytes[i];
            int charIndex = i * 2;
            chars[charIndex] = HEX_DIGITS[b >>> 4 & 0xf];
            chars[charIndex + 1] = HEX_DIGITS[b & 0xf];
        }
        String result = new String(chars);
        return result;
    }

    public static String getSizeText(long totalBytes) {
        String sizeText = "";
        if (totalBytes >= 0) {
            sizeText = Formatter.formatFileSize(AppContext.getInstance(), totalBytes);
        }
        return sizeText;
    }

    public static String toDateString(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");//初始化Formatter的转换格式。
        return formatter.format(time);
    }

    public static String optStringFromJsonObject(JSONObject object, String key) {
        return Html.fromHtml(object.optString(key)).toString();
    }

    public static String optStringFromJsonArray(JSONArray array, int position) {
        return Html.fromHtml(array.optString(position)).toString();
    }
}
