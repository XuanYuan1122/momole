package com.moemoe.lalala.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class EncoderUtils {

	private static final String TAG = "RSAUtils";

	private static final int BUFFER_SIZE = 1024;

	private static RSAEncoder mEncoder;

	public static void init(Context context) {
		try {
			mEncoder = new RSAEncoder();
			mEncoder.loadPublicKey(context.getAssets().open("otakuhouse_public_server.pem"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static RSAEncoder getEncoder() {
		return mEncoder;
	}  

	/**
	 * BASE64 加密
	 * 
	 * @param str
	 * @return
	 */
	public static String encryptBASE64(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		try {
			byte[] encode = str.getBytes("UTF-8");
			return encryptBASE64(encode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * BASE64 加密
	 * 
	 * @param str
	 * @return
	 */
	public static String encryptBASE64(byte[] data) {
		if (data == null || data.length == 0) {
			return null;
		}
		try {
			// base64 加密
			return new String(Base64.encode(data, 0, data.length, Base64.URL_SAFE), "UTF-8");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * BASE64 解密
	 * 
	 * @param str
	 * @return
	 */
	public static String decryptBASE64(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		try {
			byte[] encode = str.getBytes("UTF-8");
			// base64 解密
			return new String(Base64.decode(encode, 0, encode.length, Base64.URL_SAFE), "UTF-8");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static byte[] encryptGZIP(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		try {
			return encryptGZIP(str.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * GZIP 加密
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] encryptGZIP(byte[] date) {
		if (date == null || date.length == 0) {
			return null;
		}

		try {
			// gzip压缩
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(baos);
			gzip.write(date);

			gzip.close();

			byte[] encode = baos.toByteArray();

			baos.flush();
			baos.close();

			// base64 加密
			return encode;
			// return new String(encode, "UTF-8");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * GZIP 解密
	 * 
	 * @param str
	 * @return
	 */
	public static String decryptGZIP(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}

		try {

			byte[] decode = str.getBytes("UTF-8");

			// gzip 解压缩
			ByteArrayInputStream bais = new ByteArrayInputStream(decode);
			GZIPInputStream gzip = new GZIPInputStream(bais);

			byte[] buf = new byte[BUFFER_SIZE];
			int len = 0;

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			while ((len = gzip.read(buf, 0, BUFFER_SIZE)) != -1) {
				baos.write(buf, 0, len);
			}
			gzip.close();
			baos.flush();

			decode = baos.toByteArray();

			baos.close();

			return new String(decode, "UTF-8");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 十六进制字符串 转换为 byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789abcdef".indexOf(c);
		// return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * byte[] 转换为 十六进制字符串
	 * 
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");

		if (src == null || src.length <= 0) {
			return null;
		}

		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * MD5 32bit
	 * 
	 * @param inStr
	 * @return
	 */
	public static String MD5(String inStr) {
		MessageDigest md5 = null;
		if(TextUtils.isEmpty(inStr)){
			return null;
		}
		
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];

		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();

		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}

		return hexValue.toString();
	}

}
