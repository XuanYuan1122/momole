package com.moemoe.lalala.presenter;

/**
 * Created by yi on 2016/11/29.
 */

public interface EditAccountContract {
    interface Presenter{
        void uploadAvatar(String path,int type);
        void modify(String name,String sex,String birthday,String bg,String headPath,String sign);
    }

    interface View extends BaseView{
        void uploadSuccess(String path,int type);
        void uploadFail(int type);
        void modifySuccess();
    }
}
