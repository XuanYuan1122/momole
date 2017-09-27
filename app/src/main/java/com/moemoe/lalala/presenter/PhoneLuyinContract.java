package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.LuYinEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface PhoneLuyinContract {
    interface Presenter extends BasePresenter{
        void loadLuYinList(String type,String roleName,int index);
        void unlockLuYin(String id, int position);
    }

    interface View extends BaseView{
        void onLoadLuYinListSuccess(ArrayList<LuYinEntity> luYinEntities,boolean isPull);
        void onUnlockSuccess(int position);
    }
}
