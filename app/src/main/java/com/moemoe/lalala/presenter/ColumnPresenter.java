package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.CalendarDayItemEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class ColumnPresenter implements ColumnContract.Presenter {

    private ColumnContract.View view;
    private ApiService apiService;

    @Inject
    public ColumnPresenter(ColumnContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    private void requestFresh(String barId, int index, final boolean isFuture) {
        apiService.requestUiDocList(barId,index,ApiService.LENGHT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<CalendarDayItemEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<CalendarDayItemEntity> calendarDayItemEntities) {
                        if(isFuture){
                            if(view != null)  view.loadColumnFutureData(calendarDayItemEntities);
                        }else {
                            if(view != null) view.loadColumnPastData(calendarDayItemEntities);
                        }
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void requestFutureFresh(String barId,int index) {
        requestFresh(barId,index,true);
    }

    @Override
    public void requestPastFresh(String barId,int index) {
        requestFresh(barId,index,false);
    }

    @Override
    public void release() {
        view = null;
    }
}
