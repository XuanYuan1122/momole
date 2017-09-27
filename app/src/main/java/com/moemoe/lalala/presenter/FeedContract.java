package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.FeaturedEntity;
import com.moemoe.lalala.model.entity.NewDynamicEntity;
import com.moemoe.lalala.model.entity.XianChongEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface FeedContract {
    interface Presenter extends BasePresenter{
        void loadList(long time,String type);
        void loadList(int index);
        void requestBannerData(String room);
        void requestFeatured(String room);
        void loadXianChongList();
    }

    interface View extends BaseView{
        void onLoadListSuccess(ArrayList<NewDynamicEntity> resList,boolean isPull);
        void onBannerLoadSuccess(ArrayList<BannerEntity> bannerEntities);
        void onFeaturedLoadSuccess(ArrayList<FeaturedEntity> featuredEntities);
        void onLoadXianChongSuccess(ArrayList<XianChongEntity> entities);
    }
}
