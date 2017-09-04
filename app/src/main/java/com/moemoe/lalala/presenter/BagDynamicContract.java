package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.BagMyShowEntity;
import com.moemoe.lalala.model.entity.DynamicEntity;
import com.moemoe.lalala.model.entity.DynamicTopEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface BagDynamicContract {
    interface Presenter extends BasePresenter{
        void loadTop();
        void loadList(long index);
    }

    interface View extends BaseView{
        void onLoadTopSuccess(ArrayList<DynamicTopEntity> entities);
        void onLoadListSuccess(ArrayList<DynamicEntity> entities);
    }
}
