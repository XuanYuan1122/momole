package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.NewBagEntity;

/**
 * Created by yi on 2016/11/29.
 */

public interface NewBagContract {
    interface Presenter extends BasePresenter{
        void loadBagData(String userId);
    }

    interface View extends BaseView{
        void onLoadBagSuccess(NewBagEntity entity);
    }
}
