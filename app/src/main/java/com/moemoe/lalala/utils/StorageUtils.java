package com.moemoe.lalala.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.io.File;

/**
 * Created by yi on 2016/11/28.
 */

public class StorageUtils {
    private static final String TAG = "StorageUtils";

    /**
     * SD卡文件根目录
     */
    private static String sDirRoot;

    /**
     * 图片文件位置
     */
    private static String sDirIconImage;

    /**
     * 根目录/gallery/xxx.jpg 放某些机型的保存到相册的图片
     */
    private static String sDirGalleryImage;

    /**
     * 数据文件位置
     */
    private static String sDirData;

    // private static String sDir

    /**
     * 临时文件，对外部分享文件路径 考虑放在内置sd卡中
     */
    private static String sDirTemp;

    /**
     * 捕获异常日志位置
     */
    private static String sDirLog;

    /**
     * 音乐文件位置
     */
    private static String sDirMusic;

    /**
     * 小说等文本文件位置
     */
    private static String sDirTxt;

    /**
     *
     * @param context
     */
    public static final void initialStorageDir(Context context) {
        File root = new File(Environment.getExternalStorageDirectory(), "ZhaiJidi");
        sDirRoot = root.getAbsolutePath();
        if (!sDirRoot.endsWith(File.separator)) {
            sDirRoot += File.separator;
        }
        sDirGalleryImage = sDirRoot + "icons/";
        sDirIconImage = sDirRoot + ".icons/";
        sDirData = sDirRoot + "details/";

        sDirTemp = sDirRoot + "temp/";

        sDirLog = sDirRoot + "log/";

        sDirMusic = sDirRoot + "music/";

        sDirTxt = sDirRoot + "nov/";

        boolean check = checkDir(sDirRoot);
        check &= checkDir(sDirIconImage);
        check &= checkDir(sDirGalleryImage);
        check &= checkDir(sDirData);
        check &= checkDir(sDirTemp);
        check &= checkDir(sDirMusic);
        check &= checkDir(sDirTxt);
        //check &= checkDir(sDirLog);

        Logger.d("initialStorageDir = " + check + ", paths = " + sDirRoot + "; " + sDirTemp);
    }

    /**
     * 获取数据文件地址
     * @param filename
     * @return
     * @author Ben
     */
    public static String getDataPathByFileName(String filename) {
        if (!TextUtils.isEmpty(filename)) {
            return new File(sDirData, filename).getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * 获取相册照片根路径
     * @return
     */
    public static String getGalleryDirPath() {
        return sDirGalleryImage;
    }

    public static String getImageCachePath(){ return sDirIconImage;}

    /**
     * 获取zhiajidi目录下的相册图片
     * @param fileName
     * @return
     */
    public static String getGalleryPathByFileName(String fileName) {
        if (!TextUtils.isEmpty(fileName)) {
            return new File(sDirGalleryImage, fileName).getAbsolutePath();
        } else {
            return null;
        }
    }



    public static String getIconByFileName(String filename) {
        if (!TextUtils.isEmpty(filename)) {
            return new File(sDirIconImage, filename).getAbsolutePath();
        } else {
            return null;
        }
    }

    public static String getThumbByFileName(String filename) {
        if (!TextUtils.isEmpty(filename)) {
            if (filename.toLowerCase().contains(".gif")) {
                filename = filename.substring(0, filename.indexOf(".")) + ".jpg";
                Logger.e("gif thumb change to : " + filename);
            }
            return new File(sDirIconImage, "thumb_" + filename).getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * 获取原图路径
     * @param filename
     * @return
     * @author Ben
     */
    public static String getRawByFileName(String filename) {
        if (!TextUtils.isEmpty(filename)) {
            return new File(sDirIconImage, "raw_" + filename).getAbsolutePath();
        } else {
            return null;
        }
    }

    public static File getTempFile(String name) {
        if (!TextUtils.isEmpty(name)) {
            return new File(sDirTemp + name);
        } else {
            return null;
        }
    }

    /**
     * 是否临时文件
     * @param filePath
     * @return
     */
    public static boolean isTempFile(String filePath){
        boolean ret = false;
        if (!TextUtils.isEmpty(filePath) && filePath.contains(sDirTemp)) {
            ret = true;
        }
        return ret;
    }

    public static String getTempRootPath() {
        return sDirTemp;
    }

    public static String getNovRootPath(){
        return sDirTxt;
    }

    public static String getRootPath(){
        return sDirRoot;
    }

    public static String getLogRoot(){ return sDirLog;}

    public static File getLogFile(String name) {
        if (!TextUtils.isEmpty(name)) {
            return new File(sDirLog + name);
        } else {
            return null;
        }
    }

    /**
     * 检查文件夹是否存在，若不存在，创建该文件夹
     *
     * @param dirPath
     * @return
     */
    private static boolean checkDir(String dirPath) {
        boolean res = false;
        if (!TextUtils.isEmpty(dirPath)) {
            if (!new File(dirPath).exists()) {
                res = new File(dirPath).mkdirs();
            } else {
                res = true;
            }
        }
        return res;
    }

    // 返回是否有SD卡
    public static boolean GetSDState() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    public static boolean isMusicExit(String name){
        return FileUtil.isExists(sDirMusic + name);
    }

    public static void deleteMusic(){
        File file = new File(sDirMusic);
        File[] files = file.listFiles();
        if(files != null && files.length > 0){
            for (File f : files){
                f.delete();
            }
        }
    }

    public static String getMusicRootPath(){
        return sDirMusic;
    }

    public static String getMusicPath(String name){
        return sDirMusic + name;
    }

    public static String shareTemImg(String name){
        return sDirTemp + name;
    }

    public static String imgDir(String name){
        return sDirGalleryImage + name;
    }
}
