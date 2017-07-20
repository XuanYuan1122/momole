package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.BadgeEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class AddressPresenter implements AddressContract.Presenter {

    private AddressContract.View view;
    private ApiService apiService;

    @Inject
    public AddressPresenter(AddressContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadUserAddress() {
        apiService.loadUserAddress()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<AddressEntity>() {
                    @Override
                    public void onSuccess(AddressEntity entity) {
                        view.onLoadAddressSuccess(entity);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void saveUserAddress(AddressEntity entity) {
        apiService.saveUserAddress(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        view.onSaveAddressSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        view.onFailure(code, msg);
                    }
                });
    }
}
