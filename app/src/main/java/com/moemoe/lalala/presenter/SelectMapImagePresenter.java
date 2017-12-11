package com.moemoe.lalala.presenter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AppUpdateEntity;
import com.moemoe.lalala.model.entity.DeleteRoleSend;
import com.moemoe.lalala.model.entity.MapHistoryEntity;
import com.moemoe.lalala.model.entity.MapUserImageEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/27.
 */

public class SelectMapImagePresenter implements SelectMapImageContract.Presenter {
    private SelectMapImageContract.View view;
    private ApiService apiService;

    @Inject
    public SelectMapImagePresenter(SelectMapImageContract.View view, ApiService apiService){
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadMapSelectList() {
        apiService.loadMapSelectList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<MapUserImageEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<MapUserImageEntity> entities) {
                        if(view != null) view.onLoadListSuccess(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null)  view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void loadMapHistoryList(String id, final int index) {
        apiService.loadMapHistoryList(id,index,ApiService.LENGHT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<MapHistoryEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<MapHistoryEntity> mapHistoryEntities) {
                        if(view != null)  view.onLoadHistoryListSuccess(mapHistoryEntities,index == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null)  view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void deleteHistoryMapRole(ArrayList<String> ids) {
        DeleteRoleSend send = new DeleteRoleSend(ids);

        apiService.deleteHistoryMapRole(send)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onDeleteSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null)  view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void likeMapRole(final boolean isLike, String id, final int position) {
        apiService.likeUserMapRole(!isLike,id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onLikeSuccess(!isLike,position);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }
}
