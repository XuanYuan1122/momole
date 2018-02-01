package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.FeedFollowType1Entity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.model.entity.TagFileDelRequest;
import com.moemoe.lalala.model.entity.UserFollowTagEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 *
 * Created by yi on 2018/1/11.
 */

public class FeedShowAllPresenter implements FeedShowAllContract.Presenter {

    private FeedShowAllContract.View view;
    private ApiService apiService;

    @Inject
    public FeedShowAllPresenter(FeedShowAllContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadList(String type, String id, final int index) {
        if(type.equals(FolderType.MOVIE.toString())){
            apiService.loadTagMoiveList(id,index,ApiService.LENGHT)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<ShowFolderEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<ShowFolderEntity> entities) {
                            if(view != null) view.onLoadListSuccess(entities,index == 0);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }else if(type.equals(FolderType.MH.toString())){
            apiService.loadTagFolderList(id,index,ApiService.LENGHT)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<ShowFolderEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<ShowFolderEntity> entities) {
                            if(view != null) view.onLoadListSuccess(entities,index == 0);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }else if(type.equals(FolderType.WZ.toString())){
            apiService.loadTagArticleList(id,index,ApiService.LENGHT)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<FeedFollowType1Entity>>() {
                        @Override
                        public void onSuccess(ArrayList<FeedFollowType1Entity> entities) {
                            if(view != null) view.onLoadList2Success(entities,index == 0);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }else if(type.equals(FolderType.MUSIC.toString())){
            apiService.loadTagMusicList(id,index,ApiService.LENGHT)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<FeedFollowType1Entity>>() {
                        @Override
                        public void onSuccess(ArrayList<FeedFollowType1Entity> entities) {
                            if(view != null) view.onLoadList2Success(entities,index == 0);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }
    }

    @Override
    public void delFile(String type, TagFileDelRequest request) {
        if(FolderType.MOVIE.toString().equals(type) || FolderType.MUSIC.toString().equals(type)){
            apiService.delTagFile(type,request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onDelFileSuccess();
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }else {
            apiService.delTagFolder(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onDelFileSuccess();
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }
    }
}
