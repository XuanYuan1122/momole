package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.SendSubmissionEntity;
import com.moemoe.lalala.model.entity.SubmissionDepartmentEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by yi on 2016/11/29.
 */

public class SubmissionPresenter implements SubmissionContract.Presenter {

    private SubmissionContract.View view;
    private ApiService apiService;

    @Inject
    public SubmissionPresenter(SubmissionContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadDepartment() {
        apiService.loadDepartment()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<SubmissionDepartmentEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<SubmissionDepartmentEntity> entities) {
                        if(view != null) view.onLoadDepartmentSuccess(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void submission(SendSubmissionEntity entity) {
        apiService.submission(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onSubmissionSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }
}
