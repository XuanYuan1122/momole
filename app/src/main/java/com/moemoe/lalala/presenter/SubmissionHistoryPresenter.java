package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.SubmissionItemEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by yi on 2016/11/29.
 */

public class SubmissionHistoryPresenter implements SubmissionHistoryContract.Presenter {

    private SubmissionHistoryContract.View view;
    private ApiService apiService;

    @Inject
    public SubmissionHistoryPresenter(SubmissionHistoryContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadSubmissionList(final int index) {
        apiService.loadSubmissionList(index,ApiService.LENGHT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<SubmissionItemEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<SubmissionItemEntity> entities) {
                        if(view != null) view.onLoadSubmissionListSuccess(entities, index == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }
}
