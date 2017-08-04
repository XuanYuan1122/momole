package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.NewDocListEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class FollowMainPresenter implements FollowMainContract.Presenter {

    private FollowMainContract.View view;
    private ApiService apiService;

    @Inject
    public FollowMainPresenter(FollowMainContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadFollowList(long lastTime, final boolean change, final boolean isPull) {
        apiService.getFeedFollowList(lastTime,ApiService.LENGHT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<NewDocListEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<NewDocListEntity> entities) {
                        if(change){
                            if(view!=null)view.onChangeListSuccess(entities);
                        }else {
                            if(view!=null)view.onLoadFollowListSuccess(entities,isPull);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view!=null)view.onFailure(code, msg);
                    }
                });
    }
}
