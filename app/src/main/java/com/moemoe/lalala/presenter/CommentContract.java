package com.moemoe.lalala.presenter;

/**
 * Created by yi on 2016/11/29.
 */

public interface CommentContract {
    interface Presenter{
        void doRequest(int data,int type);
    }

    interface View extends BaseView{
        void onSuccess(Object entities,boolean pull);
        void onChangeSuccess(Object entities);
    }
}
