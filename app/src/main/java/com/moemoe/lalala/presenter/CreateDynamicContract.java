package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.DynamicSendEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface CreateDynamicContract {
    interface Presenter extends BasePresenter{
        void createDynamic(DynamicSendEntity entity, ArrayList<String> paths);
    }

    interface View extends BaseView{
        void onCreateDynamicSuccess();
    }
}
