package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.LuntanTabEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface LuntanContract {
    interface Presenter extends BasePresenter{
        void loadTabList();
    }

    interface View extends BaseView{
        void onLoadTabListSuccess(ArrayList<LuntanTabEntity> entities);
    }
}
