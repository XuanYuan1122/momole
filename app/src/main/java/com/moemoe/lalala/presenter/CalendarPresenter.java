package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.CalendarDayEntity;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class CalendarPresenter implements CalendarContract.Presenter {

    private CalendarContract.View view;
    private ApiService apiService;

    @Inject
    public CalendarPresenter(CalendarContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void doRequest(String day, final boolean pull) {
        apiService.requestCalDayList(day)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<CalendarDayEntity>() {
                    @Override
                    public void onSuccess(CalendarDayEntity calendarDayEntity) {
                        if(view != null) view.onSuccess(calendarDayEntity,pull);
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void release() {
        view = null;
    }
}
