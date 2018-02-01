package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.OfficialTag;
import com.moemoe.lalala.model.entity.SimpleListSend;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 *
 * Created by yi on 2016/11/29.
 */

public class SelectTagPresenter implements SelectTagContract.Presenter {

    private SelectTagContract.View view;
    private ApiService apiService;

    @Inject
    public SelectTagPresenter(SelectTagContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadOfficialTags() {
        apiService.loadOfficialTags()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<OfficialTag>>() {
                    @Override
                    public void onSuccess(ArrayList<OfficialTag> tags) {
                        if(view != null) view.onLoadOfficialTags(tags);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void saveUserTags(SimpleListSend entity) {
        apiService.saveUserTags(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onSaveUserTagsSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }
}
