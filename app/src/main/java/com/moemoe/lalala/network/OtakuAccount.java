//package com.moemoe.lalala.network;
//
//import android.content.Context;
//import android.text.TextUtils;
//
//import com.app.Utils;
//import com.app.common.Callback;
//import com.app.image.ImageOptions;
//import com.igexin.sdk.PushManager;
//import com.moemoe.lalala.PhoneStateCheckActivity;
//import com.moemoe.lalala.R;
//import com.moemoe.lalala.android.http.ResponseInfo;
//import com.moemoe.lalala.android.storage.UpCompletionHandler;
//import com.moemoe.lalala.android.storage.UploadManager;
//import com.moemoe.lalala.callback.BaseCommonCallback;
//import com.moemoe.lalala.data.AuthorInfo;
//import com.moemoe.lalala.utils.EncoderUtils;
//import com.moemoe.lalala.utils.FileUtil;
//import com.moemoe.lalala.utils.PhoneUtil;
//import com.moemoe.lalala.utils.PreferenceManager;
//import com.moemoe.lalala.utils.ToastUtil;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * Created by Haru on 2016/4/28 0028.
// */
//public enum  OtakuAccount {
//    INSTANCE;
//
//    private IOtakuAccount service = Utils.http().create(IOtakuAccount.class);
//
//    public void loginThird(Context context,String open_id,String platform,Callback.InterceptCallback<String> callback){
//
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("key",open_id);
//            jsonObject.put("type","OPEN_ID");
//            jsonObject.put("dev_id", PushManager.getInstance().getClientid(context) + "@and");
//            jsonObject.put("platform",platform);
//            String data = jsonObject.toString();
//            String str = EncoderUtils.encryptBASE64(EncoderUtils.encryptGZIP(EncoderUtils.getEncoder().encrypt(data.getBytes("UTF-8"))));
//            service.loginThird(str, callback);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void login(Context context,String account,String password,Callback.InterceptCallback<String> callback){
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("key",account);
//            jsonObject.put("password",password);
//            jsonObject.put("type","MOBILE");
//            jsonObject.put("dev_id", PushManager.getInstance().getClientid(context) + "@and");
//            String data = jsonObject.toString();
//            String str = EncoderUtils.encryptBASE64(EncoderUtils.encryptGZIP(EncoderUtils.getEncoder().encrypt(data.getBytes("UTF-8"))));
//            service.login(str, callback);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void logout(Context context,Callback.CommonCallback<String> callback){
//        PreferenceManager.getInstance(context).clearThirdPartyLoginMsg();
//        service.logout(callback);
//    }
//
//    public void register(String account,Callback.CommonCallback<String> callback){
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("mobile",account);
//            String data = EncoderUtils.encryptBASE64(EncoderUtils.encryptGZIP(EncoderUtils.getEncoder().encrypt(
//                    jsonObject.toString().getBytes("UTF-8"))));
//            service.register(data, callback);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void checkPhoneCode(int action,AuthorInfo authorInfo,String code,Callback.InterceptCallback<String> callback,Context context){
//        if(action == PhoneStateCheckActivity.ACTION_REGISTER){
//            try {
//                JSONObject json = new JSONObject();
//                json.put("mobile", authorInfo.getmPhone());
//                json.put("password",authorInfo.getmPassword());
//                json.put("v_code", code);
//                json.put("client_type", "");
//                String data = EncoderUtils.encryptBASE64(EncoderUtils.encryptGZIP(EncoderUtils.getEncoder().encrypt(
//                        json.toString().getBytes("UTF-8"))));
//                service.phoneRegister(data,callback);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }else if(action == PhoneStateCheckActivity.ACTION_FIND_PASSWORD){
//            try {
//                JSONObject json = new JSONObject();
//                json.put("v_code", code);
//                json.put("mobile", authorInfo.getmPhone());
//                String data = EncoderUtils.encryptBASE64(EncoderUtils.encryptGZIP(EncoderUtils.getEncoder().encrypt(
//                        json.toString().getBytes("UTF-8"))));
//                service.checkVCode(authorInfo.getmToken(),data,new BaseCommonCallback(callback,context));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void requestCode4ResetPwd(String account,Callback.CommonCallback<String> callback){
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("mobile",account);
//            String data = EncoderUtils.encryptBASE64(EncoderUtils.encryptGZIP(EncoderUtils.getEncoder().encrypt(
//                    jsonObject.toString().getBytes("UTF-8"))));
//            service.requestCode4ResetPwd(data, callback);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void changePassword(String token,String password,String passwordOld,Callback.InterceptCallback<String> callback,Context context){
//        try {
//            JSONObject json = new JSONObject();
//            json.put("password_new", password);
//            json.put("password_old",passwordOld);
//            String data = EncoderUtils.encryptBASE64(EncoderUtils.encryptGZIP(EncoderUtils.getEncoder().encrypt(
//                    json.toString().getBytes("UTF-8"))));
//            service.changePassword(token, data, new BaseCommonCallback(callback, context));
//        }catch (Exception e){
//
//        }
//    }
//
//    public void resetPwdByCode(String token,String account,String password,String code,Callback.InterceptCallback<String> callback,Context context){
//        try {
//            JSONObject json = new JSONObject();
//            json.put("password", password);
//            json.put("mobile",account);
//            json.put("v_code", code);
//            String data = EncoderUtils.encryptBASE64(EncoderUtils.encryptGZIP(EncoderUtils.getEncoder().encrypt(
//                    json.toString().getBytes("UTF-8"))));
//            service.resetPwdByCode(token, data, new BaseCommonCallback(callback,context));
//        }catch (Exception e){
//
//        }
//    }
//
//    public void requestQnFileKey(String token, final int index ,final String path, final Callback.CommonCallback<String> callback){
//        if (TextUtils.isEmpty(path) || !new File(path).exists()) {
//            callback.onError(null,false);
//            return ;
//        }
//        String suffix = FileUtil.getExtensionName(path);
//        if (TextUtils.isEmpty(suffix)) {
//            callback.onError(null,false);
//            return ;
//        }
//        service.requestQnFileKey(token, suffix.toLowerCase(), new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                try {
//                    JSONObject json = new JSONObject(result);
//                    if (json.optInt("ok") == Otaku.SERVER_OK) {
//                        JSONObject jsonObject = new JSONObject(json.optString("data"));
//                        final String qnFileName = jsonObject.optString("filePath");
//                        String qnUToken = jsonObject.optString("uToken");
//                        File file = new File(path);//http://183.136.139.10
//                        UploadManager uploadManager = new UploadManager();
//                        uploadManager.put(file, qnFileName, qnUToken, new UpCompletionHandler() {
//
//                            @Override
//                            public void complete(String key, ResponseInfo info, JSONObject response) {
//                                if (info.isOK()) {
//                                    String temp = index + "," + key;
//                                    callback.onSuccess(temp);
//                                } else {
//                                    callback.onError(null, false);
//                                }
//                            }
//                        }, null);
//                    } else {
//                        callback.onError(null, false);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                callback.onError(ex, isOnCallback);
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//                callback.onCancelled(cex);
//            }
//
//            @Override
//            public void onFinished() {
//                callback.onFinished();
//            }
//        });
//    }
//
//    public void requestSelfData(String token,Callback.InterceptCallback<String> callback,Context context){
//        service.requestSelfData(token, "USER_LOAD_SELF", new BaseCommonCallback(callback,context));
//    }
//
//    public void uploadFilesToQiniu(String token, final ArrayList<String> paths, final Callback.CommonCallback<ArrayList<String>> callback){
//
//        Callback.CommonCallback<String> callback1 = new Callback.CommonCallback<String>() {
//            ArrayList<String> resultPaths = new ArrayList<>();
//            AtomicInteger count = new AtomicInteger(0);
//            Map<Integer,String> map = new HashMap<>();
//
//            @Override
//            public void onSuccess(String result) {
//                int index = Integer.valueOf(result.split(",")[0]);
//                String temp = result.split(",")[1];
//                map.put(index,temp);
//                int curSize = count.incrementAndGet();
//                if(curSize == paths.size()){
//                    for (int i = 0;i < paths.size();i++){
//                        resultPaths.add(map.get(i));
//                    }
//                    callback.onSuccess(resultPaths);
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                callback.onError(ex,isOnCallback);
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//                callback.onCancelled(cex);
//            }
//
//            @Override
//            public void onFinished() {
//                callback.onFinished();
//            }
//        };
//
//
//        for(int i = 0; i < paths.size();i++){
//            String path = paths.get(i);
//            //requestQnFileKey(token, i,path,callback1);
//        }
//    }
//
//    public void requestCommentFromOther(String token,int index,Callback.InterceptCallback<String> callback,Context context){
//        service.requestCommentFromOther(token,index,Otaku.LENGTH,new BaseCommonCallback(callback,context));
//    }
//}
//
//
