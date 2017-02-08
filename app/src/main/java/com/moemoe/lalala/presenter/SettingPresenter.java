package com.moemoe.lalala.presenter;

import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AppUpdateEntity;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
                            view.showUpdateDialog(appUpdateEntity);
                        }else {
                            view.noUpdate();
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
                        view.logoutSuccess();
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        view.onFailure(code,msg);
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
                            view.modifySecretFail(type);
                            view.onFailure(code,msg);
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
                            view.modifySecretFail(type);
                            view.onFailure(code,msg);
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
                            view.modifySecretFail(type);
                            view.onFailure(code,msg);
                        }
                    });
        }
    }
}
