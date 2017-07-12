package com.eof.libs.base.utils;

import com.eof.libs.base.debug.Log;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;

/**
 * AES加密解密处理类
 */
public class AES {
    private final static String TAG = "AES";

    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String DEFAULT_ALGORITHM_MODE = "AES/CBC/NoPadding";
    private static final String DEFAULT_ALGORITHM = "AES";

    private String encoding = DEFAULT_ENCODING;
    private String algorithmMode = DEFAULT_ALGORITHM_MODE;
    private String algorithm = DEFAULT_ALGORITHM;

    public AES() {
        this(DEFAULT_ALGORITHM, DEFAULT_ALGORITHM_MODE, DEFAULT_ENCODING);
    }

    public AES(String algorithm, String algorithmMode) {
        this(algorithm, algorithmMode, DEFAULT_ENCODING);
    }

    public AES(String algorithm, String algorithmMode, String encoding) {
        this.algorithm = algorithm;
        this.algorithmMode = algorithmMode;
        this.encoding = encoding;
    }

    public byte[] encrypt(String text, String iv, String secretKey) throws Exception {
        if (text == null || text.length() == 0) {
            throw new Exception("Empty string");
        }
        byte[] encrypted = null;
        try {
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes(encoding));
            SecretKeySpec keyspec = new SecretKeySpec(secretKey.getBytes(), algorithm);
            Cipher cipher = Cipher.getInstance(algorithmMode);
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            encrypted = cipher.doFinal(padString(text).getBytes());
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.toString());
        } catch (NoSuchPaddingException e) {
            Log.e(TAG, e.toString());
        }
        return encrypted;
    }

    public byte[] decrypt(byte[] code, String iv, String secretKey) throws Exception {
        if (code == null || code.length == 0) {
            throw new Exception("Empty string");
        }

        byte[] decrypted = new byte[0];
        try {
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes(encoding));
            SecretKeySpec keyspec = new SecretKeySpec(secretKey.getBytes(), algorithm);
            Cipher cipher = Cipher.getInstance(algorithmMode);
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            decrypted = cipher.doFinal(code);
        } catch (Throwable e) {
            Log.e(Log.TAG, e.toString());
        }
        return decrypted;
    }

    private static String padString(String source) {
        char paddingChar = ' ';
        int size = 16;
        int x = source.length() % size;
        if (0 == x) {
            return source;
        }

        int padLength = size - x;

        for (int i = 0; i < padLength; i++) {
            source += paddingChar;
        }

        return source;
    }
}
