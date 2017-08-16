package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.BadgeEntity;
import com.moemoe.lalala.model.entity.RejectEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class UserRejectPresenter implements UserRejectContract.Presenter {

    private UserRejectContract.View view;
    private ApiService apiService;

    @Inject
    public UserRejectPresenter(UserRejectContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void getBlackList(final int index) {
        apiService.getBlackList(index,ApiService.LENGHT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<RejectEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<RejectEntity> list) {
                        view.onLoadBlackList(list, index == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void removeBlack(final RejectEntity item) {
        apiService.removeBlackUser(item.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        view.onRemoveBlackSuccess(item);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        view.onFailure(code, msg);
                    }
                });
    }
}