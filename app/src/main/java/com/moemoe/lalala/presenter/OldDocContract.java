package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.DocResponse;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface OldDocContract {
    interface Presenter extends BasePresenter{
        void loadOldDocList(String type,long time);
        void requestBannerData(String room);
    }

    interface View extends BaseView{
        void loadOldDocListSuccess(ArrayList<DocResponse> list,boolean isPull);
        void onBannerLoadSuccess(ArrayList<BannerEntity> bannerEntities);
    }
}
