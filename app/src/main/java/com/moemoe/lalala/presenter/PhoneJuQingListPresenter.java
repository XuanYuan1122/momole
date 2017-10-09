package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.JuQingEntity;
import com.moemoe.lalala.model.entity.PhoneMenuEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * Created by yi on 2016/11/29.
 */

public class PhoneJuQingListPresenter implements PhoneJuQingListContract.Presenter {

    private PhoneJuQingListContract.View view;
    private ApiService apiService;

    @Inject
    public PhoneJuQingListPresenter(PhoneJuQingListContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    /**
     *
     * @param level 1主线 2.支线 3.日常
     * @param type 0全部 1.攻略中 2.已完成 3.未解锁
     * @param index
     */
    @Override
    public void loadUserList(int level,int type, final int index) {
        apiService.loadStoryList(level,type,index,ApiService.LENGHT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<JuQingEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<JuQingEntity> juQingEntities) {
                        if(view != null)view.onLoadUserListSuccess(juQingEntities,index == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }
}
