package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.DocResponse;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by yi on 2016/11/29.
 */

public class OldDocPresenter implements OldDocContract.Presenter {

    private OldDocContract.View view;
    private ApiService apiService;

    @Inject
    public OldDocPresenter(OldDocContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void requestBannerData(String room) {
        apiService.requestNewBanner(room)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<BannerEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<BannerEntity> bannerEntities) {
                        if(view != null) view.onBannerLoadSuccess(bannerEntities);
                    }

                    @Override
                    public void onFail(int code,String msg) {
                    }
                });
    }

    @Override
    public void loadOldDocList(String type, final long time) {
        apiService.loadOldDocList(type,time)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<DocResponse>>() {
                    @Override
                    public void onSuccess(ArrayList<DocResponse> list) {
                        if(view != null) view.loadOldDocListSuccess(list,time == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }
}
