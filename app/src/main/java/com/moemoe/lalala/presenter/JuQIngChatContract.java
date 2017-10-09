package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;

/**
 * Created by yi on 2016/11/29.
 */

public interface JuQIngChatContract {
    interface Presenter extends BasePresenter{
        void doneJuQing(String id);
    }

    interface View extends BaseView{
        void onDoneSuccess(long time);
    }
}
