package com.moemoe.lalala.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;

import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.view.activity.DownLoadListActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * Created by Haru on 2016/5/1 0001.
 */
public class BitmapUtils {

    public static Bitmap.Config DEFAULT_BITMAP_CONFIG;
    /**
     * 允许长图，宽度压缩
     */
    public static final int MAX_UPLOAD_FILE_WIDTH = 1600;
    /**
     * 默认图片质量
     */
    public static final int IMAGE_QUALITY = 80;

    public static int[] getImageSize(String imagePath) {
        int[] res = new int[2];

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;
        BitmapFactory.decodeFile(imagePath, options);

        res[0] = options.outWidth;
        res[1] = options.outHeight;

        return res;
    }


    public static Bitmap takeScreenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取屏幕长和高
        int width = activity.getResources().getDisplayMetrics().widthPixels;
        int height = activity.getResources().getDisplayMetrics().heightPixels;

        Bitmap bmp = Bitmap.createBitmap(b1, 0, 0, width, height);
        view.destroyDrawingCache();
        return bmp;
    }

    public static Bitmap loadThumb(String path, int width, int height) {
        return loadThumb(path, width, height, DEFAULT_BITMAP_CONFIG, true);
    }

    public static Bitmap loadThumb(String path, int width, int height, Bitmap.Config config, boolean isWHMaxLimit) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap b = null;
        options.inSampleSize = 1;
        options.inJustDecodeBounds = true;

        try {
            if (width <= 0 || height <= 0) {
                b = BitmapFactory.decodeFile(path);
                return b;
            } else {
                // 解析图片宽高至option中
                BitmapFactory.decodeFile(path, options);
                if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
                    return null;
                }

                int orientation = getOrientation(path);
                int rotation = getRotation(orientation);

                float sx, sy;
                float outW, outH;
                if (rotation == 90 || rotation == 270) {
                    outW = options.outHeight;
                    outH = options.outWidth;
                } else {
                    outH = options.outHeight;
                    outW = options.outWidth;
                }
                sx = width / outW;
                sy = height / outH;
                if (isWHMaxLimit && sx > sy) {
                    sx = sy;
                } else if (!isWHMaxLimit && sx < sy){
                    sx = sy;
                }
                // if(sx<sy)sx = sy;
                int ss = (int) (1 / sx);
                float w = sx * outW;
                float h = sx * outH;
                // LOGE(TAG, "out put bitmap size: "+w+"\t"+h );

                options.inSampleSize = ss;
                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inPreferredConfig = config;
                b = BitmapFactory.decodeFile(path, options);
                if (b == null)
                    return null;
                if (orientation != 1) {
                    Matrix m = new Matrix();
                    m.postRotate(rotation);
                    Bitmap newBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, false);
                    if (newBitmap != null && !newBitmap.equals(b)) {// 前后两张图片不一样，则回收前面一张图，并把新图片资源指向前面图的变量；
                        b.recycle();
                        b = newBitmap;
                    }
                }
                Bitmap newBitmap = Bitmap.createScaledBitmap(b, (int) w, (int) h, false);
                if (newBitmap != null && !newBitmap.equals(b)) {// 前后两张图片不一样，则回收前面一张图，并把新图片资源指向前面图的变量；
                    b.recycle();
                    b = newBitmap;
                }
            }
        } catch (OutOfMemoryError ex) {
            System.gc();
        } catch (Exception e) {
        }
        return b;
    }

    public static int[] getDocIconSizeFromW(int width, int height, int widthLimit){
        int[] res = new int[2];

        if(width > widthLimit){
            res[0] = widthLimit;
            res[1] = height * widthLimit / width;
        }else{
            res[0] = width;
            res[1] = height;
        }
        return res;
    }

    public static int[] getDocIconSizeFromH(int width, int height, int heightLimit){
        int[] res = new int[2];

        if(height > heightLimit){
            res[1] = heightLimit;
            res[0] = width * heightLimit / height;
        }else {
            res[0] = width;
            res[1] = height;
        }
        return res;
    }

    public static int[] getDocIconSize(double width, double height, int widthLimit){
        int[] res = new int[2];
        int scale = 2;
        // width = width * scale;
        // height = height * scale;

        if(width > widthLimit){
            res[0] = widthLimit;
            res[1] = (int) (height * widthLimit / width);
        }else{
            res[0] = (int) width;
            res[1] = (int) height;
        }
        return res;
    }

//    public static int[] fitIcon(int width,int height){
//        int[] res = new int[2];
//        res[0] = DensityUtil.getScreenWidth();
//        res[1] = height + DensityUtil.getScreenWidth() / height;
//        return res;
//    }


    public static int getOrientation(String file) {
        int orientation = 1;
        if (!TextUtils.isEmpty(file)) {
            try {
                ExifInterface exif = new ExifInterface(file);
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            } catch (NoClassDefFoundError e) {
            } catch (IOException e) {
            } catch (ExceptionInInitializerError e) {
            }
        }
        return orientation;
    }

    public static int getRotation(int orientation) {
        switch (orientation) {
            case 1:
                return 0;
            case 8:
                return 270;
            case 3:
                return 180;
            case 6:
                return 90;
            default:
                return 0;
        }
    }
    /**
     * 压缩图片，上传给服务器用
     * @param bitmap
     * @return
     */
    public static Bitmap getServerSizeBitmap(Bitmap bitmap){
        Bitmap res = bitmap;
        if(bitmap != null){
            if(bitmap.getWidth() > MAX_UPLOAD_FILE_WIDTH){
                float scale = (float)MAX_UPLOAD_FILE_WIDTH / bitmap.getWidth();
                res = Bitmap.createScaledBitmap(bitmap, MAX_UPLOAD_FILE_WIDTH, (int)(bitmap.getHeight() * scale), false);
                bitmap.recycle();
            }
        }
        return res;
    }
    /**
     * 压缩图片，上传给服务器用
     * @param path
     * @return
     */
    public static Bitmap getServerSizeBitmap(Context context, String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        int orientation = getOrientation(path);
        int rotation = getRotation(orientation);
        if (orientation != 1) {
            Matrix m = new Matrix();
            m.postRotate(rotation);
            Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, false);
            if (newBitmap != null && !newBitmap.equals(bitmap)) {// 前后两张图片不一样，则回收前面一张图，并把新图片资源指向前面图的变量；
                bitmap.recycle();
                bitmap = newBitmap;
            }
        }
        if(NetworkUtils.isWifi(context)) {
            return bitmap;
        } else {
            return getServerSizeBitmap(bitmap);
        }
    }


    public static boolean saveAsJpg(Bitmap bitmap, String path){
        boolean res = false;
        try {
            FileOutputStream out = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, out);
            out.close();
            res = true;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return res;
    }


    /**
     * 获取保存图片的目录
     *
     * @return
     */
    public static File getAlbumDir() {
        File dir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                getAlbumName());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 获取保存 隐患检查的图片文件夹名称
     *
     * @return
     */
    public static String getAlbumName() {
        return "neta";
    }

    /**
     * 添加到图库
     */
    public static void galleryAddPic(final Context context, String path) {
        File f = new File(path);
        addToMediaStore(context,f);
//        try {
//            MediaStore.Images.Media.insertImage(context.getContentResolver(), f.getAbsolutePath(), f.getName(), "图片: " + f.getName());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        MediaScannerConnection.scanFile(context,
//                new String[]{f.getParentFile().getAbsolutePath()},
//                new String[]{"image/*"},
//                new MediaScannerConnection.OnScanCompletedListener() {
//                    @Override
//                    public void onScanCompleted(String path, Uri uri) {
//                        ToastUtils.showShortToast(context,"完成:" + path);
//                    }
//                });
//
//
//        Intent mediaScanIntent = new Intent(
//                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//        context.sendBroadcast(mediaScanIntent);
    }

    private static void addToMediaStore(Context context,File f) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, f.getAbsolutePath());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    /**
     * 根据路径删除图片
     *
     * @param path
     */
    public static void deleteTempFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 根据路径获得突破并压缩返回bitmap用于显示
     *
     * @param
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 1080, 1920);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static ArrayList<Object> handleUploadItem(ArrayList<Object> paths){
        ArrayList<Object> res = new ArrayList<>();
        for(int i = 0; i < paths.size(); i++){
            Object o = paths.get(i);
            if(o instanceof String){
                if (FileUtil.isGif((String) o)) {
                    Image fb = new Image();
                    fb.setPath((String) o);
                    res.add(fb);
                }else {
                    String fileNameTemp = new File((String) o).getName();
                    String fileName = fileNameTemp.replace("." + FileUtil.getExtensionName(fileNameTemp),"");
                    File f = StorageUtils.getTempFile(fileName + ".jpg");
                    if(f != null && !f.exists()){
                        Bitmap bm = BitmapUtils.getSmallBitmap((String) o);
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(f);
                            bm.compress(Bitmap.CompressFormat.JPEG, 40, fos);
                            fos.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Image fb = new Image();
                    fb.setPath(f.getAbsolutePath());
                    res.add(fb);
                }
            }else {
                res.add(o);
            }
        }
        return res;
    }

    public static ArrayList<Image> handleUploadImage(ArrayList<String> paths){
        ArrayList<Image> fbs = new ArrayList<>();
        for (String s : paths){
            Image fb = new Image();
            fb.setPath(s);
            fbs.add(fb);
        }
        return fbs;
    }

    public static Bitmap loadBitmapFromAssets(Context context,String filePath){
        InputStream inputStream = null;
        try {
            inputStream = context.getResources().getAssets().open(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(inputStream==null) return null;
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inScaled=false;
        Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
        return bitmap;
    }

    /**
     * 翻转
     *
     * @param a
     * @return
     */
    public static Bitmap convert(Bitmap a,int index) {
        int w = a.getWidth();
        int h = a.getHeight();

        Bitmap newb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        Matrix m = new Matrix();
        //
        if(index==0){
            m.postScale(-1, 1); // 镜像水平翻转
        }else{
            m.postScale(1, -1); // 镜像垂直翻转
        }
        // m.postRotate(-90); // 旋转-90度
        Bitmap new2 = Bitmap.createBitmap(a, 0, 0, w, h, m, true);
        cv.drawBitmap(new2, new Rect(0, 0, new2.getWidth(), new2.getHeight()),
                new Rect(0, 0, w, h), null);
        return newb;
    }

    /**
     * 旋转
     *
     * @param angle
     * @param bitmap
     * @return
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    public static Bitmap decodeSampledBitmapFromResource(
            String path, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

//    public static ArrayList<Image> handleUploadImage(ArrayList<String> paths){
//
//
// ArrayList<Image> fbs = new ArrayList<>();
//        for(int i = 0; i < paths.size(); i++){
//            if (FileUtil.isGif(paths.get(i))) {
//                Image fb = new Image();
//                fb.setPath(paths.get(i));
//                fbs.add(fb);
//            }else {
//                File f = StorageUtils.getTempFile(System.currentTimeMillis() + ".jpg");
//                if(f != null && !f.exists()){
//                    Bitmap bm = BitmapUtils.getSmallBitmap(paths.get(i));
//                    FileOutputStream fos = null;
//                    try {
//                        fos = new FileOutputStream(f);
//                        bm.compress(Bitmap.CompressFormat.JPEG, 40, fos);
//                        fos.close();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                Image fb = new Image();
//                fb.setPath(f.getAbsolutePath());
//                fbs.add(fb);
//            }
//        }
//        return fbs;
//    }
}
