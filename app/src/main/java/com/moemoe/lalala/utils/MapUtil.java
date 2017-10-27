package com.moemoe.lalala.utils;

import android.content.Context;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.greendao.gen.MapDbEntityDao;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetTResultSubscriber;
import com.moemoe.lalala.model.entity.MapDbEntity;
import com.moemoe.lalala.view.activity.ImageBigSelectActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
/**
 * Created by yi on 2017/10/10.
 */

public class MapUtil {

//    public static void checkAndDownload(Context context,ArrayList<MapDbEntity> entities,Observer<MapDbEntity> callback){//1.未下载 2.下载完成 3.下载失败
//        for(MapDbEntity entity : entities){
//            //文件是否存在
//            if(FileUtil.isExists(StorageUtils.getMapRootPath() + entity.getFileName())){
//                File file = new File(StorageUtils.getMapRootPath() + entity.getFileName());
//                String md5 = entity.getMd5();
//                if(md5.length() < 32){
//                    int n = 32 - md5.length();
//                    for(int i = 0;i < n;i++){
//                        md5 = "0" + md5;
//                    }
//                }
//                if(!md5.equals(StringUtils.getFileMD5(file))){
//                    FileUtil.deleteFile(StorageUtils.getMapRootPath() + entity.getFileName());
//                    entity.setDownloadState(1);
//                }else {
//                    entity.setDownloadState(2);
//                }
//            }else {
//                entity.setDownloadState(1);
//            }
//        }
//        //检查文件是否更改
//        final MapDbEntityDao dao = GreenDaoManager.getInstance().getSession().getMapDbEntityDao();
//        dao.deleteAll();
//        dao.insertOrReplaceInTx(entities);
//
//        //下载
//        downLoadFiles(context, entities,callback);
//    }

    public static void checkAndDownload(Context context,ArrayList<MapDbEntity> entities,Observer<MapDbEntity> callback){//1.未下载 2.下载完成 3.下载失败
        for(MapDbEntity entity : entities){
            //文件是否存在
            if(FileUtil.isExists(StorageUtils.getMapRootPath() + entity.getFileName())){
                File file = new File(StorageUtils.getMapRootPath() + entity.getFileName());
                String md5 = entity.getMd5();
                if(md5.length() < 32){
                    int n = 32 - md5.length();
                    for(int i = 0;i < n;i++){
                        md5 = "0" + md5;
                    }
                }
                if(!md5.equals(StringUtils.getFileMD5(file))){
                    FileUtil.deleteFile(StorageUtils.getMapRootPath() + entity.getFileName());
                    entity.setDownloadState(1);
                }else {
                    entity.setDownloadState(2);
                }
            }else {
                entity.setDownloadState(1);
            }
        }
        //检查文件是否更改
        final MapDbEntityDao dao = GreenDaoManager.getInstance().getSession().getMapDbEntityDao();
        dao.deleteAll();
        dao.insertOrReplaceInTx(entities);

        //下载
        downLoadFiles(context, entities,callback);
    }

    public static void downLoadFiles(Context context, ArrayList<MapDbEntity> tasks,Observer<MapDbEntity> callback){//1.未下载 2.下载完成 3.下载失败

        Observable.fromIterable(tasks)
                .filter(new Predicate<MapDbEntity>() {
                    @Override
                    public boolean test(@NonNull MapDbEntity mapDbEntity) throws Exception {
                        if(mapDbEntity.getDownloadState() != 2){
                            return true;
                        }else {
                            return false;
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<MapDbEntity, ObservableSource<MapDbEntity>>() {
                    @Override
                    public ObservableSource<MapDbEntity> apply(@NonNull final MapDbEntity mapDbEntity) throws Exception {
                        return  Observable.create(new ObservableOnSubscribe<MapDbEntity>() {
                            @Override
                            public void subscribe(@NonNull final ObservableEmitter<MapDbEntity> res) throws Exception {
                                FileDownloader.getImpl().create(ApiService.URL_QINIU + mapDbEntity.getImage_path())
                                        .setPath(StorageUtils.getMapRootPath() + mapDbEntity.getFileName())
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
                                                mapDbEntity.setDownloadState(2);
                                                res.onNext(mapDbEntity);
                                                res.onComplete();
                                            }

                                            @Override
                                            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                            }

                                            @Override
                                            protected void error(BaseDownloadTask task, Throwable e) {
                                                mapDbEntity.setDownloadState(3);
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
                .subscribe(callback);
    }

//    public static void downLoadFiles(Context context, ArrayList<MapDbEntity> entities,Observer<MapDbEntity> callback){//1.未下载 2.下载完成 3.下载失败
////        final RxDownload downloadSub = RxDownload.getInstance(context)
////                .maxThread(1)
////                .maxRetryCount(6)
////                .defaultSavePath(StorageUtils.getMapRootPath())
////                .retrofit(MoeMoeApplication.getInstance().getNetComponent().getRetrofit());
//        Observable.fromIterable(entities)
//                .filter(new Predicate<MapDbEntity>() {
//                    @Override
//                    public boolean test(@NonNull MapDbEntity mapDbEntity) throws Exception {
//                        if(mapDbEntity.getDownloadState() != 2){
//                            return true;
//                        }else {
//                            return false;
//                        }
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .flatMap(new Function<MapDbEntity, ObservableSource<MapDbEntity>>() {
//                    @Override
//                    public ObservableSource<MapDbEntity> apply(@NonNull final MapDbEntity mapDbEntity) throws Exception {
//                        return  Observable.create(new ObservableOnSubscribe<MapDbEntity>() {
//                            @Override
//                            public void subscribe(@NonNull final ObservableEmitter<MapDbEntity> res) throws Exception {
//
//
//
////                                downloadSub.download(ApiService.URL_QINIU + mapDbEntity.getImage_path(),mapDbEntity.getFileName())
////                                        .subscribeOn(Schedulers.io())
////                                        .observeOn(Schedulers.io())
////                                        .subscribe(new NetTResultSubscriber<DownloadStatus>() {
////                                            @Override
////                                            public void onSuccess() {
////                                                mapDbEntity.setDownloadState(2);
////                                                res.onNext(mapDbEntity);
////                                                res.onComplete();
////                                                downloadSub.deleteServiceDownload(ApiService.URL_QINIU + mapDbEntity.getImage_path(),false).subscribe();
////                                            }
////
////                                            @Override
////                                            public void onLoading(DownloadStatus res) {
////
////                                            }
////
////                                            @Override
////                                            public void onFail(Throwable e) {
////                                                mapDbEntity.setDownloadState(3);
////                                                res.onNext(mapDbEntity);
////                                                res.onComplete();
////                                                downloadSub.deleteServiceDownload(ApiService.URL_QINIU + mapDbEntity.getImage_path(),false).subscribe();
////                                            }
////                                        });
//                            }
//                        });
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(callback);
//    }
}
