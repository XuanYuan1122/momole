package com.moemoe.lalala.presenter;

import com.google.gson.JsonObject;
import com.moemoe.lalala.model.entity.BadgeEntity;
import com.moemoe.lalala.model.entity.CoinShopEntity;
import com.moemoe.lalala.model.entity.OrderEntity;
import com.moemoe.lalala.model.entity.OrderTmp;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface CoinShopContract {
    interface Presenter extends BasePresenter{
        void loadShopList(int index);
        void createOrder(CoinShopEntity id);
        void createOrder(CoinShopEntity id,int num);
        void createOrderList(OrderTmp orderTmp);
    }

    interface View extends BaseView{
        void onLoadShopListSuccess(ArrayList<CoinShopEntity> list, boolean isPull);
        void onCreateOrderSuccess(OrderEntity entity);
        void onCreateOrderListSuccess(ArrayList<JsonObject> jsonObjects);
    }
}
