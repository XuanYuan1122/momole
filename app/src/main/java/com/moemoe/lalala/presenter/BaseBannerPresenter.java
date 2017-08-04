package com.moemoe.lalala.presenter;

import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.DepartmentEntity;
import com.moemoe.lalala.model.entity.DocListEntity;
import com.moemoe.lalala.model.entity.FeaturedEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class BaseBannerPresenter{

    private BaseBannerContract.View view;
    private ApiService apiService;

    public BaseBannerPresenter(BaseBannerContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

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

    public void requestFeatured(String room) {
        apiService.requestFreatured(room)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<FeaturedEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<FeaturedEntity> featuredEntities) {
                        if(view != null) view.onFeaturedLoadSuccess(featuredEntities);
                    }

                    @Override
                    public void onFail(int code,String msg) {
                    }
                });
    }

    public void release() {
        view = null;
    }
}
