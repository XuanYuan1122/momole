package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.HongBaoEntity;
import com.moemoe.lalala.model.entity.InviteUserEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 *
 * Created by yi on 2016/11/29.
 */

public class HongBaoListPresenter implements HongBaoListContract.Presenter {

    private HongBaoListContract.View view;
    private ApiService apiService;

    @Inject
    public HongBaoListPresenter(HongBaoListContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadHongBaoList(String id) {
        apiService.loadHongBaoList(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<HongBaoEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<HongBaoEntity> entities) {
                        if(view != null) view.onLoadHongBaoListSuccess(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }
}
