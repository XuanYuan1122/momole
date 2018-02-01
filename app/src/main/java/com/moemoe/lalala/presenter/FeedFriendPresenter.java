package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.DiscoverEntity;
import com.moemoe.lalala.model.entity.FeedRecommendUserEntity;
import com.moemoe.lalala.model.entity.UserFollowTagEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 *
 * Created by yi on 2018/1/11.
 */

public class FeedFriendPresenter implements FeedFriendContract.Presenter {

    private FeedFriendContract.View view;
    private ApiService apiService;

    @Inject
    public FeedFriendPresenter(FeedFriendContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
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

    @Override
    public void loadDiscoverList(final long time) {
        apiService.loadFeedNoticeListV4(time)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<DiscoverEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<DiscoverEntity> entities) {
                        if(view!=null)view.onLoadDiscoverListSuccess(entities,time == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view!=null)view.onFailure(code, msg);
                    }
                });
    }
}
