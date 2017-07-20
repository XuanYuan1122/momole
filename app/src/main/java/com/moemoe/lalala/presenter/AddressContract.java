package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.BadgeEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface AddressContract {
    interface Presenter extends BasePresenter{
        void loadUserAddress();
        void saveUserAddress(AddressEntity entity);
    }

    interface View extends BaseView{
        void onLoadAddressSuccess(AddressEntity entity);
        void onSaveAddressSuccess();
    }
}
