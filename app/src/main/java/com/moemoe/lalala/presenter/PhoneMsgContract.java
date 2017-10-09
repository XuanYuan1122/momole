package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;

import java.util.Date;

/**
 * Created by yi on 2016/11/29.
 */

public interface PhoneMsgContract {
    interface Presenter extends BasePresenter{
        void getServerTime(String role);
    }

    interface View extends BaseView{
        void onGetTimeSuccess(Date time,String role);
    }
}
