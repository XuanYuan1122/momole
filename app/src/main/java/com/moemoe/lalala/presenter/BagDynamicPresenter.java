package com.moemoe.lalala.presenter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.BagMyEntity;
import com.moemoe.lalala.model.entity.BagMyShowEntity;
import com.moemoe.lalala.model.entity.DynamicEntity;
import com.moemoe.lalala.model.entity.DynamicTopEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class BagDynamicPresenter implements BagDynamicContract.Presenter {

    private BagDynamicContract.View view;
    private ApiService apiService;

    @Inject
    public BagDynamicPresenter(BagDynamicContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadTop() {
        apiService.loadDynamicTop()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<DynamicTopEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<DynamicTopEntity> dynamicTopEntities) {
                        if(view != null) view.onLoadTopSuccess(dynamicTopEntities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void loadList(long lastTime) {
        apiService.loadDynamicList(lastTime)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<DynamicEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<DynamicEntity> dynamicEntities) {
                        if(view != null) view.onLoadListSuccess(dynamicEntities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }
}
