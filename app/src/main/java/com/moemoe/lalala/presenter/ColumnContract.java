package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.CalendarDayItemEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface ColumnContract {
    interface Presenter{
        void requestFutureFresh(String barId,int index);
        void requestPastFresh(String barId,int index);
    }

    interface View extends BaseView{
        void loadColumnFutureData(ArrayList<CalendarDayItemEntity> calendarDayItemEntities);
        void loadColumnPastData(ArrayList<CalendarDayItemEntity> calendarDayItemEntities);
    }
}
