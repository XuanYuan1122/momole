package com.moemoe.lalala.presenter;

import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AppUpdateEntity;
import com.moemoe.lalala.model.entity.RecommendTagEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * Created by yi on 2016/11/27.
 */

public class RecommendTagPresenter implements RecommendTagContract.Presenter {
    private RecommendTagContract.View view;
    private ApiService apiService;

    @Inject
    public RecommendTagPresenter(RecommendTagContract.View view, ApiService apiService){
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadRecommendTag(String folderType) {
        apiService.loadRecommendTag(folderType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<RecommendTagEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<RecommendTagEntity> entities) {
                        if(view != null) view.onLoadRecommendTagSuccess(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void loadKeyWordTag(String keyWord) {
        apiService.loadKeywordTag(keyWord)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<RecommendTagEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<RecommendTagEntity> entities) {
                        if(view != null) view.onLoadRecommendTagSuccess(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }
}
