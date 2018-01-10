package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.HongBaoEntity;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2016/11/29.
 */

public interface HongBaoListContract {
    interface Presenter extends BasePresenter{
        void loadHongBaoList(String id);
    }

    interface View extends BaseView{
        void onLoadHongBaoListSuccess(ArrayList<HongBaoEntity> entities);
    }
}
