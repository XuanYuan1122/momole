package com.moemoe.lalala.presenter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.ApiResult;
import com.moemoe.lalala.model.entity.CommonFileEntity;
import com.moemoe.lalala.model.entity.FileXiaoShuoEntity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ManHua2Entity;
import com.moemoe.lalala.model.entity.NewFolderEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class NewFolderItemPresenter implements NewFolderItemContract.Presenter {

    private NewFolderItemContract.View view;
    private ApiService apiService;

    @Inject
    public NewFolderItemPresenter(NewFolderItemContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }


    @Override
    public void loadFolderInfo(String userId, String type, String folderId) {
        apiService.loadFolderInfo(userId,type,folderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<NewFolderEntity>() {
                    @Override
                    public void onSuccess(NewFolderEntity newFolderEntity) {
                        if(view != null) view.onLoadFolderSuccess(newFolderEntity);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void loadFileList(String userId, String type, String folderId, final int index) {
        if(type.equals(FolderType.MH.toString())){
            apiService.loadFiManHua2List(userId,folderId,ApiService.LENGHT,index)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<ManHua2Entity>>() {
                        @Override
                        public void onSuccess(ArrayList<ManHua2Entity> manHua2Entities) {
                            if(view != null) view.onLoadManHua2ListSuccess(manHua2Entities,index == 0);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }else {
            if(type.equals(FolderType.MHD.toString())){
                type = FolderType.MH.toString();
            }
            final String finalType = type;
            apiService.loadFileList(userId,type,folderId,ApiService.LENGHT,index)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<JsonArray>() {
                        @Override
                        public void onSuccess(JsonArray res) {
                            if(view != null) {
                                if(finalType.equals(FolderType.XS.toString())){
                                    Gson gson = new Gson();
                                    ArrayList<FileXiaoShuoEntity> list = gson.fromJson(res,new TypeToken<ArrayList<FileXiaoShuoEntity>>(){}.getType());
                                    if(view!=null)view.onLoadFileListSuccess(list, index == 0);
                                }else {
                                    Gson gson = new Gson();
                                    ArrayList<CommonFileEntity> list = gson.fromJson(res,new TypeToken<ArrayList<CommonFileEntity>>(){}.getType());
                                    if(view!=null)view.onLoadFileListSuccess(list, index == 0);
                                }
                            }
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }
    }

    @Override
    public void deleteFiles(ArrayList<String> ids, String type, String folderId,String parentId) {
        if(type.equals(FolderType.MH.toString())){
            apiService.deleteManHua2(ids,folderId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onDeleteFilesSuccess();
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }else {
            if(type.equals(FolderType.MHD.toString())){
                apiService.deleteManHuaFile(parentId,folderId,ids)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetSimpleResultSubscriber() {
                            @Override
                            public void onSuccess() {
                                if(view != null) view.onDeleteFilesSuccess();
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                if(view != null) view.onFailure(code, msg);
                            }
                        });
            }else {
                apiService.deleteFiles(ids,type,folderId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetSimpleResultSubscriber() {
                            @Override
                            public void onSuccess() {
                                if(view != null) view.onDeleteFilesSuccess();
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
    public void topFile(String folderId, String type, String fileId) {
        if(type.equals(FolderType.MH.toString())){
            apiService.topManHua2(folderId,fileId)  .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onTopFileSuccess();
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });

        }else {
            apiService.topFile(folderId,type,fileId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onTopFileSuccess();
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }
    }

    @Override
    public void followFolder(String userId, String type, String folderId) {
        apiService.followFolder(userId,type,folderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onFollowFolderSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void removeFollowFolder(String userId, String type, String folderId) {
        apiService.removeFollowFolder(userId,type,folderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onFollowFolderSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void buyFolder(String userId, String type, String folderId) {
        apiService.buyFolder(userId,type,folderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onBuyFolderSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void followUser(String id, boolean isFollow) {
        if (!isFollow){
            apiService.followUser(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onFollowSuccess(true);
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
                            if(view != null) view.onFollowSuccess(false);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }
    }

    @Override
    public void refreshRecommend(String folderName, int page, String excludeFolderId) {
        apiService.loadRefreshList(excludeFolderId,folderName,page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<ShowFolderEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<ShowFolderEntity> entities) {
                        if(view != null) view.onReFreshSuccess(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }
}
