package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.BadgeEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface BadgeContract {
    interface Presenter extends BasePresenter{
        void requestMyBadge(int index);
        void requestAllBadge(int index);
        void saveBadge(ArrayList<String> ids);
        void buyBadge(String id,int position);
    }

    interface View extends BaseView{
        void loadMyBadgeSuccess(ArrayList<BadgeEntity> entities, boolean pull);
        void loadAllBadgeSuccess(ArrayList<BadgeEntity> entities, boolean pull);
        void saveSuccess();
        void buySuccess(int position);
    }
}
