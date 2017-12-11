package com.moemoe.lalala.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.view.activity.MultiImageChooseActivity;
import com.orhanobut.logger.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by yi on 2016/11/28.
 */

public class FileUtil {
    private static final String TAG = "FileUtil";

    private static final String Charset_UTF_8 = "utf-8";

    private FileUtil() {
    }


    public static String getDefaultBitmapPath(){
        return StorageUtils.getIconByFileName("default.png");
    }

    public static boolean renameOneFile(String srcPath, String dstPath) {
        boolean result = false;
        if (!TextUtils.isEmpty(srcPath) && !TextUtils.isEmpty(dstPath)) {
            if (!srcPath.equals(dstPath)) {
                File src = new File(srcPath);
                File dst = new File(dstPath);
                result = src.renameTo(dst);
                if(!result){
                    try {
                        copyFile(src, dst);
                        deleteOneFile(srcPath);
                        Logger.d("renameOneFile fail on copy succ");
                        result = true;
                    } catch (IOException e) {
                        Logger.e("renameOneFile error with copy error", e);
                    }
                }
            }
        }
        return result;
    }

    public static boolean deleteOneFile(String path) {
        boolean result = false;
        if (!TextUtils.isEmpty(path)) {
            File f = new File(path);
            if (f.exists() && f.isFile()) {
                result = f.delete();
            }
        }
        return result;
    }

    public static long getOneFileSize(String path) {
        long result = 0;
        if (!TextUtils.isEmpty(path)) {
            File f = new File(path);
            if (f.exists() && f.isFile()) {
                return f.length();
            }
        }
        return result;
    }

    public static String getOneFileSizeStr(String path) {
        String result = "0k";
        if (!TextUtils.isEmpty(path)) {
            File f = new File(path);
            if (f.exists() && f.isFile()) {
                long length = f.length();
                if(length > 1024 * 1024){
                    result = new DecimalFormat("#.00").format((float)length / 1024 / 1024) + "mb";
                }else{
                    result = new DecimalFormat("#.00").format((float)length / 1024) + "kb";
                }
            }
        }
        return result;
    }

    /**
     * 转换文件大小
     *
     * @param fileLen 单位B
     * @return
     */
    public static String formatFileSizeToString(long fileLen) {
        DecimalFormat df = new DecimalFormat("0.00");
        String fileSizeString = "";
        if (fileLen < 1024) {
            fileSizeString = df.format((double) fileLen) + "B";
        } else if (fileLen < 1048576) {
            fileSizeString = df.format((double) fileLen / 1024) + "K";
        } else if (fileLen < 1073741824) {
            fileSizeString = df.format((double) fileLen / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileLen / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 获取不重复的文件名， 例如 aaa_1.jpg, aaa_2.jpg
     * @param root
     * @param fileName
     * @return
     */
    public static String getUnDuplicatedFileName(String root, String fileName) {
        String ret = null;
        if (!TextUtils.isEmpty(root) && !TextUtils.isEmpty(fileName)) {
            File file = new File(root, fileName);
            if (file.exists()) {
                String suffix = getExtensionName(fileName);
                String name = fileName;
                if (!TextUtils.isEmpty(suffix)) {
                    suffix = "." + suffix;
                    name = fileName.substring(0, fileName.indexOf(suffix));
                }


                for (int i = 0; i < 200; i++) {
                    file = new File(root, name + "_" + i + suffix);
                    if (!file.exists()) {
                        break;
                    }
                }
                ret = file.getAbsolutePath();
                Logger.e("getUnDumplicatedFileName: " + ret);
            } else {
                ret = file.getAbsolutePath();
            }
        }
        return ret;
    }

    public static boolean writeStringTofile(String text, String path) {
        boolean result = false;
        if (!TextUtils.isEmpty(text) && !TextUtils.isEmpty(path)) {
            try {
                FileOutputStream fos = new FileOutputStream(path);
                fos.write(text.getBytes(Charset_UTF_8));
                fos.close();
                result = true;
            } catch (IOException e) {
                Logger.e("writeStringTofile", e);
            }
        }
        return result;
    }

    public static boolean moveDir(String from, String to) {
        boolean result = false;
        try {
            File dir = new File(from);
            // 文件一览
            File[] files = dir.listFiles();
            if (files == null)
                return result;
            // 目标
            File moveDir = new File(to);
            if (!moveDir.exists()) {
                moveDir.mkdirs();
            }
            // 文件移动
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    moveDir(files[i].getPath(), to + File.separator + files[i].getName());
                    // 成功，删除原文件
                    files[i].delete();
                }
                File moveFile = new File(moveDir.getPath() + File.separator + files[i].getName());
                // 目标文件夹下存在的话，删除
                if (moveFile.exists()) {
                    moveFile.delete();
                }
                files[i].renameTo(moveFile);
            }
            result = true;
        } catch (Exception e) {
            Logger.e("fileMove IOException from = " + from + "\t to = " + to, e);
        }
        return result;
    }

    public static boolean isExists(String path) {
        boolean result = false;
        if (!TextUtils.isEmpty(path)) {
            File f = new File(path);
            result = f.exists();
        }
        if (!result) {
            Logger.d("isExists " + path + result);
        }
        return result;
    }

    public static boolean deleteFile(String path){
        if (isGif(path)){
            return false;
        }
        File f = new File(path);
        if(isExists(path)){
            return f.delete();
        }
        return false;
    }

    public static void deleteDir(final String pPath) {
        File dir = new File(pPath);
        deleteDirWihtFile(dir);
    }

    public static void deletFileInDir(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
    }

    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    public static boolean copyFile(String src, String dst) {
        if (TextUtils.isEmpty(src))
            return false;
        try {
            return copyFile(new FileInputStream(src), dst);
        } catch (FileNotFoundException e) {
            Logger.e("", e);
        }
        return false;
    }

    public static boolean copyFile(InputStream in, FileOutputStream fos) {
        try {
            InputStream is = in;
            byte[] buf = new byte[10240];
            int len = -1;
            while ((len = is.read(buf)) > 0){
                fos.write(buf, 0, len);
            }
            fos.flush();
            fos.close();
            is.close();
            return true;
        } catch (IOException e) {
            Logger.e("",e);
        }
        return false;
    }

    public static boolean copyFile(InputStream in, String dst) {
        try {
            if (dst != null) {
                FileOutputStream fos = new FileOutputStream(dst);
                InputStream is = in;
                byte[] buf = new byte[10240];
                int len = -1;
                while ((len = is.read(buf)) > 0){
                    fos.write(buf, 0, len);
                }
                fos.flush();
                fos.close();
                is.close();
                return true;
            }
        } catch (FileNotFoundException e) {
            Logger.e("", e);
        } catch (IOException e) {
            Logger.e("", e);
        }
        return false;
    }
    /***
     *
     * @param src source file
     * @param dst destination file
     * @throws IOException
     */
    public static void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            closeSilently(inChannel);
            closeSilently(outChannel);
        }
    }

    public static void closeSilently(Closeable c){
        if (c == null)
            return;
        try {
            c.close();
        } catch (Throwable t) {
            // do nothing
        }
    }

    /**
     * 是否为gif图
     * @param path
     * @return
     */
    public static boolean isGif(String path) {
        boolean res = false;
        if (!TextUtils.isEmpty(path)) {
            if (path.toLowerCase().endsWith(".gif")) {
                res = true;
            }
        }
        return res;
    }


    public static boolean isValidGifFile(String path) {
        //4749463
        if (TextUtils.isEmpty(path))
            return false;
        DataInputStream bw = null;
        byte[] buffer = new byte[2];
        boolean isJpgOrPng = false;
        try {
            bw = new DataInputStream(new FileInputStream(path));
            bw.read(buffer, 0, 2);
            // if(buffer[0] == 0xFF && buffer[1] == 0xD8)
            if (buffer[0] == 71 && buffer[1] == 73) {	//
                isJpgOrPng = true;
            } else {
            }
            if (bw != null)
                bw.close();
        } catch (Exception e) {
        }
        return isJpgOrPng;

    }

    /***
     * 根据文件的头2个字节是不是0xFF 0xD8来判断文件是不是jpg格式的文件
     *
     * @param path
     *            文件路径
     * @return true is JPG format
     */
    private static boolean isJpgOrPngFile(String path) {
        if (TextUtils.isEmpty(path))
            return false;
        DataInputStream bw = null;
        byte[] buffer = new byte[2];
        boolean isJpgOrPng = false;
        try {
            bw = new DataInputStream(new FileInputStream(path));
            bw.read(buffer, 0, 2);
            // if(buffer[0] == 0xFF && buffer[1] == 0xD8)
            if (buffer[0] == -1 && buffer[1] == -40) {	//
                isJpgOrPng = true;
            } else if (buffer[0] == -119 && buffer[1] == 80) {
                isJpgOrPng = true;
            } else {
            }
            if (bw != null)
                bw.close();
        } catch (Exception e) {
        }
        return isJpgOrPng;
    }

    /**
     * 严格判断是否是正确格式的png和jpg图片
     * @param path 路径
     * @param checkSize 文件容量最大限制
     * @return
     */
    public static boolean isValidImageFile(String path, boolean checkSize) {
        boolean valid = false;
        if (isExists(path) && isJpgOrPngFile(path)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
                valid = false;
                FileUtil.deleteOneFile(path);
            } else {
                valid = true;
            }
        }
        return valid;
    }
    /***
     * 严格判断是否是正确格式的png和jpg图片
     * @param path
     * @return
     */
    public static boolean isValidImageFile(String path) {
        //return isValidImageFile(path, false);
        return isImageTypeBySuffix(path);
    }

    public static boolean isImageTypeBySuffix(String path){
        boolean ret = false;
        if (!TextUtils.isEmpty(path)) {
            if (path.toLowerCase().endsWith(".jpg")
                    || path.toLowerCase().endsWith(".png")
                    || path.toLowerCase().endsWith(".jpeg")) {
                ret = true;
            }
        }

        return ret;
    }

    /**
     * 仅根据文件后缀名判断是否为图片文件，包括 jpg，png，gif图片
     * @param path 文件路径
     * @return
     */
    public static boolean isImageFileBySuffix(String path){
        boolean ret = false;
        if (!TextUtils.isEmpty(path)) {
            if (path.toLowerCase().endsWith(".jpg") ||
                    path.toLowerCase().endsWith(".png") ||
                    path.toLowerCase().endsWith(".gif")||
                    path.toLowerCase().endsWith(".jpeg")) {
                ret = true;
            }
        }

        return ret;
    }

    public static String file2String(InputStream input, String charsetName) {
        StringBuilder sb = new StringBuilder(64);
        BufferedReader br = null;
        String line = null;
        try {
            br = new BufferedReader(new InputStreamReader(input, charsetName));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
        return sb.toString();
    }

    public static String readFileToString(File file) {
        InputStream in = null;
        try {
            // 一次读多个字节
            byte[] tempbytes;
            if(file.length() > 1024){
                tempbytes = new byte[1024];
            }else {
                tempbytes = new byte[(int) file.length()];
            }
            in = new FileInputStream(file);
            in.read(tempbytes);
            return new String(tempbytes,FileUtil.getCharset(file.getAbsolutePath()));
        } catch (Exception e1) {
            e1.printStackTrace();
            return "";
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    public static String file2String(String filepath, String charsetName) {
        StringBuilder sb = new StringBuilder(64);
        BufferedReader br = null;
        String line = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filepath)), charsetName));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
        return sb.toString();
    }

    public static boolean appendString2File(String fileName, String content) {
        boolean result = false;
        if ((!TextUtils.isEmpty(content)) && (!TextUtils.isEmpty(fileName))) {
            File file = new File(fileName);
            File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.exists()) {
                    parent.mkdirs();
                }
            }
            FileWriter fw = null;
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                fw = new FileWriter(fileName, true);
                fw.write(content);
                result = true;
            } catch (Exception e) {
                android.util.Log.e(TAG, e.getMessage(), e);
            } finally {
                if (fw != null) {
                    try {
                        fw.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        android.util.Log.e(TAG, "appendString2File result = " + result);
        return result;
    }
    /**
     * write string content to a file.
     * you can write multi-strings to a file by calling this method with multi-times.
     * @param fileOutputStream  file output stream to write
     * @param content   string content to write
     */
    public static void writeStringToFile(FileOutputStream fileOutputStream, String content){
        if ((fileOutputStream!=null)&&(!TextUtils.isEmpty(content))) {
            try {
                fileOutputStream.write(content.getBytes(Charset_UTF_8));
            }catch (UnsupportedEncodingException e) {
            }catch (IOException e) {
            }
        }else {
        }
    }
    /**
     * 从指定的文件路径 条件到相关的Uri
     * @param path
     * @return 文件对应的Uri
     */
    public static Uri getUrifromFilePath(String path){
        Uri uri = null;
        if(!TextUtils.isEmpty(path)){
            uri = Uri.fromFile(new File(path));
        }
        return uri;
    }
    /**
     * 获取指定文件名称的后缀
     * @param name 文件名
     * @return 文件名称的后缀
     */
    public static String getExtensionName(String name){
        String extenstion = "";
        if(!TextUtils.isEmpty(name)){
            int dot = name.lastIndexOf(".");
            if((dot > -1) && dot < (name.length()-1)){
                extenstion = name.substring(dot+1);
            }
        }
        return extenstion;
    }

    /**
     * 获取所有图片的大小
     * @param context
     * @return
     */
    public static long getAllPictureSize(Context context){
        return getPictureSize(new File(StorageUtils.getRootPath()));
    }

    public static long getCacheSize(Context context){
        long ret = 0;
        File tempRoot = new File(StorageUtils.getTempRootPath());
        ret += getDirSize(tempRoot);
        File imageRoot = new File(StorageUtils.getImageCachePath());
        ret += getDirSize(imageRoot);
        File logRoot = new File(StorageUtils.getLogRoot());
        ret += getDirSize(logRoot);
        File picassoRoot =  Glide.getPhotoCacheDir(context);
        ret += getDirSize(picassoRoot);

        return ret;
    }

    public static long clearAllCache(Context context){
        long ret = 0;
        File tempRoot = new File(StorageUtils.getTempRootPath());
        ret += clearDir(tempRoot);
        File imageRoot = new File(StorageUtils.getImageCachePath());
        ret += clearDir(imageRoot);
        File logRoot = new File(StorageUtils.getLogRoot());
        ret += clearDir(logRoot);
        File picassoRoot = Glide.getPhotoCacheDir(context);
        ret += clearDir(picassoRoot);
        return ret;
    }

    private static long clearDir(File root){
        long ret = 0;
        if(root.isDirectory()){
            File[] listFiles = root.listFiles();
            for(File file : listFiles){
                if(file.isDirectory()){
                    ret += clearDir(file);
                }else {
                    ret += file.length();
                    file.delete();
                }
            }
        }
        return ret;
    }

    public static long getDirSize(File root){
        long ret = 0;
        if(root.isDirectory()){
            File[] listFiles = root.listFiles();
            for(File file : listFiles){
                if(file.isDirectory()){
                    ret += getDirSize(file);
                }else {
                    ret += file.length();
                }
            }
        }
        return ret;
    }

    public static long getPictureSize(File root){
        long ret = 0;
        if (root.isDirectory()) {
            File[] listFiles = root.listFiles();
            for (int i = 0; i < listFiles.length; i++) {
                File childFile = listFiles[i];
                if (childFile.isDirectory()) {
                    // 文件目录，接着遍历
                    ret += getPictureSize(childFile);
                } else {
                    String fileName = childFile.getName();
                    if (isImageFileBySuffix(fileName)) {
                        ret += childFile.length();
                    }
                }
            }
        }
        return ret;
    }

    /**
     * 获取所有相册列表
     *
     * @return
     */
    public static ArrayList<MultiImageChooseActivity.AlbumModel> getAlbumList(Context context) {
        ArrayList<MultiImageChooseActivity.AlbumModel> albums = new ArrayList<MultiImageChooseActivity.AlbumModel>();

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] {"count("+ MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME+")", MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATA},
                MediaStore.Images.ImageColumns.SIZE+">1024) group by (" + MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                null,
                MediaStore.Images.ImageColumns.DATE_ADDED + " DESC");
        MultiImageChooseActivity.AlbumModel recent = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                String name = cursor.getString(1);
                //TODO 图片可能不存在，导致相册封面消失
                String path = cursor.getString(2);
                // 创建最近相册
                if(recent == null){
                    recent = new MultiImageChooseActivity.AlbumModel(context.getString(R.string.label_recent_photos), 0, path, true);
                }
                recent.count += count;
                MultiImageChooseActivity.AlbumModel album = new MultiImageChooseActivity.AlbumModel(name, count, path);
                albums.add(album);
            }
            cursor.close();
        }
        if(recent != null){
            albums.add(0, recent);
        }


        return albums;
    }

    /**
     * 获取某一相册的所有图片
     * @param context
     * @param albumName 相册名
     * @return
     */
    public static ArrayList<String> getOneAlbumPhotoList(Context context, String albumName) {
        ArrayList<String> images = new ArrayList<String>();
        if(context != null ){
            String where = !TextUtils.isEmpty(albumName) ? MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME+" = ? and " : "";
            where += MediaStore.Images.ImageColumns.SIZE+">1024";
            String[] args = !TextUtils.isEmpty(albumName) ? new String[] { albumName } : null;
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[] { MediaStore.Images.ImageColumns.DATA }, where, args, MediaStore.Images.ImageColumns.DATE_ADDED + " DESC"); // 最新的图片显示在最上面

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String path = cursor.getString(0);
                    if(FileUtil.isExists(path)){
                        images.add(path);
                    }else{
                    }
                }
                cursor.close();
            }
        }
        return images;
    }

    public static void saveToGallery(final Activity context, final String filePath) {
        new Thread() {
            public void run() {
                File srcFile = new File(filePath);

                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                if (dir != null && !dir.exists()) {
                    boolean r = dir.mkdir(); // 没有相册文件夹，就做一个
                }

                String fileName = "Neta_" + StringUtils.toServerTimeString(System.currentTimeMillis()) + "."
                        + FileUtil.getExtensionName(filePath);

                if (dir == null || !dir.exists()) {
                    // 系统相册默认文件夹获取失败，保存到本地文件夹下
                    dir = new File(StorageUtils.getGalleryDirPath());
                }

                final String target = FileUtil.getUnDuplicatedFileName(dir.getAbsolutePath(), fileName);

                boolean res;
                try {
                    FileUtil.copyFile(srcFile, new File(target));
                    // ImageRegisterService.register2Gallery(mActivity, new String[]{target});
                    res = true;
                    try {
                        MediaStore.Images.Media.insertImage(context.getContentResolver(), target, fileName, null);
                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(
                                target))));
                    } catch (FileNotFoundException e) {
                    }

                } catch (IOException e) {
                    res = false;
                }
                final boolean b = res;
                if (context != null && !context.isFinishing()) {
                    context.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (b) {
                                Toast.makeText(context,
                                        context.getString(R.string.msg_register_to_gallery_success, target),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, R.string.msg_register_to_gallery_fail, Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });
                }
            };
        }.start();

    }

    public static File getChapterFile(String bookId, int chapter) {
        File file = new File(StorageUtils.getNovRootPath() + bookId,chapter + ".txt");
        if(!file.exists()){
            createFile(file);
        }
        return file;
    }

    public static String createFile(File file){
        try {
            if (file.getParentFile().exists()) {
                file.createNewFile();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String createDir(String dirPath) {
        try {
            File file = new File(dirPath);
            if (file.getParentFile().exists()) {
                file.mkdir();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dirPath;
    }

    public static String getCharset(String fileName) {
        BufferedInputStream bis = null;
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            bis = new BufferedInputStream(new FileInputStream(fileName));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE
                    && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF
                    && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            bis.mark(0);
            if (!checked) {
                while ((read = bis.read()) != -1) {
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF)
                            // (0x80 - 0xBF),也可能在GB编码内
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return charset;
    }

    public static boolean unzip(String zipFile, String targetDir) {
        int BUFFER = 4096; //这里缓冲区我们使用4KB，
        String strEntry; //保存每个zip的条目名称
        try {
            BufferedOutputStream dest = null; //缓冲输出流
            FileInputStream fis = new FileInputStream(zipFile);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry; //每个zip条目的实例

            while ((entry = zis.getNextEntry()) != null) {
                try {
                    int count;
                    byte data[] = new byte[BUFFER];
                    strEntry = entry.getName();

                    File entryFile = new File(targetDir + strEntry);
                    File entryDir = new File(entryFile.getParent());
                    if (!entryDir.exists()) {
                        entryDir.mkdirs();
                    }

                    FileOutputStream fos = new FileOutputStream(entryFile);
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return false;
                }
            }
            zis.close();
            return true;
        } catch (Exception cwj) {
            cwj.printStackTrace();
            return false;
        }
    }

    public static ArrayList<String> getUnZipFileList(String strPath) {
        ArrayList<String> res = new ArrayList<>();
        File dir = new File(strPath);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (!files[i].isDirectory()) { // 判断是文件还是文件夹
                    String path = files[i].getAbsolutePath();
                    if(isImageFileBySuffix(path)){
                        res.add(files[i].getAbsolutePath());
                    }

                }
            }

        }
        return res;
    }
}
