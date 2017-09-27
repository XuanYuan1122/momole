package com.moemoe.lalala.presenter;


/**
 * Created by yi on 2016/11/29.
 */

public interface CreateForwardContract {
    interface Presenter extends BasePresenter{
        void createForward(int type, Object entity);
    }

    interface View extends BaseView{
        void onCreateForwardSuccess();
    }
}
