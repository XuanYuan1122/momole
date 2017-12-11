package com.moemoe.lalala.utils;

import java.io.File;
import java.io.FileOutputStream;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.rarfile.FileHeader;

/**
 * Created by yi on 2017/11/15.
 */

public class RarArchiver  extends BaseArchiver{
    @Override
    public void doArchiver(File[] files, String destpath) {

    }

    @Override
    public void doUnArchiver(String srcfile, String unrarPath, String password, final IArchiverListener listener) {
        File srcFile = new File(srcfile);
        if (null == unrarPath || "".equals(unrarPath)) {
            unrarPath = srcFile.getParentFile().getPath();
        }
        // 保证文件夹路径最后是"/"或者"\"
        char lastChar = unrarPath.charAt(unrarPath.length() - 1);
        if (lastChar != '/' && lastChar != '\\') {
            unrarPath += File.separator;
        }
        if (listener != null)
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onStartArchiver();
                }
            });

        FileOutputStream fileOut = null;
        Archive rarfile = null;

        try {
            rarfile = new Archive(srcFile,password,false);
            FileHeader fh = null;
            final int total = rarfile.getFileHeaders().size();
            for (int i = 0; i < rarfile.getFileHeaders().size(); i++) {
                fh = rarfile.getFileHeaders().get(i);
                String entrypath = "";
                if (fh.isUnicode()) {//解決中文乱码
                    entrypath = fh.getFileNameW().trim();
                } else {
                    entrypath = fh.getFileNameString().trim();
                }
                entrypath = entrypath.replaceAll("\\\\", "/");

                File file = new File(unrarPath + entrypath);
                if (fh.isDirectory()) {
                    file.mkdirs();
                } else {
                    File parent = file.getParentFile();
                    if (parent != null && !parent.exists()) {
                        parent.mkdirs();
                    }
                    fileOut = new FileOutputStream(file);
                    rarfile.extractFile(fh, fileOut);
                    fileOut.close();
                }
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
            rarfile.close();
            if(listener != null){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onComplete();
                    }
                });
            }
        } catch (Exception e) {
            if (listener != null){
                listener.onFail("解压失败");
            }
            e.printStackTrace();
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.close();
                    fileOut = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (rarfile != null) {
                try {
                    rarfile.close();
                    rarfile = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (listener != null)
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onEndArchiver();
                    }
                });
        }
    }
}
