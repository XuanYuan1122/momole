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

public class DepartPresenter implements DepartContract.Presenter {

    private DepartContract.View view;
    private ApiService apiService;
    private String before;

    @Inject
    public DepartPresenter(DepartContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void requestBannerData(String room) {
        apiService.requestNewBanner(room)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<BannerEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<BannerEntity> bannerEntities) {
                        view.onBannerLoadSuccess(bannerEntities);
                    }

                    @Override
                    public void onFail(int code,String msg) {
                    }
                });
    }

    @Override
    public void requestFeatured(String room) {
        apiService.requestFreatured(room)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<FeaturedEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<FeaturedEntity> featuredEntities) {
                        view.onFeaturedLoadSuccess(featuredEntities);
                    }

                    @Override
                    public void onFail(int code,String msg) {
                    }
                });
    }

    @Override
    public void requestDocList(final int index, final String room, int type) {
        if(type == 0){
            if(index == 0) {
                before = "";
            }
            apiService.requestDepartmentDocList(index,ApiService.LENGHT,room,before)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<DepartmentEntity>() {
                        @Override
                        public void onSuccess(DepartmentEntity departmentEntity) {
                            before = departmentEntity.getBefore();
                            view.onDocLoadSuccess(departmentEntity,index == 0);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }else if(type == 1){
            apiService.requestTagDocList(index,ApiService.LENGHT,room.equals("change")?"":room, AppSetting.SUB_TAG)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<DocListEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<DocListEntity> docListEntities) {
                            if(room.equals("change")){
                                view.onChangeSuccess(docListEntities);
                            }else {
                                view.onDocLoadSuccess(docListEntities,index == 0);
                            }
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }else if(type == 2){
            apiService.requestSwimDocList(index,ApiService.LENGHT, AppSetting.SUB_TAG)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<DocListEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<DocListEntity> docListEntities) {
                            if(room.equals("change")){
                                view.onChangeSuccess(docListEntities);
                            }else {
                                view.onDocLoadSuccess(docListEntities,index == 0);
                            }
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }
    }
}
