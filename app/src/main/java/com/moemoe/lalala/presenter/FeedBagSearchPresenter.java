package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.ShowFolderEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 *
 * Created by yi on 2016/11/29.
 */

public class FeedBagSearchPresenter implements FeedBagSearchContract.Presenter {

    private FeedBagSearchContract.View view;
    private ApiService apiService;

    @Inject
    public FeedBagSearchPresenter(FeedBagSearchContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadList(String type, String name, final int page) {
        apiService.loadFeedBagSearchList(type, name, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<ShowFolderEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<ShowFolderEntity> entities) {
                        if(view != null) view.onLoadListSuccess(entities,page == 1);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }
}
