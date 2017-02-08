package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.LoginEntity;
import com.moemoe.lalala.model.entity.LoginResultEntity;

/**
 * Created by yi on 2016/11/27.
 */

public interface LoginContract {
    interface Presenter{
        void login(LoginEntity entity);
        void loginThird(String platform,String DevId);
    }

    interface View extends BaseView{
        void onLoginSuccess(LoginResultEntity entity);
        void onLoginThirdSuccess(String id);
    }
}
