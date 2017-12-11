package com.moemoe.lalala.utils;

/**
 * Created by yi on 2017/11/15.
 */

public interface IArchiverListener {
    void onStartArchiver();

    void onProgressArchiver(int current,int total);

    void onComplete();

    void onEndArchiver();

    void onFail(String msg);
}
