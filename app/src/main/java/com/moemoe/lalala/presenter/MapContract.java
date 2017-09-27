package com.moemoe.lalala.presenter;

import android.content.Context;

import com.moemoe.lalala.dialog.SignDialog;
import com.moemoe.lalala.model.entity.AppUpdateEntity;
import com.moemoe.lalala.model.entity.BuildEntity;
import com.moemoe.lalala.model.entity.DailyTaskEntity;
import com.moemoe.lalala.model.entity.JuQIngStoryEntity;
import com.moemoe.lalala.model.entity.JuQingTriggerEntity;
import com.moemoe.lalala.model.entity.NetaEvent;
import com.moemoe.lalala.model.entity.PersonalMainEntity;
import com.moemoe.lalala.model.entity.SignEntity;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by yi on 2016/11/27.
 */

public interface MapContract {
    interface Presenter extends BasePresenter{
        void checkVersion();
        void signToday(SignDialog dialog);
        void getDailyTask();
        void requestPersonMain();
        void checkBuild(int buildVersion,int appVersion);
        void getEventList();
        void saveEvent(NetaEvent event);
        void getServerTime();
        void getTrigger();
        void getAllStory();
        void checkStoryVersion();
    }

    interface View extends BaseView{
        void changeSignState(SignEntity entity, boolean sign);
        void showUpdateDialog(AppUpdateEntity entity);
        void onDailyTaskLoad(DailyTaskEntity entity);
        void onPersonMainLoad(PersonalMainEntity entity);
        void checkBuildSuccess(BuildEntity s);
        void getEventSuccess(ArrayList<NetaEvent> events);
        void saveEventSuccess();
        void onGetTimeSuccess(Date time);
        void onGetTriggerSuccess(ArrayList<JuQingTriggerEntity> entities);
        void onGetAllStorySuccess(ArrayList<JuQIngStoryEntity> entities);
        void onCheckStoryVersionSuccess(int version);

    }
}
