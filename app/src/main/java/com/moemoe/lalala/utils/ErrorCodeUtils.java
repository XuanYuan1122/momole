package com.moemoe.lalala.utils;

import android.content.Context;
import android.text.TextUtils;

import com.moemoe.lalala.R;
import com.moemoe.lalala.greendao.gen.ChatContentDbEntityDao;
import com.moemoe.lalala.greendao.gen.ChatUserEntityDao;
import com.moemoe.lalala.greendao.gen.GroupUserEntityDao;
import com.moemoe.lalala.greendao.gen.PrivateMessageItemEntityDao;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;

/**
 * Created by yi on 2016/11/28.
 */

public class ErrorCodeUtils {

    public static final boolean showErrorMsgByCode(Context context, int errorCode,String msg) {
        boolean resb = false;
        if(context != null){
            String res;
            if(errorCode < 0){
                res = getErrorMsgByCode(context, errorCode);
            }else {
                res = msg;
            }
            if (!TextUtils.isEmpty(res)) {
                ToastUtils.showShortToast(context, res);
                if(errorCode == 4003 || errorCode == 4024){
                    ((BaseAppCompatActivity)context).finish();
                }
                if (errorCode == 999){
                    //清除数据库相关私信信息
                    //私信列表
                    try {
                        if(StringUtils.isThirdParty(PreferenceUtils.getAuthorInfo().getPlatform())){
                            Platform p = ShareSDK.getPlatform(PreferenceUtils.getAuthorInfo().getPlatform());
                            if(p.isAuthValid()){
                                p.removeAccount(true);
                            }
                        }
                        PrivateMessageItemEntityDao privateMessageItemEntityDao = GreenDaoManager.getInstance().getSession().getPrivateMessageItemEntityDao();
                        privateMessageItemEntityDao.deleteAll();
                        ChatContentDbEntityDao chatContentDbEntityDao = GreenDaoManager.getInstance().getSession().getChatContentDbEntityDao();
                        chatContentDbEntityDao.deleteAll();
                        GroupUserEntityDao groupUserEntityDao = GreenDaoManager.getInstance().getSession().getGroupUserEntityDao();
                        groupUserEntityDao.deleteAll();
                        ChatUserEntityDao chatUserEntityDao = GreenDaoManager.getInstance().getSession().getChatUserEntityDao();
                        chatUserEntityDao.deleteAll();
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        PreferenceUtils.clearAuthorInfo();
                        DialogUtils.checkLoginAndShowDlg(context);
                    }
                }
                resb = true;
            }
        }
        return resb;
    }

    private static final String getErrorMsgByCode(Context context, int errorCode) {
        switch (errorCode){
            case 3001:
                return context.getString(R.string.msg_server_illegal_pwd);
            case 3002:
                return context.getString(R.string.msg_server_login_account_frozen);
            case 3003:
                return context.getString(R.string.msg_server_registe_phone_repeat);
            case 3004:
                return context.getString(R.string.msg_server_register_vcode_error);
            case 3005:
                return context.getString(R.string.msg_server_no_account);
            case 3006:
                return context.getString(R.string.msg_vcode_illegal);
            case 3007:
                return context.getString(R.string.msg_change_pwd_ori_pwd_wrong);
            case 4001:
                return context.getString(R.string.msg_trash_send);
            case 4002:
                return context.getString(R.string.msg_have_no_coin);
            case 4003:
                return context.getString(R.string.msg_doc_has_deleted);
            case 4004:
                return context.getString(R.string.msg_can_not_give_coin_to_self);
            case 4005:
                return context.getString(R.string.msg_only_delete_self_doc);
            case 4006:
                return context.getString(R.string.msg_only_delete_self_comment);
            case 4007:
                return context.getString(R.string.msg_tag_not_exit);
            case 4008:
                return context.getString(R.string.msg_clicked);
            case 4009:
                return context.getString(R.string.msg_coin_got);
            case 4010:
                return context.getString(R.string.label_tag_not_exit);
            case 4011:
                return context.getString(R.string.label_tag_exit);
            case 4012:
                return context.getString(R.string.label_trash_not_exit);
            case 999:
                return context.getString(R.string.msg_need_login_first);
            case -2:
                return context.getString(R.string.msg_user_cancel);
            case -3:
                return "未安装三方平台或未知错误";
            default:
                return context.getString(R.string.msg_refresh_fail);
        }
    }
}
