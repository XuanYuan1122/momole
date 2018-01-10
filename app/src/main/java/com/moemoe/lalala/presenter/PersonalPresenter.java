package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.CreatePrivateMsgEntity;
import com.moemoe.lalala.model.entity.UserInfo;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * Created by yi on 2016/11/29.
 */

public class PersonalPresenter implements PersonalContract.Presenter {

    private PersonalContract.View view;
    private ApiService apiService;

    @Inject
    public PersonalPresenter(PersonalContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void requestUserInfo(String id) {
        apiService.requestUserInfoV2(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<UserInfo>() {
                    @Override
                    public void onSuccess(UserInfo info) {
                        if(view != null) view.onLoadUserInfo(info);
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        if(view != null) view.onLoadUserInfoFail();
                    }
                });
    }

    @Override
    public void followUser(String id,boolean isFollow) {
        if (!isFollow){
            apiService.followUser(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onFollowSuccess(true);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }else {
            apiService.cancelfollowUser(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onFollowSuccess(false);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }
    }

    @Override
    public void saveOrCancelBlackUser(String userId, boolean isSave) {
        if(isSave){
            apiService.removeBlackUser(userId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view!=null)view.onSaveOrCancelBlackSuccess(false);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view!=null)view.onFailure(code, msg);
                        }
                    });
        }else {
            apiService.saveBlackUser(userId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view!=null)view.onSaveOrCancelBlackSuccess(true);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view!=null)view.onFailure(code, msg);
                        }
                    });
        }
    }


    @Override
    public void release() {
        view = null;
    }
}
