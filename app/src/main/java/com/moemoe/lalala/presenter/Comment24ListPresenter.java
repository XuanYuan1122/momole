package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.Comment24Entity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by yi on 2016/11/29.
 */

public class Comment24ListPresenter implements Comment24ListContract.Presenter {

    private Comment24ListContract.View view;
    private ApiService apiService;

    @Inject
    public Comment24ListPresenter(Comment24ListContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadCommentList(final int page) {
        apiService.load24Comments(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<Comment24Entity>>() {
                    @Override
                    public void onSuccess(ArrayList<Comment24Entity> entities) {
                        if(view != null) view.onLoadCommentSuccess(entities,page == 1);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }
}
