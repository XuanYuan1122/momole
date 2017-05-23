package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.SnowEntity;
import com.moemoe.lalala.model.entity.SnowInfo;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class SnowPresenter implements SnowContract.Presenter {

    private SnowContract.View view;
    private ApiService apiService;

    @Inject
    public SnowPresenter(SnowContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void requestSnowInfo() {
        apiService.requestSnowInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<SnowEntity>() {
                    @Override
                    public void onSuccess(SnowEntity snowEntity) {
                        if(view != null) view.updateSnowView(snowEntity);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void requestSnowRankInfo(final int index) {
        apiService.requestSnowRank(index,ApiService.LENGHT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<SnowInfo>() {
                    @Override
                    public void onSuccess(SnowInfo snowInfo) {
                        if(view != null) view.updateSnowList(snowInfo,index == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void release() {
        view = null;
    }
}
