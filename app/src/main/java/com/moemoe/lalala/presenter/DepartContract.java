package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.FeaturedEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface DepartContract {
    interface Presenter{
        void requestBannerData(String room);
        void requestFeatured(String room);
        void requestDocList(int index,String room,int type);
    }

    interface View extends BaseView{
        void onBannerLoadSuccess(ArrayList<BannerEntity> bannerEntities);
        void onFeaturedLoadSuccess(ArrayList<FeaturedEntity> featuredEntities);
        void onDocLoadSuccess(Object entity,boolean pull);
        void onChangeSuccess(Object entity);
    }
}
