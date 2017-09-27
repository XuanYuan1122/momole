package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.InviteUserEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface InviteContract {
    interface Presenter extends BasePresenter{
        void loadList();
        void getUserName(String no);
        void useInviteNo(String no);
    }

    interface View extends BaseView{
        void onLoadListSuccess(ArrayList<InviteUserEntity> entities);
        void onGetUserNameSuccess(String name);
        void onUseNoSuccess();
    }
}
