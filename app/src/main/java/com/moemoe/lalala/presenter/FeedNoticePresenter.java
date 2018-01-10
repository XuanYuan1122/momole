package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.FeedNoticeEntity;
import com.moemoe.lalala.model.entity.FeedRecommendUserEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 *
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
    public void loadFeedNoticeList(String type, final long followTime, final long notifyTime) {
        apiService.loadFeedNoticeListV3(type,followTime,notifyTime)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<FeedNoticeEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<FeedNoticeEntity> entities) {
                        if(view != null) view.onLoadFeedNoticeListSuccess(entities, followTime == 0 && notifyTime == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void likeDynamic(String id, final boolean isLike, final int position) {
        apiService.likeDynamic(id,!isLike)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view!=null)view.onLikeDynamicSuccess(!isLike,position);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view!=null)view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void loadRecommendUserList() {
        apiService.loadFeedRecommentUserList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<FeedRecommendUserEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<FeedRecommendUserEntity> entities) {
                        if(view!=null)view.onLoadRecommendUserListSuccess(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view!=null)view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void followUser(String id, boolean isFollow, final int position) {
        if (!isFollow){
            apiService.followUser(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onFollowUserSuccess(true,position);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }else {
            apiService.cancelfollowUser(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onFollowUserSuccess(false,position);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }
    }
}
