package com.moemoe.lalala.presenter;

import android.text.TextUtils;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.ApiResult;
import com.moemoe.lalala.model.entity.DocPut;
import com.moemoe.lalala.model.entity.NewDocType;
import com.moemoe.lalala.model.entity.UploadEntity;
import com.moemoe.lalala.utils.FileUtil;
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

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.functions.Func5;
import rx.schedulers.Schedulers;

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
    public void createDoc(final DocPut doc, final int docType, final String docId) {
        ArrayList<DocPut.DocDetail> res = new ArrayList<>();
        res.addAll(doc.details);
        res.addAll(doc.coin.details);
        Observable.zip(
                Observable.range(0, res.size()),
                Observable.from(res),
                new Func2<Integer, DocPut.DocDetail, UpMap>() {
                    @Override
                    public UpMap call(Integer integer, DocPut.DocDetail docDetail) {
                        UpMap map = new UpMap();
                        map.index = integer;
                        map.data = docDetail;
                        return map;
                    }
                }
                )
                .observeOn(Schedulers.io())
                .filter(new Func1<UpMap, Boolean>() {
                    @Override
                    public Boolean call(UpMap upMap) {
                        return upMap.data.type.equals(NewDocType.DOC_IMAGE.toString()) ||  upMap.data.type.equals(NewDocType.DOC_MUSIC.toString());
                    }
                })
                .observeOn(Schedulers.io())
                .concatMap(new Func1<UpMap, Observable<LinkedHashMap<UploadEntity,UpMap>>>() {
                    @Override
                    public Observable<LinkedHashMap<UploadEntity,UpMap>> call(UpMap docDetailBooleanMap) {
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
                            temp2 = FileUtil.getExtensionName(((DocPut.DocPutMusic)docDetailBooleanMap.data.data).cover.getPath());
                        }
                        if(TextUtils.isEmpty(temp2)){
                            return Observable.zip(
                                    apiService.requestQnFileKey(temp),
                                    Observable.just(path),
                                    Observable.just(docDetailBooleanMap),
                                    new Func3<ApiResult<UploadEntity>, String,UpMap, LinkedHashMap<UploadEntity,UpMap>>() {
                                        @Override
                                        public LinkedHashMap<UploadEntity,UpMap> call(ApiResult<UploadEntity> uploadEntityApiResult, String s,UpMap i) {
                                            uploadEntityApiResult.getData().setLocalPath(s);
                                            LinkedHashMap<UploadEntity,UpMap> map = new LinkedHashMap<>();
                                            map.put(uploadEntityApiResult.getData(),i);
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
                                    new Func5<ApiResult<UploadEntity>, ApiResult<UploadEntity>, String, String, UpMap, LinkedHashMap<UploadEntity,UpMap>>() {
                                        @Override
                                        public LinkedHashMap<UploadEntity,UpMap> call(ApiResult<UploadEntity> uploadEntityApiResult, ApiResult<UploadEntity> uploadEntityApiResult2, String s, String s2, UpMap i) {
                                            uploadEntityApiResult.getData().setLocalPath(s);
                                            uploadEntityApiResult.getData().setImg(false);
                                            uploadEntityApiResult2.getData().setLocalPath(s2);
                                            uploadEntityApiResult2.getData().setImg(true);
                                            LinkedHashMap<UploadEntity,UpMap> map = new LinkedHashMap<>();
                                            map.put(uploadEntityApiResult.getData(),i);
                                            map.put(uploadEntityApiResult2.getData(),i);
                                            return map;
                                        }
                                    }
                            );
                        }

                    }
               })
               .observeOn(Schedulers.io())
               .concatMap(new Func1<LinkedHashMap<UploadEntity, UpMap>, Observable<HashMap<UploadEntity,UpMap>>>() {
                   @Override
                   public Observable<HashMap<UploadEntity,UpMap>> call(LinkedHashMap<UploadEntity, UpMap> uploadEntityIntegerLinkedHashMap) {
                       return Observable.zip(
                               Observable.from(uploadEntityIntegerLinkedHashMap.keySet()),
                               Observable.from(uploadEntityIntegerLinkedHashMap.values()),
                               new Func2<UploadEntity, UpMap, HashMap<UploadEntity,UpMap>>() {
                                   @Override
                                   public HashMap<UploadEntity,UpMap> call(final UploadEntity uploadEntity,final UpMap map) {
                                       HashMap<UploadEntity,UpMap> hashMap = new HashMap<>();
                                       hashMap.put(uploadEntity,map);
                                       return hashMap;
                                   }
                               }
                       );
                   }
               })
               .observeOn(Schedulers.io())
               .concatMap(new Func1<HashMap<UploadEntity, UpMap>, Observable<DocPut.DocDetail>>() {
                   @Override
                   public Observable<DocPut.DocDetail> call(HashMap<UploadEntity, UpMap> uploadEntityUpMapHashMap) {
                       Set<UploadEntity> uploadEntities = uploadEntityUpMapHashMap.keySet();
                       final UploadEntity uploadEntity = uploadEntities.iterator().next();
                       final UpMap map = uploadEntityUpMapHashMap.get(uploadEntity);
                       final File file = new File(uploadEntity.getLocalPath());
                       final UploadManager uploadManager = new UploadManager();
                       return Observable.create(new Observable.OnSubscribe<DocPut.DocDetail>() {
                           @Override
                           public void call(final Subscriber<? super DocPut.DocDetail> subscriber) {
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
                                               subscriber.onNext(put);
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
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new Subscriber<DocPut.DocDetail>() {
                   @Override
                   public void onCompleted() {
                       if(TextUtils.isEmpty(docId)){
                           if(docType == 1){
                               apiService.createQiuMingShanDoc(doc)
                                       .subscribeOn(Schedulers.io())
                                       .observeOn(AndroidSchedulers.mainThread())
                                       .subscribe(new NetSimpleResultSubscriber() {
                                           @Override
                                           public void onSuccess() {
                                               if(view != null) view.onSendSuccess();
                                           }

                                           @Override
                                           public void onFail(int code,String msg) {
                                               if(view != null) view.onFailure(code,msg);
                                           }
                                       });
                           }else if(docType == 0){
                               apiService.createNormalDoc(doc)
                                       .subscribeOn(Schedulers.io())
                                       .observeOn(AndroidSchedulers.mainThread())
                                       .subscribe(new NetSimpleResultSubscriber() {
                                           @Override
                                           public void onSuccess() {
                                               if(view != null) view.onSendSuccess();
                                           }

                                           @Override
                                           public void onFail(int code,String msg) {
                                               if(view != null) view.onFailure(code,msg);
                                           }
                                       });
                           }else if(docType == 2){
                               apiService.createSwimPoolDoc(doc)
                                       .subscribeOn(Schedulers.io())
                                       .observeOn(AndroidSchedulers.mainThread())
                                       .subscribe(new NetSimpleResultSubscriber() {
                                           @Override
                                           public void onSuccess() {
                                               if(view != null) view.onSendSuccess();
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
                                           if(view != null) view.onSendSuccess();
                                       }

                                       @Override
                                       public void onFail(int code,String msg) {
                                           if(view != null) view.onFailure(code,msg);
                                       }
                                   });
                       }
                   }

                   @Override
                   public void onError(Throwable e) {

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
