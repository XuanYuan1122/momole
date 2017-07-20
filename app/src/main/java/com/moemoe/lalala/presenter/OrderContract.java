package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.PayReqEntity;
import com.moemoe.lalala.model.entity.PayResEntity;

/**
 * Created by yi on 2016/11/29.
 */

public interface OrderContract {
    interface Presenter extends BasePresenter{
        void cancelOrder(String orderId);
        void payOrder(PayReqEntity entity);
    }

    interface View extends BaseView{
        void onCancelOrderSuccess();
        void onPayOrderSuccess(PayResEntity entity);
    }
}
