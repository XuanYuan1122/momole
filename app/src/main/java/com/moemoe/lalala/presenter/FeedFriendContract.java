package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.DiscoverEntity;
import com.moemoe.lalala.model.entity.FeedRecommendUserEntity;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2016/11/29.
 */

public interface FeedFriendContract {
    interface Presenter extends BasePresenter{
        void likeDynamic(String id,boolean isLike,int position);
        void loadRecommendUserList();
        void followUser(String id, boolean isFollow, int position);
        void loadDiscoverList(long time);
    }

    interface View extends BaseView{
        void onLikeDynamicSuccess(boolean isLike,int position);
        void onLoadRecommendUserListSuccess(ArrayList<FeedRecommendUserEntity> entities);
        void onFollowUserSuccess(boolean isFollow,int position);
        void onLoadDiscoverListSuccess(ArrayList<DiscoverEntity> entities, boolean isPull);
    }
}
