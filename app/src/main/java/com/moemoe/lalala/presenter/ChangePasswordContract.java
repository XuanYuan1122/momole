package com.moemoe.lalala.presenter;

/**
 * Created by yi on 2016/11/29.
 */

public interface ChangePasswordContract {
    interface Presenter extends BasePresenter{
        void changePassword(String oldPwd,String newPwd);
        void resetPwdByCode(String phone,String pwd);
    }

    interface View extends BaseView{
        void onChangeSuccess();
    }
}
