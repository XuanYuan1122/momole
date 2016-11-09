package com.moemoe.lalala.network;

import android.text.TextUtils;

/**
 * Created by Haru on 2016/4/27 0027.
 */
public class OtakuResource{

    public static String getDocShareUrl(String url){
        if(!TextUtils.isEmpty(url)){
            return Otaku.URL_SHARE + url;
        }else{
            return null;
        }
    }
}
