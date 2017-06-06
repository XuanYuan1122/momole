package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.ApiResult;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.TrashPut;
import com.moemoe.lalala.model.entity.UploadEntity;
import com.moemoe.lalala.utils.FileUtil;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class CreateTrashPresenter implements CreateTrashContract.Presenter {

    private CreateTrashContract.View view;
    private ApiService apiService;

    @Inject
    public CreateTrashPresenter(CreateTrashContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;

    }

    @Override
    public void createTrash(TrashPut put) {
        apiService.createTextTrash(put)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onCreateSuccess();
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void createUploadTrash(final TrashPut put, final String path) {
        String suffix = FileUtil.getExtensionName(path);
        apiService.requestQnFileKey(suffix)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Func1<ApiResult<UploadEntity>, Observable<Image>>() {
                    @Override
                    public Observable<Image> call(final ApiResult<UploadEntity> uploadEntityApiResult) {
                        final File file = new File(path);
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
                        put.h = image.getH();
                        put.w = image.getW();
                        put.path = image.getPath();
                        return apiService.createImageTrash(put);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onCreateSuccess();
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void release() {
        view = null;
    }
}
