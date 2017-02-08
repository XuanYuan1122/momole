package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.TrashEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class TrashListPresenter implements TrashListContract.Presenter {

    private TrashListContract.View view;
    private ApiService apiService;

    @Inject
    public TrashListPresenter(TrashListContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void doRequest(final int index, String type, int list_type) {
        if("image".equals(type)){
            if(list_type == 0){//my
                apiService.myImgTrashList(index,ApiService.LENGHT)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetResultSubscriber<ArrayList<TrashEntity>>() {
                            @Override
                            public void onSuccess(ArrayList<TrashEntity> entities) {
                                view.onSuccess(entities,index == 0);
                            }

                            @Override
                            public void onFail(int code,String msg) {
                                view.onFailure(code,msg);
                            }
                        });
            }else if(list_type == 1){//favorite
                apiService.myFavoriteImageTrashList(index,ApiService.LENGHT)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetResultSubscriber<ArrayList<TrashEntity>>() {
                            @Override
                            public void onSuccess(ArrayList<TrashEntity> entities) {
                                view.onSuccess(entities,index == 0);
                            }

                            @Override
                            public void onFail(int code,String msg) {
                                view.onFailure(code,msg);
                            }
                        });
            }
        }else if("text".equals(type)){
            if(list_type == 0){
                apiService.myTextTrashList(index,ApiService.LENGHT)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetResultSubscriber<ArrayList<TrashEntity>>() {
                            @Override
                            public void onSuccess(ArrayList<TrashEntity> entities) {
                                view.onSuccess(entities,index == 0);
                            }

                            @Override
                            public void onFail(int code,String msg) {
                                view.onFailure(code,msg);
                            }
                        });
            }else if(list_type == 1){
                apiService.myFavoriteTextTrashList(index,ApiService.LENGHT)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetResultSubscriber<ArrayList<TrashEntity>>() {
                            @Override
                            public void onSuccess(ArrayList<TrashEntity> entities) {
                                view.onSuccess(entities,index == 0);
                            }

                            @Override
                            public void onFail(int code,String msg) {
                                view.onFailure(code,msg);
                            }
                        });
            }
        }
    }
}
