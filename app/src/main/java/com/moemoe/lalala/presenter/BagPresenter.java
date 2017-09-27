package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.ApiResult;
import com.moemoe.lalala.model.entity.BagDirEntity;
import com.moemoe.lalala.model.entity.BagEntity;
import com.moemoe.lalala.model.entity.BagFolderInfo;
import com.moemoe.lalala.model.entity.BagModifyEntity;
import com.moemoe.lalala.model.entity.FileEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.model.entity.UploadEntity;
import com.moemoe.lalala.model.entity.UploadResultEntity;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.Utils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class BagPresenter implements BagContract.Presenter {

    private BagContract.View view;
    private ApiService apiService;

    @Inject
    public BagPresenter(BagContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void openBag(final String name, final Image image, final int type) {
        String suffix = FileUtil.getExtensionName(image.getPath());
        if(!image.getPath().startsWith("/") && type == 1){
            BagModifyEntity entity = new BagModifyEntity(image.getPath(),name);
            apiService.updateBag(entity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.openOrModifyBagSuccess();
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }else {
            apiService.requestQnFileKey(suffix)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMap(new Function<ApiResult<UploadEntity>, ObservableSource<Image>>() {
                        @Override
                        public ObservableSource<Image> apply(@NonNull final ApiResult<UploadEntity> uploadEntityApiResult) throws Exception {
                            final File file = new File(image.getPath());
                            final UploadManager uploadManager = new UploadManager();
                            return Observable.create(new ObservableOnSubscribe<Image>() {
                                @Override
                                public void subscribe(@NonNull final ObservableEmitter<Image> res) throws Exception {
                                    try {
                                        uploadManager.put(file,uploadEntityApiResult.getData().getFilePath(), uploadEntityApiResult.getData().getUploadToken(), new UpCompletionHandler() {
                                            @Override
                                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                                if (info.isOK()) {
                                                    Image image = new Image();
                                                    image.setPath(key);
                                                    try {
                                                        image.setH(response.getInt("h"));
                                                        image.setW(response.getInt("w"));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    res.onNext(image);
                                                    res.onComplete();
                                                } else {
                                                    res.onError(null);
                                                }
                                            }
                                        }, null);
                                    }catch (Exception e){
                                        res.onError(e);
                                    }
                                }
                            });
                        }
                    })
                    .observeOn(Schedulers.io())
                    .flatMap(new Function<Image, ObservableSource<ApiResult>>() {
                        @Override
                        public ObservableSource<ApiResult> apply(@NonNull Image image) throws Exception {
                            BagModifyEntity entity = new BagModifyEntity(image.getPath(),name);
                            if(type == 0){
                                return apiService.openBag(entity);
                            }else {
                                return apiService.updateBag(entity);
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.openOrModifyBagSuccess();
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }
    }

    @Override
    public void getBagInfo(String userId) {
        apiService.getBagInfo(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<BagEntity>() {
                    @Override
                    public void onSuccess(BagEntity entity) {
                        if(view != null) view.loadBagInfoSuccess(entity);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void getFolderList(String userId, final int index,String type) {
        apiService.loadFolderList(userId,type,ApiService.LENGHT,index)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<ShowFolderEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<ShowFolderEntity> entities) {
                        if(view != null) view.loadFolderListSuccess(entities,index == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void createFolder(final String folderName, final int coin, final Image cover, final ArrayList<Object> items, final String readType) {
        final ArrayList<UploadResultEntity> resList = new ArrayList<>();
        final BagFolderInfo resFolder = new BagFolderInfo();
        Utils.uploadFiles(apiService,items,cover.getPath(),0,"","",new Observer<UploadResultEntity>() {
            @Override
            public void onError(Throwable e) {
                if(view != null) view.onFailure(-1,"");
            }

            @Override
            public void onComplete() {
                resFolder.setFiles(resList);
                apiService.createFolder(resFolder)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetSimpleResultSubscriber() {
                            @Override
                            public void onSuccess() {
                                if(view != null) view.createFolderSuccess();
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                if(view != null) view.onFailure(code, msg);
                            }
                        });
            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(UploadResultEntity uploadResultEntity) {
                if(uploadResultEntity.getType().equals("cover")){
                    resFolder.setFolderInfo(new BagFolderInfo.FolderInfo(coin,uploadResultEntity.getPath(),folderName,uploadResultEntity.getSize(),readType));
                }else {
                    resList.add(uploadResultEntity);
                }
            }
        });
    }

    @Override
    public void modifyFolder(final String folderId, final String folderName, final int coin, final Image cover, long size, final String readType) {
        if(!cover.getPath().startsWith("/")){
            BagFolderInfo.FolderInfo info = new BagFolderInfo.FolderInfo();
            info.size = size;
            info.coin = coin;
            info.cover = cover.getPath();
            info.name = folderName;
            info.readType = readType;
            apiService.modifyFolder(folderId,info)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.modifyFolderSuccess();
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }else {
            final BagFolderInfo.FolderInfo info = new BagFolderInfo.FolderInfo();
            Utils.uploadFile(apiService,cover.getPath(),new Observer<UploadResultEntity>() {
                @Override
                public void onError(Throwable e) {
                    if(view != null) view.onFailure(-1,"");
                }

                @Override
                public void onComplete() {
                    apiService.modifyFolder(folderId,info)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new NetSimpleResultSubscriber() {
                                @Override
                                public void onSuccess() {
                                    if(view != null) view.modifyFolderSuccess();
                                }

                                @Override
                                public void onFail(int code, String msg) {
                                    if(view != null) view.onFailure(code, msg);
                                }
                            });
                }

                @Override
                public void onSubscribe(@NonNull Disposable d) {

                }

                @Override
                public void onNext(UploadResultEntity uploadResultEntity) {
                    info.size = uploadResultEntity.getSize();
                    info.coin = coin;
                    info.cover = uploadResultEntity.getPath();
                    info.name = folderName;
                    info.readType = readType;
                }
            });
        }
    }

    @Override
    public void uploadFilesToFolder(final String folderId, final ArrayList<Object> items) {
        final ArrayList<UploadResultEntity> resList = new ArrayList<>();
        Utils.uploadFiles(apiService,items,"",-1,"","",new Observer<UploadResultEntity>() {

            @Override
            public void onError(Throwable e) {
                if(view != null) view.onFailure(-1,"");
            }

            @Override
            public void onComplete() {
                apiService.uploadFolder(folderId,resList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetSimpleResultSubscriber() {
                            @Override
                            public void onSuccess() {
                                if(view != null) view.uploadFolderSuccess();
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                if(view != null) view.onFailure(code, msg);
                            }
                        });
            }

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(UploadResultEntity uploadResultEntity) {
                resList.add(uploadResultEntity);
            }
        });
    }

    @Override
    public void getFolderItemList(String folderId, final int index) {
        apiService.getFolderItemList(folderId,index,ApiService.LENGHT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<FileEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<FileEntity> folderItemEntities) {
                        if(view != null) view.loadFolderItemListSuccess(folderItemEntities,index == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void checkSize(long size) {
        apiService.checkSize(size)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if(view != null) view.onCheckSize(aBoolean);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void buyFolder(String folderId) {
        apiService.buyFolder(folderId)
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
    public void deleteFolder(ArrayList<String> ids) {
        apiService.deleteFolders(ids)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.deleteFolderSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void followFolder(String folderId) {
        apiService.followFolder(folderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onFollowOrUnFollowFolderSuccess(true);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void unFollowFolder(String folderId) {
        ArrayList<String> id = new ArrayList<>();
        id.add(folderId);
        apiService.deleteBagFollowList(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onFollowOrUnFollowFolderSuccess(false);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void getFolder(String userId, String folderId) {
        apiService.getFolder(userId,folderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<BagDirEntity>() {
                    @Override
                    public void onSuccess(BagDirEntity entity) {
                        if(view != null) view.onLoadFolderSuccess(entity);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onLoadFolderFail();
                    }
                });
    }

    @Override
    public void release() {
        view = null;
    }
}
