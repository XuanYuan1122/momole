package com.moemoe.lalala.presenter;

import android.text.TextUtils;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.ApiResult;
import com.moemoe.lalala.model.entity.BagFolderInfo;
import com.moemoe.lalala.model.entity.BookInfo;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.ManHuaUploadEntity;
import com.moemoe.lalala.model.entity.NewUploadEntity;
import com.moemoe.lalala.model.entity.UploadResultEntity;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.MusicLoader;
import com.moemoe.lalala.utils.StringUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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

public class FileUploadPresenter implements FileUploadContract.Presenter {

    private FileUploadContract.View view;
    private ApiService apiService;

    @Inject
    public FileUploadPresenter(FileUploadContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
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
    public void uploadFiles(final String folderType, final String folderId, final String parentFolderId, final String name, final ArrayList<Object> items, final String cover, final int coverSize) {
        final ArrayList<NewUploadEntity> entities = new ArrayList<>();
        final ArrayList<Integer> range = new ArrayList<>();
        final ArrayList<UploadResultEntity> resList = new ArrayList<>();
        if(!TextUtils.isEmpty(cover) && coverSize != -1){
            entities.add(new NewUploadEntity(StringUtils.getFileMD5(new File(cover)), FileUtil.getExtensionName(cover)));
            range.add(0);
        }
        for(Object o : items){
            if (o instanceof String){
                entities.add(new NewUploadEntity(StringUtils.getFileMD5(new File((String) o)),FileUtil.getExtensionName((String) o)));
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
                                        if(integer == 0 && !TextUtils.isEmpty(cover) && coverSize != -1){
                                            uploadResultEntity.setType("cover");
                                            uploadResultEntity.setFilePath(cover);
                                        }else {
                                            Object o ;
                                            if(!TextUtils.isEmpty(cover) && coverSize != -1){
                                                o = items.get(integer - 1);
                                            }else {
                                                o = items.get(integer);
                                            }
                                            if(o instanceof String){
                                                uploadResultEntity.setFilePath((String) o);
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
                                                        if(folderType.equals(FolderType.XS.toString())){
                                                            entity.setNum((int)file.length());
                                                            entity.setTitle(name);
                                                            String content = FileUtil.readFileToString(file);
                                                            if(content.length() > 100){
                                                                content = content.substring(0,100);
                                                            }
                                                            entity.setContent(content);
                                                        }
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
                        if(folderType.equals(FolderType.XS.toString())){
                            if(resList.size() == 2){
                                UploadResultEntity finalRes = resList.get(1);
                                finalRes.setCover(resList.get(0).getPath());
                                finalRes.setCoverSize((int) resList.get(0).getSize());
                                resList.clear();
                                resList.add(finalRes);
                                apiService.uploadXiaoshuo(folderId,resList)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new NetSimpleResultSubscriber() {
                                            @Override
                                            public void onSuccess() {
                                                if(view != null) view.onUploadFilesSuccess();
                                            }

                                            @Override
                                            public void onFail(int code, String msg) {
                                                if(view != null) view.onFailure(code,msg);
                                            }
                                        });
                            }else {
                                if(view != null) view.onFailure(-1,"");
                            }
                        }else if(folderType.equals(FolderType.MH.toString())){
                            String path = resList.get(0).getPath();
                            int size = (int) resList.get(0).getSize();
                            resList.remove(0);
                            ManHuaUploadEntity entity = new ManHuaUploadEntity(path,size,resList,name);
                            apiService.uploadManhua(parentFolderId,entity)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new NetSimpleResultSubscriber() {
                                        @Override
                                        public void onSuccess() {
                                            if(view != null) view.onUploadFilesSuccess();
                                        }

                                        @Override
                                        public void onFail(int code, String msg) {
                                            if(view != null) view.onFailure(code,msg);
                                        }
                                    });
                        }else if(folderType.equals(FolderType.ZH.toString())){
                            apiService.uploadZonghe(folderId,resList)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new NetSimpleResultSubscriber() {
                                        @Override
                                        public void onSuccess() {
                                            if(view != null) view.onUploadFilesSuccess();
                                        }

                                        @Override
                                        public void onFail(int code, String msg) {
                                            if(view != null) view.onFailure(code,msg);
                                        }
                                    });
                        }else if(folderType.equals(FolderType.TJ.toString())){
                            apiService.uploadTuji(folderId,resList)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new NetSimpleResultSubscriber() {
                                        @Override
                                        public void onSuccess() {
                                            if(view != null) view.onUploadFilesSuccess();
                                        }

                                        @Override
                                        public void onFail(int code, String msg) {
                                            if(view != null) view.onFailure(code,msg);
                                        }
                                    });
                        }else if(folderType.equals(FolderType.MHD.toString())){
                            String path = resList.get(0).getPath();
                            int size = (int) resList.get(0).getSize();
                            if(coverSize != -1){
                                resList.remove(0);
                            }else {
                                size = -1;
                                path = cover;
                            }
                            ManHuaUploadEntity entity = new ManHuaUploadEntity(path,size,resList,name);
                            apiService.uploadManhua2(parentFolderId,folderId,entity)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new NetSimpleResultSubscriber() {
                                        @Override
                                        public void onSuccess() {
                                            if(view != null) view.onUploadFilesSuccess();
                                        }

                                        @Override
                                        public void onFail(int code, String msg) {
                                            if(view != null) view.onFailure(code,msg);
                                        }
                                    });
                        }
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
    public void createMh(String folderId, String parentFolderId, String name, ArrayList<Object> items) {

    }

}
