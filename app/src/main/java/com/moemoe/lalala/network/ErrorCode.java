package com.moemoe.lalala.network;

import android.content.Context;
import android.text.TextUtils;

import com.moemoe.lalala.BaseActivity;
import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.ToastUtil;

/**
 * Created by Haru on 2016/4/29 0029.
 */
public class ErrorCode {

    /**
     * 验证码发送失败
     */
    public static final String ERR_MOBILE_MESSAGE_SEND = "ERR_MOBILE_MESSAGE_SEND";
    /**
     * 连接错误
     */
    public static final String ERR_ID_CONNECTION = "ERR_ID_CONNECTION";
    /**
     * 服务器错误
     */
    public static final String ERR_ID_SERVER = "ERR_ID_SERVER";
    /**
     * 本地请求错误
     */
    public static final String ERR_ID_LOCAL = "ERR_ID_LOCAL";
    /**
     * 总之就是错了
     */
    public static final String ERR_ID_UNKNOWN = "ERR_ID_UNKNOWN";
    /**
     * 本地待上传图片错误
     */
    public static final String ERR_ID_ILLEGAL_FILE = "ERR_ID_ILLEGAL_FILE";
    /**
     * 图片上传到七牛服务器错误
     */
    public static final String ERR_ID_QINIU_UPLOAD = "ERR_ID_QINIU_UPLOAD";



    public static final String ERR_ID_REG_USER_MOBILE_REPEAT = "ERR_ID_REG_USER_MOBILE_REPEAT";


    public static final String ERR_COMMAND_CANNOT_ADD_NICE_TO_SELF = "ERR_COMMAND_CANNOT_ADD_NICE_TO_SELF";

    //----------

    // -------- ID 服务错误代码汇总 ----------
    public static final String ERR_ID_BAD_FORM = "ERR_ID_BAD_FORM";
    public static final String ERR_ID_BASE64_URL_DECODE = "ERR_ID_BASE64_URL_DECODE";
    public static final String ERR_ID_RSA_DECODE_ERR = "ERR_ID_RSA_DECODE_ERR";
    public static final String ERR_ID_GZIP_DECODE_ERR = "ERR_ID_GZIP_DECODE_ERR";
    public static final String ERR_ID_TOKEN_BAD = "ERR_ID_TOKEN_BAD";
    public static final String ERR_ID_TOKEN_OVER_TIME = "ERR_ID_TOKEN_OVER_TIME";
    public static final String ERR_ID_TOKEN_BUILD_BAD = "ERR_ID_TOKEN_BUILD_BAD";
    public static final String ERR_ID_BAD_REQUEST_DATA = "ERR_ID_BAD_REQUEST_DATA";
    public static final String ERR_ID_TOKEN_REMOVE = "ERR_ID_TOKEN_REMOVE";
    public static final String ERR_ID_LOGIN_MISS_TYPE = "ERR_ID_LOGIN_MISS_TYPE";
    public static final String ERR_ID_LOGIN_MISS_MOBILE = "ERR_ID_LOGIN_MISS_MOBILE";
    public static final String ERR_ID_LOGIN_MISS_PASSWORD = "ERR_ID_LOGIN_MISS_PASSWORD";
    public static final String ERR_ID_LOGIN_BAD_ACCOUNT = "ERR_ID_LOGIN_BAD_ACCOUNT";
    public static final String ERR_ID_LOGIN_BAD_PASSWORD = "ERR_ID_LOGIN_BAD_PASSWORD";
    public static final String ERR_ID_LOGIN_NO_ACTIVE = "ERR_ID_LOGIN_NO_ACTIVE";
    public static final String ERR_ID_LOGIN_IN_FROZEN = "ERR_ID_LOGIN_IN_FROZEN";
    public static final String ERR_ID_PASSWORD_CHANGE_BAD = "ERR_ID_PASSWORD_CHANGE_BAD";

    /**
     * 没有查询到内容，例如帖子被删
     */
    public static final String ERR_QUERY_NOT_FOUND = "ERR_QUERY_NOT_FOUND";
    public static final String ERR_QUERY_BAD_FORM = "ERR_QUERY_BAD_FORM";
    public static final String ERR_QUERY_MISS_TOKEN = "ERR_QUERY_MISS_TOKEN";
    public static final String ERR_QUERY_BAD_TOKEN = "ERR_QUERY_BAD_TOKEN";
    public static final String ERR_QUERY_NO_QUERY = "ERR_QUERY_NO_QUERY";
    public static final String ERR_QUERY_ARGS_BAD = "ERR_QUERY_ARGS_BAD";
    public static final String ERR_QUERY_USER_NOT_FOUND = "ERR_QUERY_USER_NOT_FOUND";

    public static final String ERR_DOMAIN_USER_NICE_SUM = "ERR_DOMAIN_USER_NICE_SUM";
    public static final String ERR_DOMAIN_USER_NOT_FOUND = "ERR_DOMAIN_USER_NOT_FOUND";
    public static final String ERR_DOMAIN_IMAGE_ID_MISS = "ERR_DOMAIN_IMAGE_ID_MISS";
    public static final String ERR_DOMAIN_IMAGE_NAME_MISS = "ERR_DOMAIN_IMAGE_NAME_MISS";
    public static final String ERR_DOMAIN_IMAGE_NOT_FOUND = "ERR_DOMAIN_IMAGE_NOT_FOUND";
    public static final String ERR_DOMAIN_CLUB_ADD_USER_HAS_CLUB = "ERR_DOMAIN_CLUB_ADD_USER_HAS_CLUB";
    public static final String ERR_DOMAIN_CLUB_ADD_NAME_REPEAT = "ERR_DOMAIN_CLUB_ADD_NAME_REPEAT";
    public static final String ERR_DOMAIN_CLUB_ADD_NAME_EMPTY = "ERR_DOMAIN_CLUB_ADD_NAME_EMPTY";
    public static final String ERR_DOMAIN_CLUB_NOT_FOUND = "ERR_DOMAIN_CLUB_NOT_FOUND";
    public static final String ERR_DOMAIN_CLUB_LOAD_ID_MISS = "ERR_DOMAIN_CLUB_LOAD_ID_MISS";
    public static final String ERR_DOMAIN_CLUB_UPDATE_NAME_REPEAT = "ERR_DOMAIN_CLUB_UPDATE_NAME_REPEAT";
    public static final String ERR_DOMAIN_CLUB_UPDATE_NAME_EMPTY = "ERR_DOMAIN_CLUB_UPDATE_NAME_EMPTY";
    public static final String ERR_DOMAIN_CLUB_MEMBER_REPEAT = "ERR_DOMAIN_CLUB_MEMBER_REPEAT";
    public static final String ERR_DOMAIN_CLUB_MEMBER_ADD = "ERR_DOMAIN_CLUB_MEMBER_ADD";
    public static final String ERR_DOMAIN_CLUB_MEMBER_DEL = "ERR_DOMAIN_CLUB_MEMBER_DEL";
    public static final String ERR_DOMAIN_CLUB_MARK_REPEAT = "ERR_DOMAIN_CLUB_MARK_REPEAT";
    public static final String ERR_DOMAIN_CLUB_TAG_ADD_TAG_EMPTY = "ERR_DOMAIN_CLUB_TAG_ADD_TAG_EMPTY";
    /**
     * 置顶帖子达到最大值，无法继续置顶
     */
    public static final String ERR_DOMAIN_CLUB_DOC_TOP_OVER_SIZE = "ERR_DOMAIN_CLUB_DOC_TOP_OVER_SIZE";
    public static final String ERR_DOMAIN_CLUB_DOC_NOT_FOUND = "ERR_DOMAIN_CLUB_DOC_NOT_FOUND";
    public static final String ERR_DOMAIN_CLUB_DOC_ADD = "ERR_DOMAIN_CLUB_DOC_ADD";
    public static final String ERR_DOMAIN_CLUB_DOC_ADD_IMG = "ERR_DOMAIN_CLUB_DOC_ADD_IMG";
    public static final String ERR_DOMAIN_CLUB_DOC_FREEZE = "ERR_DOMAIN_CLUB_DOC_FREEZE";
    public static final String ERR_DOMAIN_CLUB_DOC_DEFREEZE = "ERR_DOMAIN_CLUB_DOC_DEFREEZE";
    public static final String ERR_DOMAIN_CLUB_DOC_NICE = "ERR_DOMAIN_CLUB_DOC_NICE";
    public static final String ERR_DOMAIN_CLUB_DOC_COMMENT_CONTENT_EMPTY = "ERR_DOMAIN_CLUB_DOC_COMMENT_CONTENT_EMPTY";
    public static final String ERR_DOMAIN_CLUB_DOC_COMMENT_CONTENT_OVER_SIZE = "ERR_DOMAIN_CLUB_DOC_COMMENT_CONTENT_OVER_SIZE";
    public static final String ERR_DOMAIN_CLUB_DOC_COMMENT_ADD = "ERR_DOMAIN_CLUB_DOC_COMMENT_ADD";
    public static final String ERR_DOMAIN_CLUB_DOC_COMMENT_DELETE = "ERR_DOMAIN_CLUB_DOC_COMMENT_DELETE";
    public static final String ERR_DOMAIN_CLUB_DOC_COMMENT_LOAD = "ERR_DOMAIN_CLUB_DOC_COMMENT_LOAD";
    public static final String ERR_DOMAIN_CLUB_DOC_NICE_EMPTY_NICE = "ERR_DOMAIN_CLUB_DOC_NICE_EMPTY_NICE";
    public static final String ERR_DOMAIN_USER_NICE_MINUS = "ERR_DOMAIN_USER_NICE_MINUS";
    public static final String ERR_DOMAIN_USER_NICE_ADD = "ERR_DOMAIN_USER_NICE_ADD";
    public static final String ERR_DOMAIN_USER_EMAIL_UPDATE = "ERR_DOMAIN_USER_EMAIL_UPDATE";
    public static final String ERR_DOMAIN_USER_ICON_UPDATE = "ERR_DOMAIN_USER_ICON_UPDATE";
    public static final String ERR_DOMAIN_USER_NICKNAME_UPDATE = "ERR_DOMAIN_USER_NICKNAME_UPDATE";
    public static final String ERR_DOMAIN_TIME_TABLE_LOAD = "ERR_DOMAIN_TIME_TABLE_LOAD";
    public static final String ERR_DOMAIN_TIME_TABLE_MARK = "ERR_DOMAIN_TIME_TABLE_MARK";
    public static final String ERR_DOMAIN_TIME_TABLE_MARK_CANCEL = "ERR_DOMAIN_TIME_TABLE_MARK_CANCEL";
    public static final String ERR_COMMAND_BAD_FORM_BAD_WORD = "ERR_COMMAND_BAD_FORM_BAD_WORD";

    public static final String ERR_COMMAND_BAD_FORM = "ERR_COMMAND_BAD_FORM";
    public static final String ERR_COMMAND_NOT_FOUND = "ERR_COMMAND_NOT_FOUND";
    public static final String ERR_COMMAND_MISS_TOKEN = "ERR_COMMAND_MISS_TOKEN";
    public static final String ERR_COMMAND_BAD_TOKEN = "ERR_COMMAND_BAD_TOKEN";
    public static final String ERR_COMMAND_NO_COMMAND = "ERR_COMMAND_NO_COMMAND";

    public static final String ERR_DB_TX_BEGIN_ERR = "ERR_DB_TX_BEGIN_ERR";
    public static final String ERR_DB_TX_CLOSE_ERR = "ERR_DB_TX_CLOSE_ERR";
    public static final String ERR_DB_TX_COMMIT_ERR = "ERR_DB_TX_COMMIT_ERR";
    public static final String ERR_DB_TX_ROLLBACK_ERR = "ERR_DB_TX_ROLLBACK_ERR";
    public static final String ERR_DB_TX_QUERY_ERR = "ERR_DB_TX_QUERY_ERR";
    public static final String ERR_DB_TX_UPDATE_ERR = "ERR_DB_TX_UPDATE_ERR";
    public static final String ERR_DB_TX_INSERT_ERR = "ERR_DB_TX_INSERT_ERR";
    public static final String ERR_DB_TX_DELETE_ERR = "ERR_DB_TX_DELETE_ERR";

    public static final String ERR_FS_CREATE_FILE = "ERR_FS_CREATE_FILE";
    public static final String ERR_FS_WRITE_FILE = "ERR_FS_WRITE_FILE";



    public static final boolean showErrorMsgByCode(Context context, String errorCode) {
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

    public static boolean handleLoginError(Context context, String errorCode){
        boolean res = false;
        if (context != null) {
            if (ERR_ID_TOKEN_BAD.equals(errorCode) || ERR_ID_TOKEN_OVER_TIME.equals(errorCode)
                    || ERR_ID_TOKEN_BUILD_BAD.equals(errorCode) || ERR_ID_TOKEN_REMOVE.equals(errorCode)
                    || ERR_QUERY_MISS_TOKEN.equals(errorCode) || ERR_COMMAND_MISS_TOKEN.equals(errorCode)
                    || ERR_COMMAND_BAD_TOKEN.equals(errorCode) || ERR_QUERY_BAD_TOKEN.equals(errorCode)) {
                ToastUtil.showToast(context, R.string.a_server_msg_try_again);
                // do relogin
               //AccountHelper.reLogin(context);
                ((BaseActivity)context).tryLoginFirst(null);
                res = true;
            }
        }
        return res;
    }

    public static final boolean handleCommandError(Context context, String errorCode){

        return false;
    }

    public static final String getErrorMsgByCode(Context context, String errorCode) {
//		String res = context.getString(R.string.a_server_msg_unknown);
        String res = "";
        if (context != null) {
            if (ERR_MOBILE_MESSAGE_SEND.equals(errorCode)) {
                res = context.getString(R.string.a_server_msg_register_vcode_error);
            } else if (ERR_ID_LOGIN_BAD_PASSWORD.equals(errorCode)) {
                res = context.getString(R.string.a_server_msg_illegal_pwd);
            } else if (ERR_ID_LOGIN_BAD_ACCOUNT.equals(errorCode)) {
                res = context.getString(R.string.a_server_msg_no_account);
            } else if (ERR_ID_CONNECTION.equals(errorCode)) {
                res = context.getString(R.string.a_server_msg_connection);
            } else if (ERR_ID_LOCAL.equals(errorCode)) {
                res = context.getString(R.string.a_server_msg_local);
            } else if (ERR_ID_SERVER.equals(errorCode)) {
                res = context.getString(R.string.a_server_msg_server);
            } else if (ERR_ID_UNKNOWN.equals(errorCode)) {
                res = context.getString(R.string.a_server_msg_unknown);
            } else if (ERR_ID_REG_USER_MOBILE_REPEAT.equals(errorCode)) {
                res = context.getString(R.string.a_server_msg_registe_phone_repeat);
            } else if (ERR_ID_LOGIN_IN_FROZEN.equals(errorCode)) {
                res = context.getString(R.string.a_server_msg_login_account_frozen);
            } else if (ERR_DOMAIN_USER_NICE_SUM.equals(errorCode)) {
                res = context.getString(R.string.a_server_msg_cant_nice);
            } else if (ERR_DOMAIN_IMAGE_ID_MISS.equals(errorCode)) {
                res = context.getString(R.string.a_server_msg_img_upload);
            } else if (ERR_DOMAIN_CLUB_ADD_USER_HAS_CLUB.equals(errorCode)) {
                res = context.getString(R.string.a_server_msg_only_one_club);
            } else if (ERR_DOMAIN_CLUB_ADD_NAME_REPEAT.equals(errorCode)) {
                res = context.getString(R.string.a_server_msg_name_repeat);
            } else if (ERR_DOMAIN_CLUB_ADD_NAME_EMPTY.equals(errorCode) || ERR_DOMAIN_CLUB_UPDATE_NAME_EMPTY.equals(errorCode)) {
                res = context.getString(R.string.a_server_msg_club_title_not_null);
            } else if (ERR_DOMAIN_CLUB_ADD_NAME_EMPTY.equals(errorCode)) {
                res = context.getString(R.string.a_server_msg_club_title_not_null);
            } else if (ERR_DOMAIN_CLUB_UPDATE_NAME_REPEAT.equals(errorCode)){
                res = context.getString(R.string.a_server_msg_club_title_repeat);
            } else if (ERR_DOMAIN_CLUB_NOT_FOUND.equals(errorCode)){
                res = context.getString(R.string.a_server_msg_club_not_exist);
            } else if (ERR_DOMAIN_CLUB_DOC_NOT_FOUND.equals(errorCode)) {
                res = context.getString(R.string.a_server_msg_doc_not_exist);
            } else if (ERR_DOMAIN_CLUB_DOC_NICE_EMPTY_NICE.equals(errorCode)) {
                res = context.getString(R.string.a_server_msg_nice_empty);
            } else if (ERR_ID_PASSWORD_CHANGE_BAD.equals(errorCode)){
                res = context.getString(R.string.a_msg_change_pwd_ori_pwd_wrong);
            }
        }

//		<string name="a_server_msg_img_upload">图片上传错误</string>
//		<string name="a_server_msg_only_one_club">暂时只能创建一个社团哦~！</string>
//		<string name="a_server_msg_name_repeat">这个社团名已存在，换个团名试试看吧！</string>
//		<string name="a_server_msg_club_title_not_null">社团名不能为空</string>
//		<string name="a_server_msg_club_not_exist">该社团不存在</string>
//		<string name="a_server_msg_club_title_repeat">这个社团名已存在，换个团名试试看吧！</string>
//		<string name="a_server_msg_doc_not_exist">这个帖子已不存在</string>
//		<string name="a_server_msg_nice_empty">你不能再捏了</string>
        return res;
    }

}
