package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.ShowFolderEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 *
 * Created by yi on 2016/11/29.
 */

public class FeedBagPresenter implements FeedBagContract.Presenter {

    private FeedBagContract.View view;
    private ApiService apiService;

    @Inject
    public FeedBagPresenter(FeedBagContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadHotBag(int index) {
        apiService.loadHotFolderV2(index)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<ShowFolderEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<ShowFolderEntity> entities) {
                        if(view != null) view.onLoadHotBagSuccess(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void loadFeedBagList(String type,final int index) {
        apiService.loadNewFolderV2(type,index,ApiService.LENGHT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<ShowFolderEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<ShowFolderEntity> entities) {
                        if(view != null) view.onLoadFeedBagListSuccess(entities,index == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }
}
