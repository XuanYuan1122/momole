package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AuthorInfo;
import com.moemoe.lalala.model.entity.LoginEntity;
import com.moemoe.lalala.model.entity.LoginResultEntity;
import com.moemoe.lalala.model.entity.RegisterEntity;
import com.moemoe.lalala.view.activity.PhoneStateCheckActivity;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class PhoneStatePresenter implements PhoneStateContract.Presenter {

    private PhoneStateContract.View view;
    private ApiService apiService;

    @Inject
    public PhoneStatePresenter(PhoneStateContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void checkPhoneCode(int action, final AuthorInfo info, String code) {
        RegisterEntity bean = new RegisterEntity();
        if(action == PhoneStateCheckActivity.ACTION_REGISTER){
            bean.mobile = info.getPhone();
            bean.code = code;
            bean.password = info.getPassword();
            apiService.phoneRegister(bean)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onRegisterSuccess(info);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }else if(action == PhoneStateCheckActivity.ACTION_FIND_PASSWORD) {
            bean.code = code;
            bean.mobile = info.getPhone();
            apiService.checkVCode(bean)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onFindPwdSuccess();
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }
    }

    @Override
    public void login(final AuthorInfo info, LoginEntity entity) {
        apiService.login(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<LoginResultEntity>() {
                    @Override
                    public void onSuccess(LoginResultEntity loginResultEntity) {
                        if(view != null) view.onLoginSuccess(info,loginResultEntity);
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
