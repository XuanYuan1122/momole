package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.FeedFollowType2Entity;
import com.moemoe.lalala.model.entity.StreamFileEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * Created by yi on 2016/11/29.
 */

public class VideoExaminePresenter implements VideoExamineContract.Presenter {

    private VideoExamineContract.View view;
    private ApiService apiService;

    @Inject
    public VideoExaminePresenter(VideoExamineContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadExamineList(final int index) {
        apiService.loadVideoExamineList(index,ApiService.LENGHT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<StreamFileEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<StreamFileEntity> entities) {
                        if(view != null) view.onLoadExamineListSuccess(entities,index == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }
}
