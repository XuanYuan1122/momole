package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
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

    @Override
    public void loadUserList(String type, final int index) {
        if(type.equals("main")){

        }else if(type.equals("sec")){

        }else if(type.equals("day")){

        }
    }
}
