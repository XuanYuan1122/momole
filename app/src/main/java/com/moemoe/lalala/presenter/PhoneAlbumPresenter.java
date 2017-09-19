package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.PhoneAlbumEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class PhoneAlbumPresenter implements PhoneAlbumContract.Presenter {

    private PhoneAlbumContract.View view;
    private ApiService apiService;

    @Inject
    public PhoneAlbumPresenter(PhoneAlbumContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }


    @Override
    public void loadAlbumList(final int index) {
        apiService.loadAlbumList(index,18)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<PhoneAlbumEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<PhoneAlbumEntity> entities) {
                        if(view != null) view.onLoadAlbumListSuccess(entities,index == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void loadAlbumItemList(String typeId, final int index) {
        apiService.loadAlbumItemList(typeId,index,18)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<PhoneAlbumEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<PhoneAlbumEntity> entities) {
                        if(view != null) view.onLoadAlbumItemListSuccess(entities,index == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void loadAlbumCount() {
        apiService.loadAlbumCount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        if(view != null) view.onLoadAlbumCountSuccess(integer);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void loadAlbumItemCount(String typeId) {
        apiService.loadAlbumItemCount(typeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        if(view != null) view.onLoadAlbumItemCountSuccess(integer);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }
}
