package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.MapAddressEntity;
import com.moemoe.lalala.model.entity.UserMapSendEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface CreateMapImageContract {
    interface Presenter extends BasePresenter{
        void loadAddressList();
        void saveUserMapImage(String cover,String orCover,String id,boolean needCheck);
    }

    interface View extends BaseView{
        void onLoadAddressListSuccess(ArrayList<MapAddressEntity> entities);
        void onSaveSuccess();
    }
}
