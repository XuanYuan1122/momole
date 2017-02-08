package com.moemoe.lalala.presenter;

import android.content.Context;

import com.moemoe.lalala.dialog.SignDialog;
import com.moemoe.lalala.model.entity.AppUpdateEntity;
import com.moemoe.lalala.model.entity.BuildEntity;
import com.moemoe.lalala.model.entity.DailyTaskEntity;
import com.moemoe.lalala.model.entity.MapMarkContainer;
import com.moemoe.lalala.model.entity.PersonalMainEntity;
import com.moemoe.lalala.model.entity.SignEntity;
import com.moemoe.lalala.view.widget.map.MapWidget;

/**
 * Created by yi on 2016/11/27.
 */

public interface MapContract {
    interface Presenter{
        void checkVersion();
        void signToday(SignDialog dialog);
        void addDayMapMark(Context context, MapWidget map, float scale);
        void addNightMapMark(Context context, MapWidget map, float scale);
        void addNightEventMapMark(Context context, MapWidget map, float scale);
        void addSnowman(Context context, MapWidget map);
        void clickSnowman(Object o,int mapX,int mapY);
        void getDailyTask();
        void requestPersonMain();
        void checkBuild(int buildVersion,int appVersion);
    }

    interface View extends BaseView{
        void changeSignState(SignEntity entity, boolean sign);
        void showUpdateDialog(AppUpdateEntity entity);
        void onMapMarkLoaded(MapMarkContainer container);
        void onDailyTaskLoad(DailyTaskEntity entity);
        void onPersonMainLoad(PersonalMainEntity entity);
        void onSnowmanSuccess(Object objectId,int mapX,int mapY);
        void checkBuildSuccess(BuildEntity s);
    }
}
