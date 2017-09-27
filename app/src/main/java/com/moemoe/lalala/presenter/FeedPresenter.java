package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.FeaturedEntity;
import com.moemoe.lalala.model.entity.NewDynamicEntity;
import com.moemoe.lalala.model.entity.XianChongEntity;

import java.util.ArrayList;

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
    public void loadList(final long time, String type) {
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
            apiService.loadFeedRandomList(time)
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
            apiService.loadFeedMyList(time)
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
    public void requestFeatured(String room) {
        apiService.requestFreatured(room)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<FeaturedEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<FeaturedEntity> featuredEntities) {
                        if(view != null) view.onFeaturedLoadSuccess(featuredEntities);
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
}
