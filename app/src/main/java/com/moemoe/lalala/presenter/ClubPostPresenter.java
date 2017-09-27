package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.ApiResult;
import com.moemoe.lalala.model.entity.ClubZipEntity;
import com.moemoe.lalala.model.entity.DocListEntity;
import com.moemoe.lalala.model.entity.TagNodeEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;


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
                .map(new Function<ApiResult<TagNodeEntity>, String>() {
                    @Override
                    public String apply(@NonNull ApiResult<TagNodeEntity> tagNodeEntityApiResult) throws Exception {
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
                .flatMap(new Function<String, ObservableSource<ClubZipEntity>>() {
                    @Override
                    public ObservableSource<ClubZipEntity> apply(@NonNull String s) throws Exception {
                        return Observable.zip(
                                apiService.requestTopTagDocList(s),
                                apiService.requestHotTagDocList(s),
                                apiService.requestTagDocList(0,ApiService.LENGHT,s, false),
                                new Function3<ApiResult<ArrayList<DocListEntity>>, ApiResult<ArrayList<DocListEntity>>, ApiResult<ArrayList<DocListEntity>>, ClubZipEntity>() {
                                    @Override
                                    public ClubZipEntity apply(@NonNull ApiResult<ArrayList<DocListEntity>> arrayListApiResult, @NonNull ApiResult<ArrayList<DocListEntity>> arrayListApiResult2, @NonNull ApiResult<ArrayList<DocListEntity>> arrayListApiResult3) throws Exception {
                                        return new ClubZipEntity(arrayListApiResult,arrayListApiResult2,arrayListApiResult3);
                                    }
                                }
                        );
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @Override
    public void requestDocList(final int index) {
        apiService.requestTagDocList(index,ApiService.LENGHT,tagName, false)
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
