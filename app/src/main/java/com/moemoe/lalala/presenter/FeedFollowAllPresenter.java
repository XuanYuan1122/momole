package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.FeedFollowType1Entity;
import com.moemoe.lalala.model.entity.UserFollowTagEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 *
 * Created by yi on 2018/1/11.
 */

public class FeedFollowAllPresenter implements FeedFollowAllContract.Presenter {

    private FeedFollowAllContract.View view;
    private ApiService apiService;

    @Inject
    public FeedFollowAllPresenter(FeedFollowAllContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }


    @Override
    public void loadList(final int index) {
        apiService.loadFollowAllList(index,ApiService.LENGHT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<FeedFollowType1Entity>>() {
                    @Override
                    public void onSuccess(ArrayList<FeedFollowType1Entity> entities) {
                        if(view != null) view.onLoadListSuccess(entities,index == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }
}
