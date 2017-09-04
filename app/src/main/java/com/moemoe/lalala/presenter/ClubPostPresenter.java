package com.moemoe.lalala.presenter;

import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.ApiResult;
import com.moemoe.lalala.model.entity.ClubZipEntity;
import com.moemoe.lalala.model.entity.DocListEntity;
import com.moemoe.lalala.model.entity.TagNodeEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class ClubPostPresenter implements ClubPostContract.Presenter {

    private ClubPostContract.View view;
    private ApiService apiService;
    private String tagName;

    @Inject
    public ClubPostPresenter(ClubPostContract.View view,ApiService apiService){
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void requestClubData(String tagId) {
        apiService.requestTagNode(tagId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<ApiResult<TagNodeEntity>, String>() {
                    @Override
                    public String call(ApiResult<TagNodeEntity> tagNodeEntityApiResult) {
                        if(tagNodeEntityApiResult.getState() == 200 && tagNodeEntityApiResult.getData() != null){
                            tagName = tagNodeEntityApiResult.getData().getName();
                            if(view!=null) view.bindClubViewData(tagNodeEntityApiResult.getData());
                            return tagNodeEntityApiResult.getData().getName();
                        }else {
                            return "";
                        }
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Func1<String, Observable<ClubZipEntity>>() {
                    @Override
                    public Observable<ClubZipEntity> call(String s) {
                        return Observable.zip(
                                apiService.requestTopTagDocList(s),
                                apiService.requestHotTagDocList(s),
                                apiService.requestTagDocList(0,ApiService.LENGHT,s, AppSetting.SUB_TAG),
                                new Func3<ApiResult<ArrayList<DocListEntity>>, ApiResult<ArrayList<DocListEntity>>, ApiResult<ArrayList<DocListEntity>>, ClubZipEntity>() {
                                    @Override
                                    public ClubZipEntity call(ApiResult<ArrayList<DocListEntity>> arrayListApiResult, ApiResult<ArrayList<DocListEntity>> arrayListApiResult2, ApiResult<ArrayList<DocListEntity>> arrayListApiResult3) {
                                        return new ClubZipEntity(arrayListApiResult,arrayListApiResult2,arrayListApiResult3);
                                    }
                                }
                        );
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ClubZipEntity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if(view != null) view.onFailure(-1,"");
                    }

                    @Override
                    public void onNext(ClubZipEntity clubZipEntity) {
                        if(view != null) view.bindListViewData(clubZipEntity);
                    }
                });
    }

    @Override
    public void requestDocList(final int index) {
        apiService.requestTagDocList(index,ApiService.LENGHT,tagName, AppSetting.SUB_TAG)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<DocListEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<DocListEntity> docListEntities) {
                        if(view != null) view.onLoadDocList(docListEntities,index == 0);
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void followClub(String id, final boolean follow) {
        apiService.followClub(!follow,id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view!=null)view.onFollowClubSuccess(!follow);
                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
    }

    @Override
    public void release() {
        view = null;
    }
}
