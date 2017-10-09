package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.JuQingEntity;
import com.moemoe.lalala.model.entity.PhoneMenuEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface PhoneJuQingListContract {
    interface Presenter extends BasePresenter{
        void loadUserList(int level,int type, int index);
    }

    interface View extends BaseView{
        void onLoadUserListSuccess(ArrayList<JuQingEntity> entities, boolean isPull);
    }
}
