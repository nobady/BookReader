package com.sanqiwan.reader.util;
/**
 * Created by sam on 2/19/14.
 */
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesEncrypt {
    private static byte[] sIv = {1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8};
    public static String encrypt(String encryptString, String encryptKey) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(sIv);
        SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
        byte[] encryptedData = cipher.doFinal(encryptString.getBytes());

        return new String(Base64.encode(encryptedData, Base64.NO_PADDING | Base64.NO_WRAP));
    }

    public static String decrypt(String decryptString, String decryptKey) throws Exception {
        byte[] byteMi =Base64.decode(decryptString.getBytes(),  Base64.NO_PADDING | Base64.NO_WRAP);
        IvParameterSpec zeroIv = new IvParameterSpec(sIv);
        SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
        byte decryptedData[] = cipher.doFinal(byteMi);

        return new String(decryptedData);
    }
}