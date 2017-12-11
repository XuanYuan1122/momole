package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.DepartmentEntity;
import com.moemoe.lalala.model.entity.DepartmentGroupEntity;
import com.moemoe.lalala.model.entity.DocListEntity;
import com.moemoe.lalala.model.entity.FeaturedEntity;
import com.moemoe.lalala.model.entity.SendSubmissionEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * Created by yi on 2016/11/29.
 */

public class DepartPresenter implements DepartContract.Presenter {

    private DepartContract.View view;
    private ApiService apiService;
    private String before;

    @Inject
    public DepartPresenter(DepartContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void joinAuthor(final String id, final String name) {
        apiService.JoinAuthorGroup(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onJoinSuccess(id,name);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }


    @Override
    public void loadDepartmentGroup(String id) {
        apiService.loadDepartmentGroup(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<DepartmentGroupEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<DepartmentGroupEntity> entity) {
                        if(view != null) view.onLoadGroupSuccess(entity);
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
    public void requestDocList(final int index, final String room, int type) {
        if(type == 0){
            if(index == 0) {
                before = "";
            }
            apiService.requestDepartmentDocList(index,ApiService.LENGHT,room,before)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<DepartmentEntity>() {
                        @Override
                        public void onSuccess(DepartmentEntity departmentEntity) {
                            before = departmentEntity.getBefore();
                            if(view != null) view.onDocLoadSuccess(departmentEntity,index == 0);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }else if(type == 1){
            apiService.requestTagDocList(index,ApiService.LENGHT,room.equals("change")?"":room, false)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<DocListEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<DocListEntity> docListEntities) {
                            if(room.equals("change")){
                                if(view != null) view.onChangeSuccess(docListEntities);
                            }else {
                                if(view != null)  view.onDocLoadSuccess(docListEntities,index == 0);
                            }
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }else if(type == 2){
            apiService.requestSwimDocList(index,ApiService.LENGHT,false)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<DocListEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<DocListEntity> docListEntities) {
                            if(room.equals("change")){
                                if(view != null) view.onChangeSuccess(docListEntities);
                            }else {
                                if(view != null) view.onDocLoadSuccess(docListEntities,index == 0);
                            }
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }else if(type == 3){
            apiService.requestQiuMingShanDocList(index,ApiService.LENGHT, false)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<DocListEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<DocListEntity> docListEntities) {
                            if(room.equals("change")){
                                if(view != null) view.onChangeSuccess(docListEntities);
                            }else{
                                if(view != null) view.onDocLoadSuccess(docListEntities,index == 0);
                            }

                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }
    }

    @Override
    public void followDepartment(String id, final boolean follow) {
        apiService.followDepartment(!follow,id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        view.onFollowDepartmentSuccess(!follow);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void loadIsFollow(String id) {
        apiService.isFollowDepartment(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if(view!=null)view.onFollowDepartmentSuccess(aBoolean);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view!=null)view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void submission(SendSubmissionEntity entity) {
        apiService.submission(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onSubmissionSuccess();
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
