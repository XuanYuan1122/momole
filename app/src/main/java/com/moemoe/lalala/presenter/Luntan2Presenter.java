package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.DepartmentGroupEntity;
import com.moemoe.lalala.model.entity.DocResponse;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 *
 * Created by yi on 2016/11/29.
 */

public class Luntan2Presenter implements Luntan2Contract.Presenter {

    private Luntan2Contract.View view;
    private ApiService apiService;

    @Inject
    public Luntan2Presenter(Luntan2Contract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadDepartmentGroup(String id) {
        apiService.loadDepartmentGroup(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<DepartmentGroupEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<DepartmentGroupEntity> entity) {
                        if(view != null) view.onLoadGroupSuccess(entity);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void loadDocList(String id, final long timestamp) {
        apiService.loadDepartmentDocList(id,timestamp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<DocResponse>>() {
                    @Override
                    public void onSuccess(ArrayList<DocResponse> responses) {
                        if(view != null) view.onLoadDocListSuccess(responses,timestamp == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void joinAuthor(final String id, final String name) {
        apiService.JoinAuthorGroup(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onJoinSuccess(id,name);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }
}
