package com.moemoe.lalala.presenter;

import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.CoinDetailEntity;
import com.moemoe.lalala.model.entity.DocListEntity;
import com.moemoe.lalala.model.entity.ReplyEntity;
import com.moemoe.lalala.model.entity.WallBlock;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class CommentPresenter implements CommentContract.Presenter {

    private CommentContract.View view;
    private ApiService apiService;

    @Inject
    public CommentPresenter(CommentContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void doRequest(final int data, final int type) {
        if(type == 0){
            apiService.requestCommentFromOther(data,ApiService.LENGHT)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<ReplyEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<ReplyEntity> replyEntities) {
                            view.onSuccess(replyEntities,data == 0);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }else if(type == 3 || type == 2){
            apiService.requestQiuMingShanDocList(data,ApiService.LENGHT, AppSetting.SUB_TAG)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<DocListEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<DocListEntity> docListEntities) {
                            if(type == 3){
                                view.onSuccess(docListEntities,data == 0);
                            }else if(type == 2){
                                view.onChangeSuccess(docListEntities);
                            }

                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }else if(type == 4){
            apiService.getWallBlocksV2(data)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<WallBlock>>() {
                        @Override
                        public void onSuccess(ArrayList<WallBlock> wallBlocks) {
                            view.onSuccess(wallBlocks,data == 1);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }else if(type == 5){
            apiService.getCoinDetails(data,ApiService.LENGHT)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<CoinDetailEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<CoinDetailEntity> coinDetailEntities) {
                            view.onSuccess(coinDetailEntities,data == 0);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }
    }
}
