package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.GroupEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface PhoneGroupListContract {
    interface Presenter extends BasePresenter{
        void loadGroupList(int index,boolean isUser);
    }

    interface View extends BaseView{
        void onLoadGroupListSuccess(ArrayList<GroupEntity> entities,boolean isPull);
    }
}
