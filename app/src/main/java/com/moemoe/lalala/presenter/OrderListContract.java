package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.OrderEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface OrderListContract {
    interface Presenter extends BasePresenter{
        void loadOrderList(int index);
    }

    interface View extends BaseView{
        void onLoadOrderListSuccess(ArrayList<OrderEntity> entities, boolean isPull);
    }
}
