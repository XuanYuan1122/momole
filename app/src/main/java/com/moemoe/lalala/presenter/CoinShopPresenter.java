package com.moemoe.lalala.presenter;

import com.google.gson.JsonObject;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.entity.CoinShopEntity;
import com.moemoe.lalala.model.entity.CreateOrderEntity;
import com.moemoe.lalala.model.entity.OrderEntity;
import com.moemoe.lalala.model.entity.OrderTmp;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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

    @Override
    public void createOrder(final CoinShopEntity id, int num) {
        apiService.createOrderNum(num,id.getId())
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
                        orderEntity.setIcon(id.getIcon());
                        orderEntity.setProductName(id.getProductName());
                        orderEntity.setDesc(id.getDesc());
                        orderEntity.setOrderType(id.getOrderType());
                        orderEntity.setRmb(id.getRmb());
                        orderEntity.setCoin(id.getCoin());
                        if(view!=null) view.onCreateOrderSuccess(orderEntity);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view!=null)view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void createOrderList(OrderTmp orderTmp) {
        apiService.createPayList(orderTmp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<JsonObject>>() {
                    @Override
                    public void onSuccess(ArrayList<JsonObject> jsonObjects) {
                        if(view!=null)view.onCreateOrderListSuccess(jsonObjects);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view!=null)view.onFailure(code, msg);
                    }
                });
    }
}
