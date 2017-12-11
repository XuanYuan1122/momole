package com.moemoe.lalala.utils;

import android.text.TextUtils;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by yi on 2017/11/15.
 */

public class ArchiverManager extends BaseArchiver{

    private volatile static ArchiverManager mInstance;

    private BaseArchiver mCurrentArchiver;

    private Executor mThreadPool;

    public static ArchiverManager getInstance(){
        if (mInstance == null) {
            synchronized (ArchiverManager.class) {
                mInstance = new ArchiverManager();
            }
        }
        return mInstance;
    }

    private ArchiverManager() {
        mThreadPool= Executors.newSingleThreadExecutor();
    }

    @Override
    public void doArchiver(File[] files, String destpath) {

    }

    @Override
    public void doUnArchiver(final String srcfile, final String unrarPath, final String password, final IArchiverListener listener) {
        mCurrentArchiver = getCorrectArchiver(getFileType(srcfile));
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                mCurrentArchiver.doUnArchiver(srcfile,unrarPath,password,listener);
            }
        });
    }

    /**
     * 获取文件类型
     * @param filename
     * @return
     */
    private String getFileType(String filename) {
        String type=null;
        if (TextUtils.isEmpty(filename))
            return type;
        String[] temp = filename.split("\\.");
        type = temp[temp.length-1];
        return type;
    }

    private BaseArchiver getCorrectArchiver(String type) {
        switch (type) {
//            case ArchiverType._7Z:
//                //return new SevenZipArchiver();
            case ArchiverType._ZIP:
                return new ZipArchiver();
            case ArchiverType._RAR:
                return new RarArchiver();
            default:
                return new ZipArchiver();
        }
    }

    public static class ArchiverType{
        public final static String _RAR="rar";
        public final static String _ZIP="zip";
     //   public final static String _7Z="7z";
    }
}
