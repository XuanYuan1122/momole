package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.FeaturedEntity;
import com.moemoe.lalala.model.entity.NewDocListEntity;
import com.moemoe.lalala.model.entity.XianChongEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface DiscovertMainContract{
    interface Presenter extends BaseBannerContract.Presenter{
        void loadXianChongList();
        void loadDocList(long index, boolean change,boolean isPull);
        void requestBannerData(String room);
        void requestFeatured(String room);
    }

    interface View extends BaseBannerContract.View{
        void onLoadXianChongSuccess(ArrayList<XianChongEntity> entities);
        void onChangeSuccess(ArrayList<NewDocListEntity> entities);
        void onLoadDocListSuccess(ArrayList<NewDocListEntity> entities, boolean isPull);
        void onBannerLoadSuccess(ArrayList<BannerEntity> bannerEntities);
        void onFeaturedLoadSuccess(ArrayList<FeaturedEntity> featuredEntities);
    }
}
