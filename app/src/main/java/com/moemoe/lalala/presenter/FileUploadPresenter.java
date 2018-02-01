package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ManHuaUploadEntity;
import com.moemoe.lalala.model.entity.UploadResultEntity;
import com.moemoe.lalala.utils.Utils;
import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 *
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
    public void uploadFiles(final String folderType,
                            final String folderId,
                            final String parentFolderId,
                            final String name,
                            final ArrayList<Object> items,
                            final String cover,
                            final int coverSize,
                            final int coin,
                            final String desc,
                            final ArrayList<String> tags) {
        final ArrayList<UploadResultEntity> resList = new ArrayList<>();
        Utils.uploadFiles(apiService,items,cover,coverSize,folderType,name,new Observer<UploadResultEntity>() {

            @Override
            public void onError(Throwable e) {
                if(view != null) view.onFailure(-1,"");
            }

            @Override
            public void onComplete() {

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
                }else if(folderType.equals(FolderType.SP.toString())){
                    String path = resList.get(0).getPath();
                    int size = (int) resList.get(0).getSize();
                    resList.remove(0);
                    UploadResultEntity entity = resList.get(0);
                    entity.setCover(path);
                    entity.setCoverSize(size);
                    entity.setCoin(coin);
                    entity.setSummary(desc);
                    entity.setTexts(tags);
                    apiService.uploadShipin(folderId,entity)
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
                }else if(folderType.equals(FolderType.YY.toString())){
                    String path = resList.get(0).getPath();
                    int size = (int) resList.get(0).getSize();
                    resList.remove(0);
                    for(UploadResultEntity entity : resList){
                        entity.setCover(path);
                        entity.setCoverSize(size);
                        entity.setCoin(coin);
                        entity.setTexts(tags);
                    }
                    apiService.uploadYinyue(folderId,resList)
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
            public void onSubscribe(@NonNull Disposable d) {

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
