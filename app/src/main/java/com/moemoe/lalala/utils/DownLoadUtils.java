package com.moemoe.lalala.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

/**
 * Created by yi on 2017/10/13.
 */

public class DownLoadUtils {
    /** 加密工具 */
    private static Cipher encryptCipher = null;

    /** 解密工具 */
    private static Cipher decryptCipher = null;

    public static void initalize_dencryptkey(String keyValue) throws Exception {
        Key key = getKey(keyValue.getBytes());
        decryptCipher = Cipher.getInstance("DES");
        decryptCipher.init(Cipher.DECRYPT_MODE, key);
    }

    /**
     * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
     *
     * @param arrBTmp
     *            构成该字符串的字节数组
     * @return 生成的密钥
     * @throws java.lang.Exception
     */
    private static Key getKey(byte[] arrBTmp) throws Exception {
        // 创建一个空的8位字节数组（默认值为0）
        byte[] arrB = new byte[8];

        // 将原始字节数组转换为8位
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }
        // 生成密钥
        Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");
        return key;
    }

    /**
     * 解密字节数组
     *
     * @param arrB
     *            需解密的字节数组
     * @return 解密后的字节数组
     * @throws Exception
     */
    public static byte[] decrypt(byte[] arrB) throws Exception {
        return decryptCipher.doFinal(arrB);
    }


}
