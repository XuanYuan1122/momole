package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AddressEntity;

import java.util.Date;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class PhoneMainPresenter implements PhoneMainContract.Presenter {

    private PhoneMainContract.View view;
    private ApiService apiService;

    @Inject
    public PhoneMainPresenter(PhoneMainContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadRcToken() {
        apiService.loadRcToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if(view != null) view.onLoadRcTokenSuccess(s);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onLoadRcTokenFail(code, msg);
                    }
                });
    }

}
