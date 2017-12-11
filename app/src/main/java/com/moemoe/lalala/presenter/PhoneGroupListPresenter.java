package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.GroupEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by yi on 2016/11/29.
 */

public class PhoneGroupListPresenter implements PhoneGroupListContract.Presenter {

    private PhoneGroupListContract.View view;
    private ApiService apiService;

    @Inject
    public PhoneGroupListPresenter(PhoneGroupListContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadGroupList(final int index,boolean isUser) {
        if(isUser){
            apiService.loadMyGroupList(index,ApiService.LENGHT)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<GroupEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<GroupEntity> entities) {
                            if(view != null) view.onLoadGroupListSuccess(entities,index == 0);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }else {
            apiService.loadGroupList(index,ApiService.LENGHT)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<GroupEntity>>() {
                        @Override
                        public void onSuccess(ArrayList<GroupEntity> entities) {
                            if(view != null) view.onLoadGroupListSuccess(entities,index == 0);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }
    }
}
