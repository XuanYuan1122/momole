package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.SnowEntity;
import com.moemoe.lalala.model.entity.SnowInfo;

/**
 * Created by yi on 2016/11/29.
 */

public interface SnowContract {
    interface Presenter extends BasePresenter{
        void requestSnowInfo();
        void requestSnowRankInfo(int index);
    }

    interface View extends BaseView{
        void updateSnowView(SnowEntity entity);
        void updateSnowList(SnowInfo entity, boolean pull);
    }
}
