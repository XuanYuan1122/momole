package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.CalendarDayEntity;

/**
 * Created by yi on 2016/11/29.
 */

public interface CalendarContract {
    interface Presenter extends BasePresenter{
        void doRequest(String day,boolean pull);
    }

    interface View extends BaseView{
        void onSuccess(CalendarDayEntity entities, boolean pull);
    }
}
