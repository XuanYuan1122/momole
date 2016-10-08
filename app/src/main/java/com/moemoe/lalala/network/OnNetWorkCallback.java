package com.moemoe.lalala.network;

/**
 * Created by Haru on 2016/9/12.
 */
public interface OnNetWorkCallback<S,T> {
    void success(S token,T t);
    void failure(String e);
}
