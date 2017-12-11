package com.moemoe.lalala.presenter;

import android.content.Context;

import com.moemoe.lalala.dialog.SignDialog;
import com.moemoe.lalala.model.entity.AppUpdateEntity;
import com.moemoe.lalala.model.entity.BuildEntity;
import com.moemoe.lalala.model.entity.DailyTaskEntity;
import com.moemoe.lalala.model.entity.JuQIngStoryEntity;
import com.moemoe.lalala.model.entity.JuQingTriggerEntity;
import com.moemoe.lalala.model.entity.MapEntity;
import com.moemoe.lalala.model.entity.MapMarkContainer;
import com.moemoe.lalala.model.entity.NearUserEntity;
import com.moemoe.lalala.model.entity.NetaEvent;
import com.moemoe.lalala.model.entity.PersonalMainEntity;
import com.moemoe.lalala.model.entity.SignEntity;
import com.moemoe.lalala.model.entity.JuQingDoneEntity;
import com.moemoe.lalala.model.entity.SplashEntity;
import com.moemoe.lalala.model.entity.UserLocationEntity;
import com.moemoe.lalala.view.widget.map.MapWidget;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by yi on 2016/11/27.
 */

public interface MapContract {
    interface Presenter extends BasePresenter{
        void checkVersion();
        void checkBuild(int buildVersion,int appVersion);
        void getEventList();
        void saveEvent(NetaEvent event);
        void getServerTime();
        void getTrigger();
        void getAllStory();
        void checkStoryVersion();
        void findMyDoneJuQing();
        void loadMapPics();
        void addMapMark(Context context,MapMarkContainer container, MapWidget map,String type);
        void addEventMark(String id,String icon,MapMarkContainer container,Context context, MapWidget map,String storyId);
        void loadRcToken();
        void saveUserLocation(UserLocationEntity entity);
        void loadMapAllUser();
        void loadMapBirthdayUser();
        void loadMapEachFollowUser();
        void loadMapTopUser();
        void loadMapNearUser(double lat,double lon);
        void loadSplashList();
    }

    interface View extends BaseView{
        void showUpdateDialog(AppUpdateEntity entity);
        void checkBuildSuccess(BuildEntity s);
        void getEventSuccess(ArrayList<NetaEvent> events);
        void saveEventSuccess();
        void onGetTimeSuccess(Date time);
        void onGetTriggerSuccess(ArrayList<JuQingTriggerEntity> entities);
        void onGetAllStorySuccess(ArrayList<JuQIngStoryEntity> entities);
        void onCheckStoryVersionSuccess(int version);
        void onFindMyDoneJuQingSuccess(ArrayList<JuQingDoneEntity> entities);
        void onLoadMapPics(ArrayList<MapEntity> entities);
        void onLoadRcTokenSuccess(String token);
        void onLoadRcTokenFail(int code,String msg);
        void onLoadMapAllUser(ArrayList<MapEntity> entities);
        void onLoadMapBirthDayUser(ArrayList<MapEntity> entities);
        void onLoadMapEachFollowUser(ArrayList<MapEntity> entities);
        void onLoadMapTopUser(NearUserEntity entities);
        void onLoadMapNearUser(NearUserEntity entities);
        void onLoadSplashSuccess(ArrayList<SplashEntity> entities);
    }
}
