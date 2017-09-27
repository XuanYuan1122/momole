package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;

/**
 * Created by yi on 2016/11/29.
 */

public interface PhoneTicketContract {
    interface Presenter extends BasePresenter{
        void loadTicketsNum();
    }

    interface View extends BaseView{
        void onLoadTicketsNumSuccess(int num);
    }
}
