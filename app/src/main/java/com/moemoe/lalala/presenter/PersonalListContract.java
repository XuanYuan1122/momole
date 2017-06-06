package com.moemoe.lalala.presenter;

/**
 * Created by yi on 2016/11/29.
 */

public interface PersonalListContract {
    interface Presenter extends BasePresenter{
        void doRequest(String id,int index,int type);
    }

    interface View extends BaseView{
        void onSuccess(Object o,boolean isPull);
    }
}
