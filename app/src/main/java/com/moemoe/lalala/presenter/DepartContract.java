package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.DepartmentGroupEntity;
import com.moemoe.lalala.model.entity.FeaturedEntity;
import com.moemoe.lalala.model.entity.SendSubmissionEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface DepartContract {
    interface Presenter extends BasePresenter{
        void requestBannerData(String room);
        void requestFeatured(String room);
        void requestDocList(int index,String room,int type);
        void followDepartment(String id, boolean follow);
        void loadIsFollow(String id);
        void submission(SendSubmissionEntity entity);
        void loadDepartmentGroup(String id);
        void joinAuthor(String id,String name);
    }

    interface View extends BaseView{
        void onBannerLoadSuccess(ArrayList<BannerEntity> bannerEntities);
        void onFeaturedLoadSuccess(ArrayList<FeaturedEntity> featuredEntities);
        void onDocLoadSuccess(Object entity,boolean pull);
        void onChangeSuccess(Object entity);
        void onFollowDepartmentSuccess(boolean follow);
        void onSubmissionSuccess();
        void onLoadGroupSuccess(ArrayList<DepartmentGroupEntity> entity);
        void onJoinSuccess(String id,String name);
    }
}
