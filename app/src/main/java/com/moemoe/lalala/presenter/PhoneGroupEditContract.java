package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.GroupEditEntity;

/**
 * Created by yi on 2016/11/29.
 */

public interface PhoneGroupEditContract {
    interface Presenter extends BasePresenter{
        void createGroup(GroupEditEntity editEntity);
        void updateGroup(String id,GroupEditEntity editEntity);
    }

    interface View extends BaseView{
        void onEditSuccess();
    }
}
