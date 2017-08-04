package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.NewDocListEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface FollowMainContract {
    interface Presenter extends BasePresenter{
        void loadFollowList(long lastTime, boolean change, boolean isPull);
    }

    interface View extends BaseView{
        void onLoadFollowListSuccess(ArrayList<NewDocListEntity> entities, boolean isPull);
        void onChangeListSuccess(ArrayList<NewDocListEntity> entities);
    }
}
