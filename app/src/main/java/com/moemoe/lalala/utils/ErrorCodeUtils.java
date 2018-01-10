package com.moemoe.lalala.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.moemoe.lalala.R;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.activity.NewFileCommonActivity;
import com.moemoe.lalala.view.activity.NewFileManHuaActivity;
import com.moemoe.lalala.view.activity.NewFileXiaoshuoActivity;
import com.moemoe.lalala.view.activity.ShopDetailActivity;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;

/**
 * 错误码解析
 * Created by yi on 2016/11/28.
 */

public class ErrorCodeUtils {

    public static final boolean showErrorMsgByCode(final Context context, int errorCode, String msg) {
        boolean resb = false;
        if(context != null){
            String res;
            if(errorCode < 0){
                if(errorCode == -1) return true;
                res = getErrorMsgByCode(context, errorCode);
            }else {
                res = msg;
            }
            if (errorCode == 4002){
                AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                alertDialogUtil.dismissDialog();
                final AlertDialogUtil alertDialogUti2 = AlertDialogUtil.getInstance();
                alertDialogUti2.createNormalDialog(context,"你的节操不够了~\n" +
                        "加入VIP会员可获得100节操！\n" +
                        "以及更多福利，还等什么？！");
                alertDialogUti2.setButtonText("查看详情","下次再说",0);
                alertDialogUti2.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                    @Override
                    public void CancelOnClick() {
                        alertDialogUti2.dismissDialog();
                        if(context instanceof NewFileCommonActivity || context instanceof NewFileManHuaActivity || context instanceof NewFileXiaoshuoActivity){
                            ((BaseAppCompatActivity)context).finish();
                        }
                    }

                    @Override
                    public void ConfirmOnClick() {
                        alertDialogUti2.dismissDialog();
                        Intent i = new Intent(context, ShopDetailActivity.class);
                        i.putExtra("uuid", "b3b952d1-7f31-4014-8048-2e207bcfe53c");
                        context.startActivity(i);
                        if(context instanceof NewFileCommonActivity || context instanceof NewFileManHuaActivity || context instanceof NewFileXiaoshuoActivity){
                            ((BaseAppCompatActivity)context).finish();
                        }
                    }
                });
                alertDialogUti2.showDialog();
            }else {
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
