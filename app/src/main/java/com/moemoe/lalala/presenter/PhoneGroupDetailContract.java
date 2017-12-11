package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.GroupEntity;

/**
 * Created by yi on 2016/11/29.
 */

public interface PhoneGroupDetailContract {
    interface Presenter extends BasePresenter{
        void loadGroupInfo(String id);
        void applyJoinGroup(String id);
        void quitGroup(String id);
        void dismissGroup(String id);
        void joinAuthor(String id);
        void inviteJoin(String userId,String groupId);
    }

    interface View extends BaseView{
        void onLoadGroupInfoSuccess(GroupEntity entity);
        void onApplySuccess();
        void onQuitOrDismissSuccess(boolean isQuit);
    }
}
