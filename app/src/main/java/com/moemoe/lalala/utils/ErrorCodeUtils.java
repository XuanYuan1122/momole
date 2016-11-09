package com.moemoe.lalala.utils;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created by yi on 2016/11/9.
 */

public class ErrorCodeUtils {

    public static final boolean showErrorMsgByCode(Context context, int errorCode) {
        boolean resb = false;
        if(handleLoginError(context, errorCode)){
            resb = true;
        }else{
            String res = getErrorMsgByCode(context, errorCode);
            if (!TextUtils.isEmpty(res)) {
                ToastUtil.showToast(context, res);
                resb = true;
            }
        }
        return resb;
    }

    private static boolean handleLoginError(Context context, int errorCode){
        boolean res = false;
        if (context != null) {
//            if () { //登陆错误
//                ToastUtil.showToast(context, R.string.a_server_msg_try_again);
//                ((BaseActivity)context).tryLoginFirst(null);
//                res = true;
//            }
        }
        return res;
    }

    private static final String getErrorMsgByCode(Context context, int errorCode) {
        String res = "";
        return res;
    }
}
