package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.PersonalMainEntity;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class PersonMainPresenter implements PersonMainContract.Presenter {

    private PersonMainContract.View view;
    private ApiService apiService;

    @Inject
    public PersonMainPresenter(PersonMainContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadInfo(String id) {
        apiService.getPersonalMain(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<PersonalMainEntity>() {
                    @Override
                    public void onSuccess(PersonalMainEntity personalMainEntity) {
                        if(view != null) view.onLoadInfoSuccess(personalMainEntity);
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void likeMapRole(final boolean isLike, String id) {
        apiService.likeUserMapRole(!isLike,id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onLikeSuccess(!isLike);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }
}
