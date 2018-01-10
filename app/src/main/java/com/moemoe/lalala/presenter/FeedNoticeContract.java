package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.FeedNoticeEntity;
import com.moemoe.lalala.model.entity.FeedRecommendUserEntity;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2016/11/29.
 */

public interface FeedNoticeContract {
    interface Presenter extends BasePresenter{
        void loadFeedNoticeList(String type,long followTime,long notifyTime);
        void likeDynamic(String id,boolean isLike,int position);
        void loadRecommendUserList();
        void followUser(String id, boolean isFollow, int position);
    }

    interface View extends BaseView{
        void onLoadFeedNoticeListSuccess(ArrayList<FeedNoticeEntity> entities,boolean isPull);
        void onLikeDynamicSuccess(boolean isLike,int position);
        void onLoadRecommendUserListSuccess(ArrayList<FeedRecommendUserEntity> entities);
        void onFollowUserSuccess(boolean isFollow,int position);
    }
}
