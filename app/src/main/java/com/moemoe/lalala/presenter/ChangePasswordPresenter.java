package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.RegisterEntity;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class ChangePasswordPresenter implements ChangePasswordContract.Presenter {

    private ChangePasswordContract.View view;
    private ApiService apiService;

    @Inject
    public ChangePasswordPresenter (ChangePasswordContract.View view,ApiService apiService){
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void changePassword(String oldPwd, String newPwd) {
        RegisterEntity entity = new RegisterEntity();
        entity.newPassword = newPwd;
        entity.oldPassword = oldPwd;
        apiService.changePassword(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onChangeSuccess();
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void resetPwdByCode(String phone, String pwd) {
        RegisterEntity entity = new RegisterEntity();
        entity.mobile = phone;
        entity.newPassword = pwd;
        apiService.resetPwdByCode(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onChangeSuccess();
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void release() {
        view = null;
    }
}
