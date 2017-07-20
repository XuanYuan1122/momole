package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.BadgeEntity;
import com.moemoe.lalala.model.entity.CoinShopEntity;
import com.moemoe.lalala.model.entity.OrderEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface CoinShopContract {
    interface Presenter extends BasePresenter{
        void loadShopList(int index);
        void createOrder(CoinShopEntity id);
    }

    interface View extends BaseView{
        void onLoadShopListSuccess(ArrayList<CoinShopEntity> list, boolean isPull);
        void onCreateOrderSuccess(OrderEntity entity);
    }
}
