package com.moemoe.lalala.presenter;

import com.moemoe.lalala.dialog.SignDialog;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.DailyTaskEntity;
import com.moemoe.lalala.model.entity.PersonalMainEntity;
import com.moemoe.lalala.model.entity.SignEntity;
import com.moemoe.lalala.utils.PreferenceUtils;

import java.util.Date;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class PhoneMainPresenter implements PhoneMainContract.Presenter {

    private PhoneMainContract.View view;
    private ApiService apiService;

    @Inject
    public PhoneMainPresenter(PhoneMainContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }


    @Override
    public void getDailyTask() {
        apiService.getDailyTask()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<DailyTaskEntity>() {
                    @Override
                    public void onSuccess(DailyTaskEntity dailyTaskEntity) {
                        if(view != null) view.onDailyTaskLoad(dailyTaskEntity);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void signToday(final SignDialog dialog) {
        apiService.signToday()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<SignEntity>() {
                    @Override
                    public void onSuccess(SignEntity entity) {
                        if(view != null) {
                            view.changeSignState(entity, true);
                            dialog.setIsSign(true)
                                    .setSignDay(entity.getDay())
                                    .changeSignState();
                        }
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void requestPersonMain() {
        apiService.getPersonalMain(PreferenceUtils.getUUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<PersonalMainEntity>() {
                    @Override
                    public void onSuccess(PersonalMainEntity personalMainEntity) {
                        if(view != null) view.onPersonMainLoad(personalMainEntity);
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }
}
