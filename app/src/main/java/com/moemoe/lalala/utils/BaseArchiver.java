package com.moemoe.lalala.utils;

import android.os.Handler;
import android.os.Message;

import java.io.File;

/**
 *
 * Created by yi on 2017/11/15.
 */

public abstract class BaseArchiver {

    protected Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public abstract void doArchiver(File[] files, String destpath);

    public abstract void doUnArchiver(String srcfile, String unrarPath,String password,IArchiverListener listener);
}
