package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.UserFollowTagEntity;

import java.util.ArrayList;

/**
 * feed流关注页接口
 * Created by yi on 2018/1/11
 */

public interface FeedFollowContract {
    interface Presenter extends BasePresenter{
        void loadUserTags();
    }

    interface View extends BaseView{
        void onLoadUserTagsSuccess(ArrayList<UserFollowTagEntity> entities);
    }
}
