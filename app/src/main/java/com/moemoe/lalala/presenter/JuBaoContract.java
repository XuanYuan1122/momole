package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;

/**
 * Created by yi on 2016/11/29.
 */

public interface JuBaoContract {
    interface Presenter extends BasePresenter{
        void doRequest(Object data,int type);
    }

    interface View extends BaseView{
        void onSuccess(Object o);
    }
}
