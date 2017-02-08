package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AppUpdateEntity;

/**
 * Created by yi on 2016/11/29.
 */

public interface SettingContract {
    interface Presenter{
        void checkVersion();
        void logout();
        void modifySecret(boolean show,int type);
    }

    interface View extends BaseView{
        void showUpdateDialog(AppUpdateEntity entity);
        void logoutSuccess();
        void modifySecretFail(int type);
        void noUpdate();
    }
}
