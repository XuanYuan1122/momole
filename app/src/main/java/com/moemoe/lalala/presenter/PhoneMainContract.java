package com.moemoe.lalala.presenter;

import com.moemoe.lalala.dialog.SignDialog;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.DailyTaskEntity;
import com.moemoe.lalala.model.entity.PersonalMainEntity;
import com.moemoe.lalala.model.entity.SignEntity;

import java.util.Date;

/**
 * Created by yi on 2016/11/29.
 */

public interface PhoneMainContract {
    interface Presenter extends BasePresenter{
        void getDailyTask();
        void signToday(SignDialog dialog);
        void requestPersonMain();
    }

    interface View extends BaseView{
        void onDailyTaskLoad(DailyTaskEntity entity);
        void changeSignState(SignEntity entity, boolean sign);
        void onPersonMainLoad(PersonalMainEntity entity);
    }
}
