package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.ApiResult;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.ModifyEntity;
import com.moemoe.lalala.model.entity.UploadEntity;
import com.moemoe.lalala.utils.FileUtil;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

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

public class EditAccountPresenter implements EditAccountContract.Presenter {

    private EditAccountContract.View view;
    private ApiService apiService;

    @Inject
    public EditAccountPresenter(EditAccountContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void uploadAvatar(final String  path, final int type) {
        final Image fb = new Image();
        fb.setLocal_path(path);
        String suffix = FileUtil.getExtensionName(path);
        apiService.requestQnFileKey(suffix)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Function<ApiResult<UploadEntity>, ObservableSource<Image>>() {
                    @Override
                    public ObservableSource<Image> apply(@NonNull final ApiResult<UploadEntity> uploadEntityApiResult) throws Exception {

                        final File file = new File(path);
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
                                                fb.setPath(key);
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Image>() {

                    @Override
                    public void onError(Throwable e) {
                        if(view != null) view.uploadFail(type);
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(Image image) {
                        if(view != null) view.uploadSuccess(image.getPath(),type);
                    }
                });
    }

    @Override
    public void modify(String name, String sex, String birthday,String bg,String headPath,String sign) {
        ModifyEntity entity = new ModifyEntity();
        entity.birthday = birthday;
        entity.sex = sex;
        entity.nickName = name;
        entity.background = bg;
        entity.headPath = headPath.replace(ApiService.URL_QINIU,"");
        entity.signature = sign;
        apiService.modifyAll(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.modifySuccess();
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
