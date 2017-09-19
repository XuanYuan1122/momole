package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.PhoneFukuEntity;
import com.moemoe.lalala.model.entity.PhoneMateEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface PhoneMateContract {
    interface Presenter extends BasePresenter{
        void loadMateInfo();
        void loadFukuInfo(String role);
        void setMate(String role);
        void setFuku(String role,String clothesId);
    }

    interface View extends BaseView{
        void onLoadMateSuccess(ArrayList<PhoneMateEntity> entities);
        void onLoadFukuSuccess(ArrayList<PhoneFukuEntity> entities);
        void setMateSuccess();
        void setFukuSuccess();
    }
}
