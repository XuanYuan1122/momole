package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;

/**
 * Created by yi on 2016/11/29.
 */

public interface LIve2dNormalContract {
    interface Presenter extends BasePresenter{
        void pingfenLive2d(int score,String id);
    }

    interface View extends BaseView{
        void onSuccess();
    }
}
