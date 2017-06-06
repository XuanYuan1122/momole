package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.CreatePrivateMsgEntity;
import com.moemoe.lalala.model.entity.UserInfo;

/**
 * Created by yi on 2016/11/29.
 */

public interface PersonalContract {
    interface Presenter extends BasePresenter{
        void requestUserInfo(String id);
        void followUser(String id,boolean isFollow);
        void createPrivateMsg(String userId);
    }

    interface View extends BaseView{
        void onLoadUserInfo(UserInfo info);
        void onFollowSuccess(boolean isFollow);
        void onLoadUserInfoFail();
        void onCreatePrivateMsgSuccess(CreatePrivateMsgEntity entity);
    }
}
