package com.moemoe.lalala.utils;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

/**
 * Created by Haru on 2016/4/29 0029.
 */
public class MimeType {
    private static final String TAG = "MimeType";

    public static final String IMAGE = "image/*";
    public static final String TEXT_PLAIN = "text/plain";

    public static final String M_MESSAGE_RFC822 = "message/rfc822";
    public static final String M_3GP = "video/3gpp";
    public static final String M_ZIP = "application/zip";
    public static final String M_RAR = "application/x-rar-compressed";
    public static final String M_PNG = "image/png";
    public static final String M_PDF = "application/pdf";
    public static final String M_MP4 = "video/mp4";
    public static final String M_MP3 = "audio/x-mpeg";
    public static final String M_M4A = "audio/mp4a-latm";
    public static final String M_M3U = "audio/x-mpegurl";
    public static final String M_JPG = "image/jpeg";
    public static final String M_HTML = "text/html";
    public static final String M_GZ = "application/x-gzip";
    public static final String M_GTAR = "application/x-gtar";
    public static final String M_GIF = "image/gif";
    public static final String M_EXE = "application/octet-stream";
    public static final String M_DOC = "application/msword";
    public static final String M_BMP = "image/bmp";
    public static final String M_BIN = "application/octet-stream";
    public static final String M_AVI = "video/x-msvideo";
    public static final String M_ASF = "video/x-ms-asf";
    public static final String M_APK = "application/vnd.android.package-archive";


    /**
     * get mime type by file suffix.
     * <p>i.e. fileSuffix = ".zip" return "application/zip"
     * @param fileSuffix file suffix, such as ".jpg" or "jpg"
     * @return mime type if has match, or null if didn't find mathch
     */
    public static String getMimeTypeByFileSuffix(String fileSuffix){
        String res = null;
        if(TextUtils.isEmpty(fileSuffix)){
            if(fileSuffix.startsWith(".")){
                fileSuffix = fileSuffix.substring(1);
            }
            res = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileSuffix);
        }
        return res;
    }
}
