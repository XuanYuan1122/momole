package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.CoinShopEntity;
import com.moemoe.lalala.model.entity.CreateOrderEntity;
import com.moemoe.lalala.model.entity.OrderEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class CoinShopPresenter implements CoinShopContract.Presenter {

    private CoinShopContract.View view;
    private ApiService apiService;

    @Inject
    public CoinShopPresenter(CoinShopContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }


    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadShopList(final int index) {
        apiService.loadShopList(index, ApiService.LENGHT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<CoinShopEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<CoinShopEntity> list) {
                        if(view!=null)view.onLoadShopListSuccess(list, index == 0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view!=null)view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void createOrder(final CoinShopEntity shopEntity) {
        apiService.createOrder(shopEntity.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<CreateOrderEntity>() {
                    @Override
                    public void onSuccess(CreateOrderEntity entity) {
                        OrderEntity orderEntity = new OrderEntity();
                        orderEntity.setAddress(entity.getAddress());
                        orderEntity.setEndTime(entity.getEndTime());
                        orderEntity.setOrderNo(entity.getOrderNo());
                        orderEntity.setLastRemark(entity.getLastRemark());
                        orderEntity.setOrderId(entity.getOrderId());
                        orderEntity.setIcon(shopEntity.getIcon());
                        orderEntity.setProductName(shopEntity.getProductName());
                        orderEntity.setDesc(shopEntity.getDesc());
                        orderEntity.setOrderType(shopEntity.getOrderType());
                        orderEntity.setRmb(shopEntity.getRmb());
                        orderEntity.setCoin(shopEntity.getCoin());
                        if(view!=null) view.onCreateOrderSuccess(orderEntity);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view!=null)view.onFailure(code, msg);
                    }
                });
    }
}
