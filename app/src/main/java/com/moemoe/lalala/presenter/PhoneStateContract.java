package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AuthorInfo;
import com.moemoe.lalala.model.entity.LoginEntity;
import com.moemoe.lalala.model.entity.LoginResultEntity;

/**
 * Created by yi on 2016/11/27.
 */

public interface PhoneStateContract {
    interface Presenter extends BasePresenter{
        void checkPhoneCode(int action, AuthorInfo info,String code);
        void login(AuthorInfo info,LoginEntity entity);
    }

    interface View extends BaseView{
        void onRegisterSuccess(AuthorInfo authorInfo);
        void onFindPwdSuccess();
        void onLoginSuccess(AuthorInfo info,LoginResultEntity entity);
    }
}
