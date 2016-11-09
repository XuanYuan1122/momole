package com.moemoe.lalala.network;

import android.content.Context;
import android.text.TextUtils;
import com.igexin.sdk.PushManager;
import com.moemoe.lalala.BaseActivity;
import com.moemoe.lalala.PhoneStateCheckActivity;
import com.moemoe.lalala.android.http.ResponseInfo;
import com.moemoe.lalala.android.storage.UpCompletionHandler;
import com.moemoe.lalala.android.storage.UploadManager;
import com.moemoe.lalala.data.AuthorInfo;
import com.moemoe.lalala.data.PersonBean;
import com.moemoe.lalala.utils.EncoderUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Haru on 2016/4/28 0028.
 */
public enum OtakuAccountV2 {
    INSTANCE;
    private IOtakuAccountV2 service = Otaku.getInstance().retrofit.create(IOtakuAccountV2.class);

    public Call<String> loginThird(Context context
            ,String open_id
            ,String platform){

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key",open_id);
            jsonObject.put("type","OPEN_ID");
            jsonObject.put("dev_id", PushManager.getInstance().getClientid(context) + "@and");
            jsonObject.put("platform",platform);
            String data = jsonObject.toString();
            String str = EncoderUtils.encryptBASE64(EncoderUtils.encryptGZIP(EncoderUtils.getEncoder().encrypt(data.getBytes("UTF-8"))));
            return service.loginThird(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Call<String> login(Context context
            ,String account
            ,String password){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key",account);
            jsonObject.put("password",password);
            jsonObject.put("type","MOBILE");
            jsonObject.put("dev_id", PushManager.getInstance().getClientid(context) + "@and");
            String data = jsonObject.toString();
            String str = EncoderUtils.encryptBASE64(EncoderUtils.encryptGZIP(EncoderUtils.getEncoder().encrypt(data.getBytes("UTF-8"))));
            return service.login(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 请求用户数据
     */
    public void requestSelfData(String token, final Context context) {
        Otaku.getAccountV2().requestSelfData(token).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String, String>() {
            @Override
            public void success(String token, String s) {
                PersonBean mMyself = new PersonBean();
                mMyself.readFromJsonContent(context,s);
                AuthorInfo mAuthorInfo = PreferenceManager.getInstance(context).getThirdPartyLoginMsg();
                mAuthorInfo.setmUUid(mMyself.uuid);
                mAuthorInfo.setmUserName(mMyself.name);
                mAuthorInfo.setmGender(mMyself.sex_str);
                mAuthorInfo.setNice_num(mMyself.nice_num);
                mAuthorInfo.setSlogan(mMyself.slogan);
                mAuthorInfo.setmHeadPath(mMyself.icon.path);
                mAuthorInfo.setRegister_time(mMyself.register_time);
                mAuthorInfo.setBirthday(mMyself.birthday);
                mAuthorInfo.setLevel_name(mMyself.level_name);
                mAuthorInfo.setScore(mMyself.score);
                mAuthorInfo.setLevel(mMyself.level);
                mAuthorInfo.setLevel_score_end(mMyself.level_score_end);
                mAuthorInfo.setLevel_score_start(mMyself.level_score_start);
                mAuthorInfo.setLevel_color(mMyself.level_color);
                mAuthorInfo.setmCoin(mMyself.coin);
                PreferenceManager.getInstance(context).saveThirdPartyLoginMsg(mAuthorInfo);
            }

            @Override
            public void failure(String e) {

            }
        }));
    }

    public Call<String> logout(Context context){
        PreferenceManager.getInstance(context).clearThirdPartyLoginMsg();
        return service.logout();
    }

    public Call<String> register(String account){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mobile",account);
            String data = EncoderUtils.encryptBASE64(EncoderUtils.encryptGZIP(EncoderUtils.getEncoder().encrypt(
                    jsonObject.toString().getBytes("UTF-8"))));
            return service.register(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Call<String> checkPhoneCode(int action
            ,AuthorInfo authorInfo
            ,String code){
        if(action == PhoneStateCheckActivity.ACTION_REGISTER){
            try {
                JSONObject json = new JSONObject();
                json.put("mobile", authorInfo.getmPhone());
                json.put("password",authorInfo.getmPassword());
                json.put("v_code", code);
                json.put("client_type", "");
                String data = EncoderUtils.encryptBASE64(EncoderUtils.encryptGZIP(EncoderUtils.getEncoder().encrypt(
                        json.toString().getBytes("UTF-8"))));
                return service.phoneRegister(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(action == PhoneStateCheckActivity.ACTION_FIND_PASSWORD){
            try {
                JSONObject json = new JSONObject();
                json.put("v_code", code);
                json.put("mobile", authorInfo.getmPhone());
                String data = EncoderUtils.encryptBASE64(EncoderUtils.encryptGZIP(EncoderUtils.getEncoder().encrypt(
                        json.toString().getBytes("UTF-8"))));
                return service.checkVCode(authorInfo.getmToken(),data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Call<String> requestCode4ResetPwd(String account){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mobile",account);
            String data = EncoderUtils.encryptBASE64(EncoderUtils.encryptGZIP(EncoderUtils.getEncoder().encrypt(
                    jsonObject.toString().getBytes("UTF-8"))));
            return service.requestCode4ResetPwd(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Call<String> changePassword(String token
            ,String password
            ,String passwordOld){
        try {
            JSONObject json = new JSONObject();
            json.put("password_new", password);
            json.put("password_old",passwordOld);
            String data = EncoderUtils.encryptBASE64(EncoderUtils.encryptGZIP(EncoderUtils.getEncoder().encrypt(
                    json.toString().getBytes("UTF-8"))));
            return service.changePassword(token, data);
        }catch (Exception e){

        }
        return null;
    }

    public Call<String> resetPwdByCode(String token
            ,String account
            ,String password
            ,String code){
        try {
            JSONObject json = new JSONObject();
            json.put("password", password);
            json.put("mobile",account);
            json.put("v_code", code);
            String data = EncoderUtils.encryptBASE64(EncoderUtils.encryptGZIP(EncoderUtils.getEncoder().encrypt(
                    json.toString().getBytes("UTF-8"))));
            return service.resetPwdByCode(token, data);
        }catch (Exception e){

        }
        return null;
    }

    public void requestQnFileKey(String token
            ,final int index
            ,final String path
            ,final OnNetWorkCallback<String,String> callback){
        if (TextUtils.isEmpty(path) || !new File(path).exists()) {
            callback.success(null, null);
            return ;
        }
        String suffix = FileUtil.getExtensionName(path);
        if (TextUtils.isEmpty(suffix)) {
            callback.failure(null);
            return ;
        }

        service.requestQnFileKey(token,suffix.toLowerCase()).enqueue(CallbackFactory.getInstance().callback(new OnNetWorkCallback<String,String>() {
            @Override
            public void success(String token, String res) {
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    final String qnFileName = jsonObject.optString("filePath");
                    String qnUToken = jsonObject.optString("uToken");
                    File file = new File(path);//http://183.136.139.10
                    UploadManager uploadManager = new UploadManager();
                    uploadManager.put(file, qnFileName, qnUToken, new UpCompletionHandler() {

                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject response) {
                            if (info.isOK()) {
                                String temp = index + "," + key;
                                callback.success(null, temp);
                            } else {
                                callback.failure(null);
                            }
                        }
                    }, null);
                }catch (Exception e){
                    callback.failure(e.toString());
                }
            }

            @Override
            public void failure(String e) {
                callback.failure(e);
            }
        }));
    }

    public Call<String> requestSelfData(String token){
        return service.requestSelfData(token, "USER_LOAD_SELF");
    }

    public void uploadFilesToQiniu(String token
            ,final ArrayList<String> paths
            ,final OnNetWorkCallback<String,ArrayList<String>> callback){

        OnNetWorkCallback<String,String> allCallback = new OnNetWorkCallback<String, String>() {
            ArrayList<String> resultPaths = new ArrayList<>();
            AtomicInteger count = new AtomicInteger(0);
            Map<Integer,String> map = new HashMap<>();

            @Override
            public void success(String token, String result) {
                int index = Integer.valueOf(result.split(",")[0]);
                String temp = result.split(",")[1];
                map.put(index,temp);
                int curSize = count.incrementAndGet();
                if(curSize == paths.size()) {
                    for (int i = 0; i < paths.size(); i++) {
                        resultPaths.add(map.get(i));
                    }
                    callback.success(token, resultPaths);
                }
            }

            @Override
            public void failure(String e) {
                callback.failure(e);
            }
        };

        for(int i = 0; i < paths.size();i++){
            String path = paths.get(i);
            requestQnFileKey(token, i,path,allCallback);
        }
    }

    public Call<String> requestCommentFromOther(String token
            ,int index){
        return service.requestCommentFromOther(token,index,Otaku.LENGTH);
    }

    public Call<String> checkSignToday(String token){
        return service.checkSignToday(token);
    }

    public Call<String> signToday(String token){
        return service.signToday(token);
    }
}


