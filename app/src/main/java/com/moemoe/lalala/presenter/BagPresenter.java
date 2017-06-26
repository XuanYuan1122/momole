package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.ApiResult;
import com.moemoe.lalala.model.entity.BagDirEntity;
import com.moemoe.lalala.model.entity.BagEntity;
import com.moemoe.lalala.model.entity.BagFolderInfo;
import com.moemoe.lalala.model.entity.BagModifyEntity;
import com.moemoe.lalala.model.entity.BookInfo;
import com.moemoe.lalala.model.entity.FileEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.NewUploadEntity;
import com.moemoe.lalala.model.entity.UploadEntity;
import com.moemoe.lalala.model.entity.UploadResultEntity;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.MusicLoader;
import com.moemoe.lalala.utils.StringUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

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
                    .flatMap(new Func1<ApiResult<UploadEntity>, Observable<Image>>() {
                        @Override
                        public Observable<Image> call(final ApiResult<UploadEntity> uploadEntityApiResult) {
                            final File file = new File(image.getPath());
                            final UploadManager uploadManager = new UploadManager();
                            return Observable.create(new Observable.OnSubscribe<Image>() {
                                @Override
                                public void call(final Subscriber<? super Image> subscriber) {
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
                                                    subscriber.onNext(image);
                                                    subscriber.onCompleted();
                                                } else {
                                                    subscriber.onError(null);
                                                }
                                            }
                                        }, null);
                                    }catch (Exception e){
                                        subscriber.onError(e);
                                    }
                                }
                            });
                        }
                    })
                    .observeOn(Schedulers.io())
                    .flatMap(new Func1<Image, Observable<ApiResult>>() {
                        @Override
                        public Observable<ApiResult> call(Image image) {
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
    public void getFolderList(String userId, final int index) {
        apiService.getFolderList(userId,ApiService.LENGHT,index)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<BagDirEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<BagDirEntity> entities) {
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
        final ArrayList<NewUploadEntity> entities = new ArrayList<>();
        final ArrayList<Integer> range = new ArrayList<>();
        final ArrayList<UploadResultEntity> resList = new ArrayList<>();
        final BagFolderInfo resFolder = new BagFolderInfo();
        entities.add(new NewUploadEntity(StringUtils.getFileMD5(new File(cover.getPath())),FileUtil.getExtensionName(cover.getPath())));
        range.add(0);
        for(Object o : items){
            if (o instanceof Image){
                entities.add(new NewUploadEntity(StringUtils.getFileMD5(new File(((Image) o).getPath())),FileUtil.getExtensionName(((Image) o).getPath())));
            }else if(o instanceof MusicLoader.MusicInfo){
                MusicLoader.MusicInfo info = (MusicLoader.MusicInfo) o;
                entities.add(new NewUploadEntity(StringUtils.getFileMD5(new File(info.getUrl())),FileUtil.getExtensionName(info.getUrl())));
            }else if(o instanceof BookInfo){
                BookInfo entity = (BookInfo) o;
                entities.add(new NewUploadEntity(StringUtils.getFileMD5(new File(entity.getPath())),FileUtil.getExtensionName(entity.getPath())));
            }
            range.add(range.size());
        }
        apiService.checkMd5(entities)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .concatMap(new Func1<ApiResult<ArrayList<UploadResultEntity>>, Observable<UploadResultEntity>>() {
                    @Override
                    public Observable<UploadResultEntity> call(ApiResult<ArrayList<UploadResultEntity>> arrayListApiResult) {
                        return Observable.zip(
                                Observable.from(range),
                                Observable.from(arrayListApiResult.getData()),
                                new Func2<Integer, UploadResultEntity, UploadResultEntity>() {
                                    @Override
                                    public UploadResultEntity call(Integer integer, final UploadResultEntity uploadResultEntity) {
                                        if(integer == 0){
                                            uploadResultEntity.setType("cover");
                                            uploadResultEntity.setFilePath(cover.getPath());
                                        }else {
                                            Object o = items.get(integer - 1);
                                            if(o instanceof Image){
                                                uploadResultEntity.setFilePath(((Image) o).getPath());
                                                uploadResultEntity.setType("image");
                                            }else if(o instanceof MusicLoader.MusicInfo){
                                                uploadResultEntity.setFilePath(((MusicLoader.MusicInfo) o).getUrl());
                                                uploadResultEntity.setType("music");
                                                uploadResultEntity.setMusicTime(((MusicLoader.MusicInfo) o).getDuration());
                                            }else if(o instanceof BookInfo){
                                                uploadResultEntity.setFilePath(((BookInfo) o).getPath());
                                                uploadResultEntity.setType("txt");
                                            }
                                        }
                                        return uploadResultEntity;
                                    }
                                }
                        );
                    }
                })
                .observeOn(Schedulers.io())
                .concatMap(new Func1<UploadResultEntity, Observable<UploadResultEntity>>() {
                    @Override
                    public Observable<UploadResultEntity> call(final UploadResultEntity uploadResultEntity) {
                        final File file = new File(uploadResultEntity.getFilePath());
                        final UploadManager uploadManager = new UploadManager();
                        return Observable.create(new Observable.OnSubscribe<UploadResultEntity>() {
                            @Override
                            public void call(final Subscriber<? super UploadResultEntity> subscriber) {
                                final UploadResultEntity entity = new UploadResultEntity();
                                if(!uploadResultEntity.isSave()){
                                    try {
                                        uploadManager.put(file,uploadResultEntity.getPath(), uploadResultEntity.getUploadToken(), new UpCompletionHandler() {
                                            @Override
                                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                                if (info.isOK()) {
                                                    entity.setFileName(file.getName());
                                                    entity.setMd5(uploadResultEntity.getMd5());
                                                    entity.setPath(uploadResultEntity.getPath());
                                                    entity.setSave(uploadResultEntity.isSave());
                                                    entity.setSize(file.length());
                                                    entity.setType(uploadResultEntity.getType());
                                                    if(uploadResultEntity.getType().equals("image")){
                                                        try {
                                                            String attr = "{\"h\":" + response.getInt("h") + ",\"w\":" + response.getInt("w") + "}";
                                                            entity.setAttr(attr);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }else if(uploadResultEntity.getType().equals("music")){
                                                        String attr = "{\"timestamp\":" + uploadResultEntity.getMusicTime() + "}";
                                                        entity.setAttr(attr);
                                                    }else if(uploadResultEntity.getType().equals("txt")){
                                                        String attr = "{\"size\":"+ file.length() +"}";
                                                        entity.setAttr(attr);
                                                    }
                                                    subscriber.onNext(entity);
                                                    subscriber.onCompleted();
                                                } else {
                                                    subscriber.onError(null);
                                                }
                                            }
                                        }, null);
                                    }catch (Exception e){
                                        subscriber.onError(e);
                                    }
                                }else {
                                    entity.setAttr(uploadResultEntity.getAttr());
                                    entity.setFileName(file.getName());
                                    entity.setMd5(uploadResultEntity.getMd5());
                                    entity.setPath(uploadResultEntity.getPath());
                                    entity.setSave(uploadResultEntity.isSave());
                                    entity.setSize(uploadResultEntity.getSize());
                                    entity.setType(uploadResultEntity.getType());
                                    subscriber.onNext(entity);
                                    subscriber.onCompleted();
                                }
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UploadResultEntity>() {
                    @Override
                    public void onCompleted() {
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
                    public void onError(Throwable e) {
                        if(view != null) view.onFailure(-1,"");
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
            final ArrayList<NewUploadEntity> entities = new ArrayList<>();
            entities.add(new NewUploadEntity(StringUtils.getFileMD5(new File(cover.getPath())),FileUtil.getExtensionName(cover.getPath())));
            final BagFolderInfo.FolderInfo info = new BagFolderInfo.FolderInfo();
            apiService.checkMd5(entities)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMap(new Func1<ApiResult<ArrayList<UploadResultEntity>>, Observable<UploadResultEntity>>() {
                        @Override
                        public Observable<UploadResultEntity> call(final ApiResult<ArrayList<UploadResultEntity>> arrayListApiResult) {
                            final UploadResultEntity uploadResultEntity = arrayListApiResult.getData().get(0);
                            final File file = new File(cover.getPath());
                            final UploadManager uploadManager = new UploadManager();
                            return Observable.create(new Observable.OnSubscribe<UploadResultEntity>() {
                                @Override
                                public void call(final Subscriber<? super UploadResultEntity> subscriber) {
                                    final UploadResultEntity entity = new UploadResultEntity();
                                    if(!uploadResultEntity.isSave()){
                                        try {
                                            uploadManager.put(file,uploadResultEntity.getPath(), uploadResultEntity.getUploadToken(), new UpCompletionHandler() {
                                                @Override
                                                public void complete(String key, ResponseInfo info, JSONObject response) {
                                                    if (info.isOK()) {
                                                        entity.setFileName(file.getName());
                                                        entity.setMd5(uploadResultEntity.getMd5());
                                                        entity.setPath(uploadResultEntity.getPath());
                                                        entity.setSave(uploadResultEntity.isSave());
                                                        entity.setSize(file.length());
                                                        entity.setType(uploadResultEntity.getType());
                                                        subscriber.onNext(entity);
                                                        subscriber.onCompleted();
                                                    } else {
                                                        subscriber.onError(null);
                                                    }
                                                }
                                            }, null);
                                        }catch (Exception e){
                                            subscriber.onError(e);
                                        }
                                    }else {
                                        entity.setAttr(uploadResultEntity.getAttr());
                                        entity.setFileName(file.getName());
                                        entity.setMd5(uploadResultEntity.getMd5());
                                        entity.setPath(uploadResultEntity.getPath());
                                        entity.setSave(uploadResultEntity.isSave());
                                        entity.setSize(uploadResultEntity.getSize());
                                        entity.setType(uploadResultEntity.getType());
                                        subscriber.onNext(entity);
                                        subscriber.onCompleted();
                                    }
                                }
                            });
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<UploadResultEntity>() {
                        @Override
                        public void onCompleted() {
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
                        public void onError(Throwable e) {
                            if(view != null) view.onFailure(-1,"");
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
        final ArrayList<NewUploadEntity> entities = new ArrayList<>();
        final ArrayList<UploadResultEntity> resList = new ArrayList<>();
        for(Object o : items){
            if (o instanceof Image){
                entities.add(new NewUploadEntity(StringUtils.getFileMD5(new File(((Image) o).getPath())),FileUtil.getExtensionName(((Image) o).getPath())));
            }else if(o instanceof MusicLoader.MusicInfo){
                MusicLoader.MusicInfo info = (MusicLoader.MusicInfo) o;
                entities.add(new NewUploadEntity(StringUtils.getFileMD5(new File(info.getUrl())),FileUtil.getExtensionName(info.getUrl())));
            }else if(o instanceof BookInfo){
                BookInfo entity = (BookInfo) o;
                entities.add(new NewUploadEntity(StringUtils.getFileMD5(new File(entity.getPath())),FileUtil.getExtensionName(entity.getPath())));
            }
        }
        apiService.checkMd5(entities)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .concatMap(new Func1<ApiResult<ArrayList<UploadResultEntity>>, Observable<UploadResultEntity>>() {
                    @Override
                    public Observable<UploadResultEntity> call(ApiResult<ArrayList<UploadResultEntity>> arrayListApiResult) {
                        return Observable.zip(
                                Observable.from(items),
                                Observable.from(arrayListApiResult.getData()),
                                new Func2<Object, UploadResultEntity, UploadResultEntity>() {
                                    @Override
                                    public UploadResultEntity call(Object o, final UploadResultEntity uploadResultEntity) {
                                        if(o instanceof Image){
                                            uploadResultEntity.setFilePath(((Image) o).getPath());
                                            uploadResultEntity.setType("image");
                                        }else if(o instanceof MusicLoader.MusicInfo){
                                            uploadResultEntity.setFilePath(((MusicLoader.MusicInfo) o).getUrl());
                                            uploadResultEntity.setType("music");
                                            uploadResultEntity.setMusicTime(((MusicLoader.MusicInfo) o).getDuration());
                                        }else if(o instanceof BookInfo){
                                            uploadResultEntity.setFilePath(((BookInfo) o).getPath());
                                            uploadResultEntity.setType("txt");
                                        }
                                        return uploadResultEntity;
                                    }
                                }
                        );
                    }
                })
                .observeOn(Schedulers.io())
                .concatMap(new Func1<UploadResultEntity, Observable<UploadResultEntity>>() {
                    @Override
                    public Observable<UploadResultEntity> call(final UploadResultEntity uploadResultEntity) {
                        final File file = new File(uploadResultEntity.getFilePath());
                        final UploadManager uploadManager = new UploadManager();
                        return Observable.create(new Observable.OnSubscribe<UploadResultEntity>() {
                            @Override
                            public void call(final Subscriber<? super UploadResultEntity> subscriber) {
                                final UploadResultEntity entity = new UploadResultEntity();
                                if(!uploadResultEntity.isSave()){
                                    try {
                                        uploadManager.put(file,uploadResultEntity.getPath(), uploadResultEntity.getUploadToken(), new UpCompletionHandler() {
                                            @Override
                                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                                if (info.isOK()) {
                                                    entity.setFileName(file.getName());
                                                    entity.setMd5(uploadResultEntity.getMd5());
                                                    entity.setPath(uploadResultEntity.getPath());
                                                    entity.setSave(uploadResultEntity.isSave());
                                                    entity.setSize(file.length());
                                                    entity.setType(uploadResultEntity.getType());
                                                    if(uploadResultEntity.getType().equals("image")){
                                                        try {
                                                            String attr = "{\"h\":" + response.getInt("h") + ",\"w\":" + response.getInt("w") + "}";
                                                            entity.setAttr(attr);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }else if(uploadResultEntity.getType().equals("music")){
                                                        String attr = "{\"timestamp\":" + uploadResultEntity.getMusicTime() + "}";
                                                        entity.setAttr(attr);
                                                    }else if(uploadResultEntity.getType().equals("txt")){
                                                        String attr = "{\"size\":"+ file.length() +"}";
                                                        entity.setAttr(attr);
                                                    }
                                                    subscriber.onNext(entity);
                                                    subscriber.onCompleted();
                                                } else {
                                                    subscriber.onError(null);
                                                }
                                            }
                                        }, null);
                                    }catch (Exception e){
                                        subscriber.onError(e);
                                    }
                                }else {
                                    entity.setAttr(uploadResultEntity.getAttr());
                                    entity.setFileName(file.getName());
                                    entity.setMd5(uploadResultEntity.getMd5());
                                    entity.setPath(uploadResultEntity.getPath());
                                    entity.setSave(uploadResultEntity.isSave());
                                    entity.setSize(uploadResultEntity.getSize());
                                    entity.setType(uploadResultEntity.getType());
                                    subscriber.onNext(entity);
                                    subscriber.onCompleted();
                                }
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UploadResultEntity>() {
                    @Override
                    public void onCompleted() {
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
                    public void onError(Throwable e) {
                        if(view != null) view.onFailure(-1,"");
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
