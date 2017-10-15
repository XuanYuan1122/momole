package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.Comment24Entity;
import com.moemoe.lalala.model.entity.FeaturedEntity;
import com.moemoe.lalala.model.entity.NewDynamicEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.model.entity.XianChongEntity;

import java.util.ArrayList;
import java.util.Random;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class FeedPresenter implements FeedContract.Presenter {

    private FeedContract.View view;
    private ApiService apiService;

    @Inject
    public FeedPresenter(FeedContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }


    @Override
    public void loadList(final long time, String type,String id) {
        if("follow".equals(type)){
            apiService.loadFeedFollowList(time)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<NewDynamicEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<NewDynamicEntity> newDynamicEntities) {
                            if(view != null) view.onLoadListSuccess(newDynamicEntities,time == 0);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }else if("random".equals(type)){
            apiService.loadFeedRandomList((int) time,ApiService.LENGHT)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<NewDynamicEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<NewDynamicEntity> newDynamicEntities) {
                            if(view != null) view.onLoadListSuccess(newDynamicEntities,time == 0);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }else if("ground".equals(type)){
            apiService.loadFeedGroundList(time)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<NewDynamicEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<NewDynamicEntity> newDynamicEntities) {
                            if(view != null) view.onLoadListSuccess(newDynamicEntities,time == 0);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }else if("my".equals(type)){
            apiService.loadFeedMyList(id,time)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<NewDynamicEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<NewDynamicEntity> newDynamicEntities) {
                            if(view != null) view.onLoadListSuccess(newDynamicEntities,time == 0);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }
    }

    @Override
    public void loadList(final int index) {
        apiService.loadFeedFavoriteList(index,ApiService.LENGHT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<NewDynamicEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<NewDynamicEntity> newDynamicEntities) {
                        if(view != null) view.onLoadListSuccess(newDynamicEntities,index == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void requestBannerData(String room) {
        apiService.requestNewBanner(room)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<BannerEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<BannerEntity> bannerEntities) {
                        if(view != null) view.onBannerLoadSuccess(bannerEntities);
                    }

                    @Override
                    public void onFail(int code,String msg) {
                    }
                });
    }

    @Override
    public void loadXianChongList() {
        apiService.loadXianChongList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<XianChongEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<XianChongEntity> entities) {
                        if(view!=null) view.onLoadXianChongSuccess(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view!=null)view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void loadFolder() {
        apiService.load24Folder()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<ShowFolderEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<ShowFolderEntity> entities) {
                        if(view!=null)view.onLoadFolderSuccess(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view!=null)view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void loadComment() {
        apiService.load24Comments(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<Comment24Entity>>() {
                    @Override
                    public void onSuccess(ArrayList<Comment24Entity> entities) {
                        if(view!=null){
                            if(entities.size() > 0){
                                Random random = new Random();
                                int i = random.nextInt(entities.size());
                                view.onLoadCommentSuccess(entities.get(i));
                            }else {
                                view.onLoadCommentSuccess(null);
                            }
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view!=null)view.onFailure(code, msg);
                    }
                });
    }
}
