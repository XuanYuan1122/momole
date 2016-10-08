package com.moemoe.lalala.thirdopen;

import com.moemoe.lalala.data.AuthorInfo;

/**
 * Created by Haru on 2016/4/27 0027.
 */
public abstract class ThirdSDK {
    public static final String QQ = "qq";
    public static final String WEIBO = "weibo";
    public static final String WECHAT = "wechat";

    public abstract void Login(final LoginListener listener) throws Throwable;

    public abstract void logout();

    public abstract void getUserInfo();

    public abstract boolean isLogin(String token);

    protected LoginListener mListener;

    public interface LoginListener{
        void onLoginFinish(AuthorInfo authorInfo);

        void onLoginFailed();
    }
}
