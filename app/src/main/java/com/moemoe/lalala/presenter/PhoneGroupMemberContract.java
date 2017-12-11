package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.GroupMemberDelEntity;
import com.moemoe.lalala.model.entity.UserTopEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface PhoneGroupMemberContract {
    interface Presenter extends BasePresenter{
        void loadMemberList(String groupId,int index);
        void delMembers(GroupMemberDelEntity entity);
    }

    interface View extends BaseView{
        void onLoadMemberListSuccess(ArrayList<UserTopEntity> entities,boolean isPull);
        void onDelMembersSuccess();
    }
}
