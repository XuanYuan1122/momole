package com.moemoe.lalala.thirdopen;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.app.common.Callback;
import com.app.http.app.InterceptRequestListener;
import com.app.http.request.UriRequest;
import com.moemoe.lalala.BaseActivity;
import com.moemoe.lalala.LoginActivity;
import com.moemoe.lalala.R;
import com.moemoe.lalala.callback.MoeMoeCallback;
import com.moemoe.lalala.data.AuthorInfo;
import com.moemoe.lalala.data.PersonBean;
import com.moemoe.lalala.network.CallbackFactory;
import com.moemoe.lalala.network.ErrorCode;
import com.moemoe.lalala.network.OnNetWorkCallback;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.PhoneUtil;
import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by Haru on 2016/4/27 0027.
 */
public class ThirdPartySDKManager{

    public static final int CLOUD_TYPE_NONE = 0;
    public static final int CLOUD_TYPE_QQ = 1;
    public static final int CLOUD_TYPE_WECHAT= 2;
    public static final int CLOUD_TYPE_WEIBO = 3;

    /**
     * 用户标示
     */
    public static final String USER_ID = "openid";

    /**
     * QQ
     */
    public static final String QQ = "com.tencent.mobileqq";

    /**
     * QQ空间
     */
    public static final String QZONE = "com.qzone";

    /**
     * 新浪微博
     */
    public static final String SINA = "com.sina.weibo";

    /**
     * 微信
     */
    public static final String WEIXIN = "com.tencent.mm";

    private static Context mContext = null;
    private static ThirdPartySDKManager mCloudMng = null;
    private AuthorInfo mThirdUser;

    private ThirdPartySDKManager(){

    }

    public static ThirdPartySDKManager getInstance(Context context){
        if(mCloudMng == null){
            mCloudMng = new ThirdPartySDKManager();
        }
        mContext = context;
        return mCloudMng;
    }

    public void init(){
        ShareSDK.initSDK(mContext);
    }

    public void login(final int apiType,final MoeMoeCallback callback){
        if (NetworkUtils.isNetworkAvailable(mContext)) {
            try{
                ((LoginActivity) mContext).createDialog(mContext.getResources().getString(R.string.msg_on_login));
            }catch (Throwable t){
                t.printStackTrace();
            }
            String platform = null;
            switch (apiType){
                case CLOUD_TYPE_QQ:
                    platform = cn.sharesdk.tencent.qq.QQ.NAME;
                    break;
                case CLOUD_TYPE_WEIBO:
                  platform = SinaWeibo.NAME;
                    break;
                case CLOUD_TYPE_WECHAT:
                    platform = Wechat.NAME;
                    break;
                default:
                    return;
            }
            if(platform != null){
                getUserInfo(platform, new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        PlatformDb db = platform.getDb();
                        final AuthorInfo authorInfo = new AuthorInfo();
                        authorInfo.setmUid(db.getUserId());
                        authorInfo.setmUserName(db.getUserName());
                        authorInfo.setmHeadPath(db.getUserIcon());
                        authorInfo.setmGender(db.getUserGender().equals("f") ? PersonBean.SEX_FEMALE : PersonBean.SEX_MALE);
                        authorInfo.setmDevId(PhoneUtil.getLocaldeviceId(mContext));
                        authorInfo.setmPlatform(platform.getName());
                        //登录自己的用户系统
                        Otaku.getAccountV2().loginThird(mContext,db.getUserId(), platform.getName()).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
                            @Override
                            public void success(String token, String s) {
                                authorInfo.setmToken(token);
                                PreferenceManager.getInstance(mContext).updateAccessTokenTimeToNow();
                                ((LoginActivity) mContext).finalizeDialog();
                                try {
                                    JSONObject json = new JSONObject(s);
                                    int ok = json.optInt("ok");
                                    if(ok == Otaku.SERVER_OK) {
                                        String flag = json.optString("new_user_flag");
                                        String id = json.optString("user_id");
                                        authorInfo.setmUUid(id);
                                        //是否应该上传信息，优化？还是跟随三方账户

                                        PreferenceManager.getInstance(mContext).saveThirdPartyLoginMsg(authorInfo);
                                        Otaku.getAccountV2().requestSelfData(token,mContext);
                                        ToastUtil.showToast(mContext, R.string.msg_login_success);
                                        callback.onSuccess();
                                    }else {
                                        ToastUtil.showToast(mContext, R.string.msg_login_error);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void failure(String e) {
                                ((LoginActivity) mContext).finalizeDialog();
                                ToastUtil.showToast(mContext, R.string.msg_login_error);
                                callback.onFailure();
                            }
                        }));

//                        Otaku.getAccount().loginThird(mContext, db.getUserId(), platform.getName(), new Callback.InterceptCallback<String>() {
//                            @Override
//                            public void beforeRequest(UriRequest request) throws Throwable {
//
//                            }
//
//                            @Override
//                            public void afterRequest(UriRequest request) throws Throwable {
//                                String token = request.getResponseHeader(Otaku.X_ACCESS_TOKEN);
//                                if(TextUtils.isEmpty(token)){
//                                    token = request.getResponseHeader(Otaku.X_ACCESS_TOKEN.toLowerCase());
//                                }
//                                authorInfo.setmToken(token);
//                                PreferenceManager.getInstance(mContext).updateAccessTokenTimeToNow();
//                            }
//
//                            @Override
//                            public void onSuccess(String result) {
//                                ((LoginActivity) mContext).finalizeDialog();
//                                try {
//                                    JSONObject json = new JSONObject(result);
//                                    int ok = json.optInt("ok");
//                                    if(ok == Otaku.SERVER_OK) {
//                                        String flag = json.optString("new_user_flag");
//                                        String id = json.optString("user_id");
//                                        authorInfo.setmUUid(id);
//                                        //是否应该上传信息，优化？还是跟随三方账户
//
//                                        PreferenceManager.getInstance(mContext).saveThirdPartyLoginMsg(authorInfo);
//                                        ToastUtil.showToast(mContext, R.string.msg_login_success);
//                                        callback.onSuccess();
//                                       // ((LoginActivity) mContext).finish();
//                                    }else {
//                                        ToastUtil.showToast(mContext, R.string.msg_login_error);
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            @Override
//                            public void onError(Throwable ex, boolean isOnCallback) {
//                                ((LoginActivity) mContext).finalizeDialog();
//                                ToastUtil.showToast(mContext, R.string.msg_login_error);
//                                callback.onFialure();
//                            }
//
//                            @Override
//                            public void onCancelled(CancelledException cex) {
//
//                            }
//
//                            @Override
//                            public void onFinished() {
//                                try{
//                                    ((LoginActivity) mContext).finalizeDialog();
//                                }catch (Throwable t){
//                                    t.printStackTrace();
//                                }
//                            }
//                        });
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                        try{
                            ((LoginActivity) mContext).finalizeDialog();
                        }catch (Throwable t){
                            t.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                        try{
                            ((LoginActivity) mContext).finalizeDialog();
                        }catch (Throwable t){
                            t.printStackTrace();
                        }
                    }
                });
            }
        }else {
            ToastUtil.showCenterToast(mContext, R.string.msg_server_connection);
        }
    }

    public boolean isThirdParty(String platform){
        if(platform == null){
            return false;
        }
        return platform.equals(cn.sharesdk.tencent.qq.QQ.NAME) || platform.equals(Wechat.NAME) || platform.equals(SinaWeibo.NAME);
    }

    private void shareIn(String platform,Platform.ShareParams params,PlatformActionListener l){
        Platform p = ShareSDK.getPlatform(platform);
        p.setPlatformActionListener(l);
        p.share(params);
    }

    private Platform.ShareParams sharePrepareBase(String platform,String title,String content,String targetUrl){
        Platform.ShareParams sp = new Platform.ShareParams();
        if(cn.sharesdk.tencent.qq.QQ.NAME.equals(platform)){
            if (!TextUtils.isEmpty(title) && title.length() > 25) {
                title = title.substring(0, 25);
            }
            if (!TextUtils.isEmpty(content) && content.length() > 35) {
                content = content.substring(0, 35);
            }
        }
        sp.setShareType(Platform.SHARE_WEBPAGE);
        sp.setTitle(title);
        sp.setText(content);
        sp.setTitleUrl(targetUrl);
        sp.setUrl(targetUrl);
        sp.setSite("Neta");
        sp.setSiteUrl(targetUrl);
        return sp;
    }

    public void share(String platform, String title, String content, String targetUrl,
                             String imagePath, PlatformActionListener listener) {
        Platform.ShareParams sp = sharePrepareBase(platform, title, content, targetUrl);
        sp.setImagePath(imagePath);
        shareIn(platform, sp, listener);
    }

    public void share(String platform, String title, String content, String targetUrl,
                             Bitmap bitmap, PlatformActionListener listener) {

        Platform.ShareParams sp = sharePrepareBase(platform, title, content, targetUrl);
        sp.setImageData(bitmap);
        shareIn(platform, sp, listener);
    }

    public void share(String platform, String title, String content, String targetUrl,
                             Resources res, int imageRes, PlatformActionListener listener) {
        Bitmap bitmap = BitmapFactory.decodeResource(res, imageRes);
        share(platform, title, content, targetUrl, bitmap, listener);
    }

    public void oauth(String platform,PlatformActionListener l){
        Platform p = ShareSDK.getPlatform(platform);
        p.SSOSetting(false);
        p.setPlatformActionListener(l);
        p.authorize();
    }

    public void getUserInfo(String platform, PlatformActionListener listener){
        Platform p = ShareSDK.getPlatform(platform);
        p.setPlatformActionListener(listener);
        p.SSOSetting(true);
        p.showUser(null);
    }

    public static String getPlatform(String packageName, String className) {
        if (QQ.equals(packageName)) {
            return cn.sharesdk.tencent.qq.QQ.NAME;
        } else if (QZONE.equals(packageName)) {
            return QZone.NAME;
        } else if (SINA.equals(packageName)) {
            return SinaWeibo.NAME;
        } else if (WEIXIN.equals(packageName)) {
            return Wechat.NAME;
        } else if (WEIXIN.equals(packageName) ) {
            return null;
            //TODO
        }
        return null;
    }
}
