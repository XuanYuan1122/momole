package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.Live2dShopEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface Live2dShopContract {
    interface Presenter extends BasePresenter{
       void buyLive2d(String id,int position);
       void loadLive2dList(int index);
    }

    interface View extends BaseView{
        void onBuyLive2dSuccess(int position);
        void onLoadListSuccess(ArrayList<Live2dShopEntity> entities,boolean isPull);
    }
}
