package com.moemoe.lalala.presenter;

import android.text.TextUtils;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.ApiResult;
import com.moemoe.lalala.model.entity.DocPut;
import com.moemoe.lalala.model.entity.NewDocType;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.functions.Function5;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class CreateRichDocPresenter implements CreateRichDocContract.Presenter {

    private CreateRichDocContract.View view;
    private ApiService apiService;

    @Inject
    public CreateRichDocPresenter(CreateRichDocContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void createDoc(final DocPut doc, final int docType, final String docId,int coverSize) {
        if(coverSize == -1){
            create(doc,docType,docId);
        }else {
            Utils.uploadFile(apiService, doc.cover, new Observer<UploadResultEntity>() {

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {
                    create(doc,docType,docId);
                }

                @Override
                public void onSubscribe(@NonNull Disposable d) {

                }

                @Override
                public void onNext(UploadResultEntity uploadResultEntity) {
                    doc.cover = uploadResultEntity.getPath();
                }
            });
        }
    }

    private void create(final DocPut doc,final int docType,final String docId){
        ArrayList<DocPut.DocDetail> res = new ArrayList<>();
        res.addAll(doc.details);
        res.addAll(doc.coin.details);
        Observable.zip(
                Observable.range(0, res.size()),
                Observable.fromIterable(res),
                new BiFunction<Integer, DocPut.DocDetail, UpMap>() {
                    @Override
                    public UpMap apply(@NonNull Integer integer, @NonNull DocPut.DocDetail docDetail) throws Exception {
                        UpMap map = new UpMap();
                        map.index = integer;
                        map.data = docDetail;
                        return map;
                    }
                })
                .observeOn(Schedulers.io())
                .filter(new Predicate<UpMap>() {
                    @Override
                    public boolean test(@NonNull UpMap upMap) throws Exception {
                        if(upMap.data.type.equals(NewDocType.DOC_IMAGE.toString()) ||  upMap.data.type.equals(NewDocType.DOC_MUSIC.toString())){
                            if(upMap.data.type.equals(NewDocType.DOC_IMAGE.toString())){
                                DocPut.DocPutImage image = (DocPut.DocPutImage) upMap.data.data;
                                if(image.path.startsWith("image")){
                                    return false;
                                }else {
                                    return true;
                                }
                            }else {
                                DocPut.DocPutMusic music = (DocPut.DocPutMusic) upMap.data.data;
                                if(music.url.startsWith("http") && music.cover.getPath().startsWith("image")){
                                    return false;
                                }else {
                                    return true;
                                }
                            }
                        }else {
                            return false;
                        }
                    }
                })
                .observeOn(Schedulers.io())
                .concatMap(new Function<UpMap, ObservableSource<LinkedHashMap<UploadEntity,UpMap>>>() {
                    @Override
                    public ObservableSource<LinkedHashMap<UploadEntity, UpMap>> apply(@NonNull UpMap docDetailBooleanMap) throws Exception {
                        String temp = "";
                        String temp2 = "";
                        String path = "";
                        String path2 = "";
                        if(docDetailBooleanMap.data.type.equals(NewDocType.DOC_IMAGE.toString())){
                            path = ((DocPut.DocPutImage)docDetailBooleanMap.data.data).path;
                            temp = FileUtil.getExtensionName(((DocPut.DocPutImage)docDetailBooleanMap.data.data).path);
                            if(TextUtils.isEmpty(temp)){
                                temp = "jpg";
                            }
                        }else if(docDetailBooleanMap.data.type.equals(NewDocType.DOC_MUSIC.toString())){
                            temp = FileUtil.getExtensionName(((DocPut.DocPutMusic)docDetailBooleanMap.data.data).url);
                            path = ((DocPut.DocPutMusic)docDetailBooleanMap.data.data).url;
                            temp2 = FileUtil.getExtensionName(((DocPut.DocPutMusic)docDetailBooleanMap.data.data).cover.getPath());
                            path2 = ((DocPut.DocPutMusic)docDetailBooleanMap.data.data).cover.getPath();
                        }
                        if(TextUtils.isEmpty(temp2)){
                            return Observable.zip(
                                    apiService.requestQnFileKey(temp),
                                    Observable.just(path),
                                    Observable.just(docDetailBooleanMap),
                                    new Function3<ApiResult<UploadEntity>, String, UpMap, LinkedHashMap<UploadEntity,UpMap>>() {
                                        @Override
                                        public LinkedHashMap<UploadEntity, UpMap> apply(@NonNull ApiResult<UploadEntity> uploadEntityApiResult, @NonNull String s, @NonNull UpMap upMap) throws Exception {
                                            uploadEntityApiResult.getData().setLocalPath(s);
                                            uploadEntityApiResult.getData().setImg(true);
                                            LinkedHashMap<UploadEntity,UpMap> map = new LinkedHashMap<>();
                                            map.put(uploadEntityApiResult.getData(),upMap);
                                            return map;
                                        }

                                    }
                            );
                        }else {
                            return Observable.zip(
                                    apiService.requestQnFileKey(temp),
                                    apiService.requestQnFileKey(temp2),
                                    Observable.just(path),
                                    Observable.just(path2),
                                    Observable.just(docDetailBooleanMap),
                                    new Function5<ApiResult<UploadEntity>, ApiResult<UploadEntity>, String, String, UpMap, LinkedHashMap<UploadEntity,UpMap>>() {
                                        @Override
                                        public LinkedHashMap<UploadEntity, UpMap> apply(@NonNull ApiResult<UploadEntity> uploadEntityApiResult, @NonNull ApiResult<UploadEntity> uploadEntityApiResult2, @NonNull String s, @NonNull String s2, @NonNull UpMap upMap) throws Exception {
                                            LinkedHashMap<UploadEntity,UpMap> map = new LinkedHashMap<>();
                                            if(!s.startsWith("http")){
                                                uploadEntityApiResult.getData().setLocalPath(s);
                                                uploadEntityApiResult.getData().setImg(false);
                                                map.put(uploadEntityApiResult.getData(),upMap);
                                            }
                                            if(!s2.startsWith("image")){
                                                uploadEntityApiResult2.getData().setLocalPath(s2);
                                                uploadEntityApiResult2.getData().setImg(true);
                                                map.put(uploadEntityApiResult2.getData(),upMap);
                                            }
                                            return map;
                                        }
                                    }
                            );
                        }
                    }
                })
                .observeOn(Schedulers.io())
                .concatMap(new Function<LinkedHashMap<UploadEntity,UpMap>, ObservableSource<HashMap<UploadEntity,UpMap>>>() {
                    @Override
                    public ObservableSource<HashMap<UploadEntity, UpMap>> apply(@NonNull LinkedHashMap<UploadEntity, UpMap> uploadEntityUpMapLinkedHashMap) throws Exception {
                        return Observable.zip(
                                Observable.fromIterable(uploadEntityUpMapLinkedHashMap.keySet()),
                                Observable.fromIterable(uploadEntityUpMapLinkedHashMap.values()),
                                new BiFunction<UploadEntity, UpMap, HashMap<UploadEntity,UpMap>>() {
                                    @Override
                                    public HashMap<UploadEntity, UpMap> apply(@NonNull UploadEntity uploadEntity, @NonNull UpMap upMap) throws Exception {
                                        HashMap<UploadEntity,UpMap> hashMap = new HashMap<>();
                                        hashMap.put(uploadEntity,upMap);
                                        return hashMap;
                                    }
                                }
                        );
                    }
                })
                .observeOn(Schedulers.io())
                .concatMap(new Function<HashMap<UploadEntity,UpMap>, ObservableSource<DocPut.DocDetail>>() {
                    @Override
                    public ObservableSource<DocPut.DocDetail> apply(@NonNull HashMap<UploadEntity, UpMap> uploadEntityUpMapHashMap) throws Exception {
                        Set<UploadEntity> uploadEntities = uploadEntityUpMapHashMap.keySet();
                        final UploadEntity uploadEntity = uploadEntities.iterator().next();
                        final UpMap map = uploadEntityUpMapHashMap.get(uploadEntity);
                        final File file = new File(uploadEntity.getLocalPath());
                        final UploadManager uploadManager = new UploadManager();
                        return Observable.create(new ObservableOnSubscribe<DocPut.DocDetail>() {
                            @Override
                            public void subscribe(@NonNull final ObservableEmitter<DocPut.DocDetail> res) throws Exception {
                                try {
                                    uploadManager.put(file,uploadEntity.getFilePath(), uploadEntity.getUploadToken(), new UpCompletionHandler() {
                                        @Override
                                        public void complete(String key, ResponseInfo info, JSONObject response) {
                                            if (info.isOK()) {
                                                DocPut.DocDetail put;
                                                if(map.index < doc.details.size()){
                                                    put = doc.details.get(map.index);
                                                }else {
                                                    put = doc.coin.details.get(map.index - doc.details.size());
                                                }
                                                if(put.type.equals(NewDocType.DOC_IMAGE.toString())){
                                                    DocPut.DocPutImage img = (DocPut.DocPutImage) put.data;
                                                    try {
                                                        img.path = key;
                                                        img.h = response.getInt("h");
                                                        img.w = response.getInt("w");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }else if(put.type.equals(NewDocType.DOC_MUSIC.toString()) ){
                                                    DocPut.DocPutMusic music = (DocPut.DocPutMusic) put.data;
                                                    if(uploadEntity.isImg()){
                                                        try {
                                                            music.cover.setPath(key);
                                                            music.cover.setH(response.getInt("h"));
                                                            music.cover.setW(response.getInt("w"));
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }else {
                                                        music.url = ApiService.URL_QINIU + key;
                                                    }
                                                }
                                                res.onNext(put);
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
                .subscribe(new Observer<DocPut.DocDetail>() {

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        if(TextUtils.isEmpty(docId)){
                            if(docType == 3){
                                apiService.createWenZhangDoc(doc)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new NetResultSubscriber<String>() {
                                            @Override
                                            public void onSuccess(String id) {
                                                if(view != null) view.onSendSuccess(id,doc.cover);
                                            }

                                            @Override
                                            public void onFail(int code,String msg) {
                                                if(view != null) view.onFailure(code,msg);
                                            }
                                        });
                            }
                        }else {
                            apiService.updateDoc(doc,docId)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new NetSimpleResultSubscriber() {
                                        @Override
                                        public void onSuccess() {
                                            if(view != null) view.onSendSuccess("","");
                                        }

                                        @Override
                                        public void onFail(int code,String msg) {
                                            if(view != null) view.onFailure(code,msg);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(DocPut.DocDetail docDetail) {

                    }
                });
    }

    @Override
    public void release() {
        view = null;
    }

    private class UpMap{
        public int index;
        public DocPut.DocDetail data;
    }
}
