package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.BadgeEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class BadgePresenter implements BadgeContract.Presenter {

    private BadgeContract.View view;
    private ApiService apiService;

    @Inject
    public BadgePresenter(BadgeContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }


    @Override
    public void requestMyBadge(final int index) {
        apiService.requestMyBadge(index,ApiService.LENGHT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<BadgeEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<BadgeEntity> badgeEntities) {
                        view.loadMyBadgeSuccess(badgeEntities,index == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void requestAllBadge(final int index) {
        apiService.requestAllBadge(index,ApiService.LENGHT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<BadgeEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<BadgeEntity> badgeEntities) {
                        view.loadAllBadgeSuccess(badgeEntities,index == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void saveBadge(ArrayList<String> ids) {
        apiService.saveBadge(ids)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        view.saveSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void buyBadge(String id, final int position) {
        apiService.buyBadge(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        view.buySuccess(position);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        view.onFailure(code,msg);
                    }
                });
    }
}
