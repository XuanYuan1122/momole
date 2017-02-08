package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.CommentListSendEntity;
import com.moemoe.lalala.model.entity.NewCommentEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class CommentListPresenter implements CommentListContract.Presenter {

    private CommentListContract.View view;
    private ApiService apiService;

    @Inject
    public CommentListPresenter(CommentListContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void doRequest(final int index, String userId) {
        apiService.getCommentsList(userId,index,ApiService.LENGHT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<NewCommentEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<NewCommentEntity> entities) {
                        view.onSuccess(entities,index == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        view.onFailure(code,msg);
                    }
                });

    }

    @Override
    public void sendComment(CommentListSendEntity entity) {
        apiService.sendComment(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        view.onSendSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        view.onFailure(code,msg);
                    }
                });
    }
}
