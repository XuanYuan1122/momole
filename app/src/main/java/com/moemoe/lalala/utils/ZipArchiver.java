package com.moemoe.lalala.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import java.io.File;

/**
 * Created by yi on 2017/11/15.
 */

public class ZipArchiver  extends BaseArchiver {


    @Override
    public void doArchiver(File[] files, String destpath) {

    }

    @Override
    public void doUnArchiver(String srcfile, String unrarPath, String password,final IArchiverListener listener) {
        if (TextUtils.isEmpty(srcfile) || TextUtils.isEmpty(unrarPath))
            return;
        File src = new File(srcfile);
        if (!src.exists())
            return;
        try {
            ZipFile zFile = new ZipFile(srcfile);
            zFile.setFileNameCharset(FileUtil.getCharset(srcfile));
            if (!zFile.isValidZipFile()){
               // throw new ZipException("文件不合法!");
                if (listener != null){
                    listener.onFail("解压失败");
                }
            }
            File destDir = new File(unrarPath);
            if (destDir.isDirectory() && !destDir.exists()) {
                destDir.mkdir();
            }

            if (zFile.isEncrypted()) {
                zFile.setPassword(password.toCharArray());
            }
            if (listener != null){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onStartArchiver();
                    }
                });
            }
            FileHeader fh = null;
            final int total = zFile.getFileHeaders().size();
            for (int i = 0; i < zFile.getFileHeaders().size(); i++) {
                fh = (FileHeader) zFile.getFileHeaders().get(i);
                zFile.extractFile(fh,unrarPath);

                if (listener != null) {
                    final int finalI = i;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onProgressArchiver(finalI + 1, total);
                        }
                    });
                }
            }
            if (listener != null){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onComplete();
                    }
                });
            }
        } catch (ZipException e1) {
            if(listener != null){
                listener.onFail("解压失败");
            }
            e1.printStackTrace();
        }finally {
            if (listener != null){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onEndArchiver();
                    }
                });
            }
        }
    }
}
