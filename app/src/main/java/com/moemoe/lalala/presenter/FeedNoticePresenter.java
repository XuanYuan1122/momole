package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.FeedNoticeEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by yi on 2016/11/29.
 */

public class FeedNoticePresenter implements FeedNoticeContract.Presenter {

    private FeedNoticeContract.View view;
    private ApiService apiService;

    @Inject
    public FeedNoticePresenter(FeedNoticeContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }


    @Override
    public void loadFeedNoticeList(final long timestamp) {
        apiService.loadFeedNoticeList(timestamp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<FeedNoticeEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<FeedNoticeEntity> entities) {
                        if(view != null) view.onLoadFeedNoticeListSuccess(entities, timestamp == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }
}
