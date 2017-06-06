package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.ChatContentEntity;
import com.moemoe.lalala.model.entity.SendPrivateMsgEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class ChatPresenter implements ChatContract.Presenter {

    private ChatContract.View view;
    private ApiService apiService;

    @Inject
    public ChatPresenter(ChatContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void loadTalkHistory(String talkId) {
        apiService.loadTalkHistory(talkId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<ChatContentEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<ChatContentEntity> entities) {
                        if(view != null) view.loadOrFindTalkHistorySuccess(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.loadOrFindTalkFailure();
                    }
                });
    }

    @Override
    public void findTalk(String talkId, String startTime, String endTime) {
        apiService.findTalk(talkId,startTime,endTime)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<ChatContentEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<ChatContentEntity> entities) {
                        if(view != null) view.loadOrFindTalkHistorySuccess(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.loadOrFindTalkFailure();
                    }
                });
    }

    @Override
    public void sendMsg(SendPrivateMsgEntity entity) {
        apiService.sendPrivateMsg(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if(view != null) view.sendMsgSuccess(s);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void release() {
        view = null;
    }
}
