package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.DocResponse;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 *
 * Created by yi on 2018/1/11.
 */

public class FeedFollowOther2Presenter implements FeedFollowOther2Contract.Presenter {

    private FeedFollowOther2Contract.View view;
    private ApiService apiService;

    @Inject
    public FeedFollowOther2Presenter(FeedFollowOther2Contract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadList(String id, final int index) {
        apiService.loadTagDocList(id,index,ApiService.LENGHT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<DocResponse>>() {
                    @Override
                    public void onSuccess(ArrayList<DocResponse> docResponses) {
                        if(view != null) view.onLoadListSuccess(docResponses,index == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }
}
