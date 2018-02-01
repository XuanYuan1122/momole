package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.FeedFollowOther1Entity;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 *
 * Created by yi on 2018/1/11.
 */

public class FeedFollowOther1Presenter implements FeedFollowOther1Contract.Presenter {

    private FeedFollowOther1Contract.View view;
    private ApiService apiService;

    @Inject
    public FeedFollowOther1Presenter(FeedFollowOther1Contract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadData(String id) {
        apiService.loadTagContent(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<FeedFollowOther1Entity>() {
                    @Override
                    public void onSuccess(FeedFollowOther1Entity feedFollowOther1Entity) {
                        if(view != null) view.onLoadDataSuccess(feedFollowOther1Entity);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }
}
