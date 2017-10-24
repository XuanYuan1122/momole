package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.model.entity.WenZhangFolderEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class NewFolderPresenter implements NewFolderContract.Presenter {

    private NewFolderContract.View view;
    private ApiService apiService;

    @Inject
    public NewFolderPresenter(NewFolderContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadFolderList(String folderType, final int index, String userId,String type) {
        if(folderType.equals(FolderType.WZ.toString())){
            if(type.equals("my")){
                apiService.loadWenZhangList(userId,ApiService.LENGHT,index)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetResultSubscriber<ArrayList<WenZhangFolderEntity>>() {
                            @Override
                            public void onSuccess(ArrayList<WenZhangFolderEntity> wenZhangFolderEntities) {
                                if(view != null) view.onLoadFolderListSuccess(wenZhangFolderEntities, index == 0);
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                if(view != null) view.onFailure(code, msg);
                            }
                        });
            }else {
                apiService.loadWenZhangFollowList(userId,ApiService.LENGHT,index)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetResultSubscriber<ArrayList<WenZhangFolderEntity>>() {
                            @Override
                            public void onSuccess(ArrayList<WenZhangFolderEntity> wenZhangFolderEntities) {
                                if(view != null) view.onLoadFolderListSuccess(wenZhangFolderEntities, index == 0);
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                if(view != null) view.onFailure(code, msg);
                            }
                        });
            }

        }else {
            if(type.equals("my")){
                apiService.loadFolderList(userId,folderType,ApiService.LENGHT,index)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetResultSubscriber<ArrayList<ShowFolderEntity>>() {
                            @Override
                            public void onSuccess(ArrayList<ShowFolderEntity> entities) {
                                if(view != null) view.onLoadFolderListSuccess(entities, index == 0);
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                if(view != null) view.onFailure(code, msg);
                            }
                        });
            }else {
                apiService.loadFollowFolderList(folderType,ApiService.LENGHT,index)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetResultSubscriber<ArrayList<ShowFolderEntity>>() {
                            @Override
                            public void onSuccess(ArrayList<ShowFolderEntity> entities) {
                                if(view != null) view.onLoadFolderListSuccess(entities, index == 0);
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                if(view != null) view.onFailure(code, msg);
                            }
                        });
            }
        }
    }

    @Override
    public void deleteFolders(ArrayList<String> ids,String type) {
        apiService.deleteFolders(ids,type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onDeleteFoldersSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void topFolder(String folderId) {
        apiService.topFolder(folderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onTopFolderSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }
}
