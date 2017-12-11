package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.LuntanTabEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 】
 * Created by yi on 2016/11/27.
 */

public class LuntanPresenter implements LuntanContract.Presenter {
    private LuntanContract.View view;
    private ApiService apiService;

    @Inject
    public LuntanPresenter(LuntanContract.View view, ApiService apiService){
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadTabList() {
        apiService.loadLuntanTabList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<LuntanTabEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<LuntanTabEntity> entities) {
                        if(view != null) view.onLoadTabListSuccess(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }
}
