package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.FeaturedEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface BaseBannerContract {
    interface Presenter extends BasePresenter{
        void requestBannerData(String room);
        void requestFeatured(String room);
    }

    interface View extends BaseView{
        void onBannerLoadSuccess(ArrayList<BannerEntity> bannerEntities);
        void onFeaturedLoadSuccess(ArrayList<FeaturedEntity> featuredEntities);
    }
}
