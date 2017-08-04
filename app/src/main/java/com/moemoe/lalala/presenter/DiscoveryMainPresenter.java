package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.NewDocListEntity;
import com.moemoe.lalala.model.entity.XianChongEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class DiscoveryMainPresenter extends BaseBannerPresenter implements DiscovertMainContract.Presenter{

    private DiscovertMainContract.View view;
    private ApiService apiService;

    @Inject
    public DiscoveryMainPresenter(DiscovertMainContract.View view, ApiService apiService) {
        super(view,apiService);
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void loadXianChongList() {
        apiService.loadXianChongList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<XianChongEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<XianChongEntity> entities) {
                        if(view!=null) view.onLoadXianChongSuccess(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view!=null)view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void loadDocList(long lastTime, final boolean change, final boolean isPull) {
        apiService.getFeedFindList(lastTime,ApiService.LENGHT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<NewDocListEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<NewDocListEntity> entities) {
                        if(change){
                            if(view!=null)view.onChangeSuccess(entities);
                        }else {
                            if(view!=null) view.onLoadDocListSuccess(entities,isPull);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view!=null)view.onFailure(code, msg);
                    }
                });
    }
}
