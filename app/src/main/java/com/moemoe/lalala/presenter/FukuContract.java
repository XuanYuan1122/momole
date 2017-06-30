package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.Live2dModelEntity;
import com.moemoe.lalala.model.entity.SnowEntity;
import com.moemoe.lalala.model.entity.SnowInfo;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface FukuContract {
    interface Presenter extends BasePresenter{
        void getFukuList();
    }

    interface View extends BaseView{
        void getFukuListSuccess(ArrayList<Live2dModelEntity> list);
    }
}
