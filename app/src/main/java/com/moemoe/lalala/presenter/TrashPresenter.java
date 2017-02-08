package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.DocTagEntity;
import com.moemoe.lalala.model.entity.TagLikeEntity;
import com.moemoe.lalala.model.entity.TagSendEntity;
import com.moemoe.lalala.model.entity.TrashEntity;
import com.moemoe.lalala.model.entity.TrashOperationEntity;
import com.moemoe.lalala.view.activity.ImageTrashActivity;
import com.moemoe.lalala.view.activity.TrashActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class TrashPresenter implements TrashContract.Presenter {

    private TrashContract.View view;
    private ApiService apiService;
    private TrashOperationEntity entity;

    @Inject
    public TrashPresenter(TrashContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
        if(view instanceof TrashActivity){
            entity = new TrashOperationEntity("text");
        }else if(view instanceof ImageTrashActivity){
            entity = new TrashOperationEntity("image");
        }
    }

    private static final Comparator<TrashEntity> timeComparator = new Comparator<TrashEntity>() {
        @Override
        public int compare(TrashEntity lhs, TrashEntity rhs) {
            return lhs.getTimestamp() - rhs.getTimestamp();
        }
    };

    @Override
    public void getTrashList(int type,int time) {
        if(type == 0){//text
            apiService.getTextTrashList(ApiService.LENGHT,time)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<TrashEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<TrashEntity> entities) {
                            Collections.sort(entities,timeComparator);
                            view.onLoadListSuccess(entities);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }else if(type == 1){//img
            apiService.getImgTrashList(ApiService.LENGHT,time)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<TrashEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<TrashEntity> entities) {
                            Collections.sort(entities,timeComparator);
                            view.onLoadListSuccess(entities);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }
    }

    @Override
    public void operationTrash(String id,boolean fun) {
        entity.ids.add(new TrashOperationEntity.Ids(id,fun));
    }

    @Override
    public void favoriteTrash(final TrashEntity entity, final String type) {
        if(!entity.isMark()){
            apiService.favoriteTrash(type,entity.getDustbinId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            entity.setMark(true);
                            view.onFavoriteTrashSuccess(entity);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }else {
            apiService.cancelFavoriteTrash(type,entity.getDustbinId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            entity.setMark(false);
                            view.onFavoriteTrashSuccess(entity);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }
    }

    @Override
    public void sendOperationTrash() {
        apiService.operationTrash(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFail(int code,String msg) {

                    }
                });
    }

    @Override
    public void getTop3List(int type) {
        if(type == 0){//text
            apiService.getTextTop3()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<TrashEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<TrashEntity> entities) {
                            view.onTop3LoadSuccess(entities);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onTop3LoadFail(code,msg);
                        }
                    });
        }else if(type == 1){//img
            apiService.getImageTop3()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<TrashEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<TrashEntity> entities) {
                            view.onTop3LoadSuccess(entities);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onTop3LoadFail(code,msg);
                        }
                    });
        }
    }

    @Override
    public void getTrashDetail(String type, String id) {
        if(type.equals("text")){//text
//            apiService.getTextTrashDetail(id)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new NetResultSubscriber<TrashEntity>() {
//                        @Override
//                        public void onSuccess(TrashEntity entity) {
//                            view.onLoadDetailSuccess(entity);
//                        }
//
//                        @Override
//                        public void onFail(int code,String msg) {
//                            view.onFailure(code,msg);
//                        }
//                    });
            apiService.getTextTrashTags(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<DocTagEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<DocTagEntity> entities) {
                            view.onLoadDetailSuccess(entities);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }else if(type.equals("image")){//img
//            apiService.getImageTrashDetail(id)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new NetResultSubscriber<TrashEntity>() {
//                        @Override
//                        public void onSuccess(TrashEntity entity) {
//                            view.onLoadDetailSuccess(entity);
//                        }
//
//                        @Override
//                        public void onFail(int code,String msg) {
//                            view.onFailure(code,msg);
//                        }
//                    });
            apiService.getImgTrashTags(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<DocTagEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<DocTagEntity> entities) {
                            view.onLoadDetailSuccess(entities);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }
    }

    @Override
    public void likeTrashTag(TagLikeEntity entity, boolean isLike, final int position) {
        if(!isLike){
            apiService.likeTrashTag(entity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            view.onLikeTag(true,position);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }else {
            apiService.dislikeTrashTag(entity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            view.onLikeTag(false,position);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            view.onFailure(code,msg);
                        }
                    });
        }
    }

    @Override
    public void createTag(TagSendEntity entity) {
        apiService.createTrashTag(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<String>() {
                    @Override
                    public void onSuccess(String s) {
                        view.onCreateTag(s);
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        view.onFailure(code,msg);
                    }
                });
    }
}
