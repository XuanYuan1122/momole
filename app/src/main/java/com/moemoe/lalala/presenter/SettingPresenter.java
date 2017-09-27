package com.moemoe.lalala.presenter;

import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AppUpdateEntity;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/27.
 */

public class SettingPresenter implements SettingContract.Presenter {
    private SettingContract.View view;
    private ApiService apiService;

    @Inject
    public SettingPresenter(SettingContract.View view, ApiService apiService){
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void checkVersion() {
        apiService.checkVersion(AppSetting.CHANNEL,AppSetting.VERSION_CODE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<AppUpdateEntity>() {
                    @Override
                    public void onSuccess(AppUpdateEntity appUpdateEntity) {
                        if(appUpdateEntity.getUpdateStatus() != 0){
                            if(view != null) view.showUpdateDialog(appUpdateEntity);
                        }else {
                            if(view != null) view.noUpdate();
                        }
                    }

                    @Override
                    public void onFail(int code,String msg) {
                    }
                });
    }

    @Override
    public void logout() {
        apiService.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.logoutSuccess();
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void modifySecret(boolean show, final int type) {
        if(type == 0){
            apiService.showFavorite(show)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess(){
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) {
                                view.modifySecretFail(type);
                                view.onFailure(code, msg);
                            }
                        }
                    });
        }else if(type == 1) {
            apiService.showFollow(show)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) {
                                view.modifySecretFail(type);
                                view.onFailure(code, msg);
                            }
                        }
                    });
        }else if(type == 2){
            apiService.showFans(show)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) {
                                view.modifySecretFail(type);
                                view.onFailure(code, msg);
                            }
                        }
                    });
        }
    }

    @Override
    public void shieldUser(boolean shield, String talkId) {
        apiService.ignoreUser(talkId,shield)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) {
                            view.shieldUserFail();
                            view.onFailure(code, msg);
                        }
                    }
                });
    }

    @Override
    public void release() {
        view = null;
    }
}
