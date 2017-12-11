package com.moemoe.lalala.utils;

import android.content.Context;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.MapDbEntity;
import com.moemoe.lalala.model.entity.SplashEntity;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yi on 2017/11/23.
 */

public class SplashUtils {

    public static void updateSplash(ArrayList<SplashEntity> entities){
        ArrayList<SplashEntity> tmp = (ArrayList<SplashEntity>) GreenDaoManager.getInstance().getSession().getSplashEntityDao().loadAll();
        if(tmp != null && tmp.size() == entities.size()){
            ArrayList<SplashEntity> inList = new ArrayList<>();
            for(SplashEntity entity : entities){
                for(SplashEntity entity1 : tmp){
                    if(entity1.getId().equals(entity.getId()) && FileUtil.isExists(StorageUtils.getSplashRootPath() + entity1.getImagePath().substring(entity1.getImagePath().lastIndexOf("/") + 1))){
                        inList.add(entity);
                    }
                }
            }
            GreenDaoManager.getInstance().getSession().getSplashEntityDao().deleteAll();
            GreenDaoManager.getInstance().getSession().getSplashEntityDao().insertOrReplaceInTx(inList);
            entities.removeAll(inList);
            if(entities.size() == 0){
                return;
            }
//            boolean  needDown = false;
//            for(SplashEntity entity : entities){
//                boolean isExit = false;
//                for(SplashEntity entity1 : tmp){
//                    if(entity1.getId().equals(entity.getId())){
//                        isExit = true;
//                        break;
//                    }
//                }
//                if(!isExit){
//                    needDown = true;
//                    break;
//                }
//            }
//            if(!needDown){
//                return;
//            }
        }
        GreenDaoManager.getInstance().getSession().getSplashEntityDao().deleteAll();
        FileUtil.deletFileInDir(new File(StorageUtils.getSplashRootPath()));
        Observable.fromIterable(entities)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<SplashEntity, ObservableSource<SplashEntity>>() {
                    @Override
                    public ObservableSource<SplashEntity> apply(@NonNull final SplashEntity mapDbEntity) throws Exception {
                        return  Observable.create(new ObservableOnSubscribe<SplashEntity>() {
                            @Override
                            public void subscribe(@NonNull final ObservableEmitter<SplashEntity> res) throws Exception {
                                FileDownloader.getImpl().create(ApiService.URL_QINIU + mapDbEntity.getImagePath())
                                        .setPath(StorageUtils.getSplashRootPath() + mapDbEntity.getImagePath().substring(mapDbEntity.getImagePath().lastIndexOf("/") + 1))
                                        .setCallbackProgressTimes(1)
                                        .setListener(new FileDownloadListener() {
                                            @Override
                                            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                            }

                                            @Override
                                            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                            }

                                            @Override
                                            protected void completed(BaseDownloadTask task) {
                                                GreenDaoManager.getInstance().getSession().getSplashEntityDao().insertOrReplace(mapDbEntity);
                                                res.onNext(mapDbEntity);
                                                res.onComplete();
                                            }

                                            @Override
                                            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                            }

                                            @Override
                                            protected void error(BaseDownloadTask task, Throwable e) {
                                                res.onNext(mapDbEntity);
                                                res.onComplete();
                                            }

                                            @Override
                                            protected void warn(BaseDownloadTask task) {

                                            }
                                        }).start();
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}
