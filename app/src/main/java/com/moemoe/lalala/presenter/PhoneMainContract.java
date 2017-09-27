package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;

import java.util.Date;

/**
 * Created by yi on 2016/11/29.
 */

public interface PhoneMainContract {
    interface Presenter extends BasePresenter{
        void loadRcToken();
    }

    interface View extends BaseView{
        void onLoadRcTokenSuccess(String token);
        void onLoadRcTokenFail(int code,String msg);
    }
}
