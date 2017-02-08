package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.DonationInfoEntity;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class DonationPresenter implements DonationContract.Presenter {

    private DonationContract.View view;
    private ApiService apiService;

    @Inject
    public DonationPresenter(DonationContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }


    @Override
    public void requestDonationInfo() {
        apiService.getDonationInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<DonationInfoEntity>() {
                    @Override
                    public void onSuccess(DonationInfoEntity entity) {
                        view.updateDonationView(entity);
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void donationCoin(long num) {
        apiService.donationCoin(num)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        view.donationCoinSuccess();
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void requestDonationBookInfo(final int index) {
        apiService.getBookDonationInfo(index,ApiService.LENGHT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<DonationInfoEntity>() {
                    @Override
                    public void onSuccess(DonationInfoEntity entity) {
                        view.updateDonationBook(entity,index == 0);
                    }

                    @Override
                    public void onFail(int code,String msg) {
                        view.onFailure(code,msg);
                    }
                });
    }
}
