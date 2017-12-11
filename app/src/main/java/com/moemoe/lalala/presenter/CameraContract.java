package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.StickEntity;

import java.util.ArrayList;

/**
 *
 *
 * Created by yi on 2016/11/29.
 */

public interface CameraContract {
    interface Presenter extends BasePresenter{
        void loadStickList();
        void buyStick(String id,String roleId,int position);
    }

    interface View extends BaseView{
        void onLoadStickListSuccess(ArrayList<StickEntity> entities);
        void onBuyStickSuccess(String roleId,int position);
    }
}
