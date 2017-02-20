package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AuthorInfo;
import com.moemoe.lalala.model.entity.LoginEntity;
import com.moemoe.lalala.model.entity.LoginResultEntity;
import com.moemoe.lalala.model.entity.ModifyEntity;
import com.moemoe.lalala.model.entity.ThirdLoginEntity;
import com.moemoe.lalala.utils.PreferenceUtils;

import java.util.HashMap;

import javax.inject.Inject;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;
    private ApiService apiService;

    @Inject
    public LoginPresenter(LoginContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void login(LoginEntity entity) {
        apiService.login(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<LoginResultEntity>() {
                    @Override
                    public void onSuccess(LoginResultEntity loginResultEntity) {
                        view.onLoginSuccess(loginResultEntity);
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void loginThird(String platform, final String devId) {
        getUserInfo(platform, new PlatformActionListener() {
            @Override
            public void onComplete(final Platform platform, int i, HashMap<String, Object> hashMap) {
                final PlatformDb db = platform.getDb();
                ThirdLoginEntity bean = new ThirdLoginEntity(db.getUserName(),db.getUserId(),platform.getName(), devId);
                apiService.loginThird(bean)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetResultSubscriber<LoginResultEntity>() {
                            @Override
                            public void onSuccess(LoginResultEntity entity) {
                                AuthorInfo authorInfo = new AuthorInfo();
                                authorInfo.setOpenId(db.getUserId());
                                authorInfo.setPlatform(platform.getName());
                                if(entity.isNew()){
                                    authorInfo.setHeadPath(db.getUserIcon());
                                    authorInfo.setUserName(db.getUserName());
                                }else {
                                    authorInfo.setUserName(entity.getUserName());
                                    if(!entity.getHeadPath().contains("http")){
                                        authorInfo.setHeadPath(ApiService.URL_QINIU + entity.getHeadPath());
                                    }else {
                                        authorInfo.setHeadPath(entity.getHeadPath());
                                    }

                                }
                                authorInfo.setToken(entity.getToken());
                                authorInfo.setUserId(entity.getUserId());
                                authorInfo.setCoin(entity.getCoin());
                                authorInfo.setLevel(entity.getLevel());
                                authorInfo.setOpenBag(entity.isOpenBag());
                                if(entity.isNew()){
                                    ModifyEntity entity1 = new ModifyEntity();
                                    entity1.birthday = "";
                                    entity1.sex = db.getUserGender();
                                    entity1.nickName = db.getUserName();
                                    entity1.background = "";
                                    entity1.headPath = db.getUserIcon();
                                    entity1.signature = "";
                                    apiService.modifyAll(entity1)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new NetSimpleResultSubscriber() {
                                                @Override
                                                public void onSuccess() {
                                                }

                                                @Override
                                                public void onFail(int code,String msg) {
                                                }
                                            });
                                }
                                PreferenceUtils.setAuthorInfo(authorInfo);
                                view.onLoginThirdSuccess(entity.getUserId());
                            }

                            @Override
                            public void onFail(int code,String msg) {
                                view.onFailure(code,msg);
                            }
                        });
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                view.onFailure(-3,"");
            }

            @Override
            public void onCancel(Platform platform, int i) {
                view.onFailure(-2,"");
            }
        });
    }



    private void getUserInfo(String platform, PlatformActionListener listener){
        Platform p = ShareSDK.getPlatform(platform);
        p.setPlatformActionListener(listener);
        p.SSOSetting(true);
        p.showUser(null);
    }
}