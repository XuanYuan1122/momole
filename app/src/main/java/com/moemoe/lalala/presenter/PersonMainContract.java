package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.PersonalMainEntity;

/**
 * Created by yi on 2016/11/29.
 */

public interface PersonMainContract {
    interface Presenter extends BasePresenter{
        void loadInfo(String id);
        void likeMapRole(boolean isLike,String id);
    }

    interface View extends BaseView{
        void onLoadInfoSuccess(PersonalMainEntity entity);
        void onLikeSuccess(boolean isLie);
    }
}
