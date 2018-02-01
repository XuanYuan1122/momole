package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.UserFollowTagEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 *
 * Created by yi on 2018/1/11.
 */

public class FeedFollowPresenter implements FeedFollowContract.Presenter {

    private FeedFollowContract.View view;
    private ApiService apiService;

    @Inject
    public FeedFollowPresenter(FeedFollowContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }


    @Override
    public void loadUserTags() {
        apiService.loadUserTags()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<UserFollowTagEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<UserFollowTagEntity> entities) {
                        if(view != null) view.onLoadUserTagsSuccess(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }
}
