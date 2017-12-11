package com.moemoe.lalala.presenter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.greendao.gen.MapDbEntityDao;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AppUpdateEntity;
import com.moemoe.lalala.model.entity.BuildEntity;
import com.moemoe.lalala.model.entity.JuQIngStoryEntity;
import com.moemoe.lalala.model.entity.JuQingTriggerEntity;
import com.moemoe.lalala.model.entity.MapDbEntity;
import com.moemoe.lalala.model.entity.MapEntity;
import com.moemoe.lalala.model.entity.MapMarkContainer;
import com.moemoe.lalala.model.entity.MapMarkEntity;
import com.moemoe.lalala.model.entity.NearUserEntity;
import com.moemoe.lalala.model.entity.NetaEvent;
import com.moemoe.lalala.model.entity.SplashEntity;
import com.moemoe.lalala.model.entity.UserLocationEntity;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.GreenDaoManager;
import com.moemoe.lalala.model.entity.JuQingDoneEntity;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.widget.map.MapWidget;
import com.moemoe.lalala.view.widget.map.interfaces.Layer;
import com.moemoe.lalala.view.widget.map.model.MapObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/27.
 */

public class MapPresenter implements MapContract.Presenter {
    private MapContract.View view;
    private ApiService apiService;

    @Inject
    public MapPresenter(MapContract.View view,ApiService apiService){
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void checkVersion() {
        apiService.checkVersion(AppSetting.CHANNEL,AppSetting.VERSION_CODE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<AppUpdateEntity>() {
                    @Override
                    public void onSuccess(AppUpdateEntity appUpdateEntity) {
                        if(appUpdateEntity.getUpdateStatus() != 0){
                            if(view != null) view.showUpdateDialog(appUpdateEntity);
                        }
                    }

                    @Override
                    public void onFail(int code,String msg) {

                    }
                });
    }

    @Override
    public void getServerTime() {
        apiService.getServerTime()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<Date>() {
                    @Override
                    public void onSuccess(Date date) {
                        if(view != null) view.onGetTimeSuccess(date);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void loadRcToken() {
        apiService.loadRcToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if(view != null) view.onLoadRcTokenSuccess(s);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onLoadRcTokenFail(code, msg);
                    }
                });
    }

    @Override
    public void saveUserLocation(UserLocationEntity entity) {
        apiService.saveUserLocation(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
    }

    @Override
    public void loadMapAllUser() {
        apiService.loadMapAllUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<MapEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<MapEntity> entities) {
                        if(view != null) view.onLoadMapAllUser(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void loadMapBirthdayUser() {
        apiService.loadMapBirthdayUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<MapEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<MapEntity> entities) {
                        if(view != null) view.onLoadMapBirthDayUser(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void loadMapEachFollowUser() {
        apiService.loadMapEachFollowUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<MapEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<MapEntity> entities) {
                        if(view != null) view.onLoadMapEachFollowUser(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void loadMapTopUser() {
        apiService.loadMapTopUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<NearUserEntity>() {
                    @Override
                    public void onSuccess(NearUserEntity entities) {
                        if(view != null) view.onLoadMapTopUser(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void loadMapNearUser(double lat, double lon) {
        apiService.loadMapNearUser(lat,lon)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<NearUserEntity>() {
                    @Override
                    public void onSuccess(NearUserEntity entities) {
                        if(view != null) view.onLoadMapNearUser(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void loadSplashList() {
        apiService.loadSplashList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<SplashEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<SplashEntity> splashEntity) {
                        if(view != null) view.onLoadSplashSuccess(splashEntity);
                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
    }

    @Override
    public void getTrigger() {
        apiService.getAllTrigger()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<JuQingTriggerEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<JuQingTriggerEntity> entities) {
                        if(view != null) view.onGetTriggerSuccess(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void getAllStory() {
        apiService.getAllStory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<JuQIngStoryEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<JuQIngStoryEntity> entities) {
                        if(view != null) view.onGetAllStorySuccess(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void checkStoryVersion() {
        apiService.checkStoryVersion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        if(view != null) view.onCheckStoryVersionSuccess(integer);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void findMyDoneJuQing() {
        apiService.getDoneJuQing()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<JuQingDoneEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<JuQingDoneEntity> entities) {
                        if(view != null) view.onFindMyDoneJuQingSuccess(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void addEventMark(String id,String icon,MapMarkContainer container,Context context, MapWidget map,String storyId){
        int iconId = 0;
        int x = 0;
        int y = 0;
        if("daily_len".equals(icon)){
            iconId = R.drawable.btn_event_daily_len;
        }else if("daily_mei".equals(icon)){
            iconId = R.drawable.btn_event_daily_mei;
        }else if("daily_sari".equals(icon)){
            iconId = R.drawable.btn_event_daily_sari;
        }else if("branch_len".equals(icon)){
            iconId = R.drawable.btn_event_branch_len;
        }else if("branch_mei".equals(icon)){
            iconId = R.drawable.btn_event_branch_mei;
        }else if("branch_sari".equals(icon)){
            iconId = R.drawable.btn_event_branch_sari;
        }else if("main_len".equals(icon)){
            iconId = R.drawable.btn_event_main_len;
        }else if("main_mei".equals(icon)){
            iconId = R.drawable.btn_event_main_mei;
        }else if("main_sari".equals(icon)){
            iconId = R.drawable.btn_event_main_sari;
        }else if("summerfestival".equals(icon)){
            iconId = R.drawable.btn_map_event_summerfestival;
        }else if("daily_current".equals(icon)){
            iconId = R.drawable.btn_event_daily_current;
        }else if("branch_current".equals(icon)){
            iconId = R.drawable.btn_event_branch_current;
        }else if("main_current".equals(icon)){
            iconId = R.drawable.btn_event_main_current;
        }

        if("activityroom".equals(id)){
            x = 1295;
            y = 866;
        }else if("classroom".equals(id)){
            x = 1696;
            y = 412;
        }else if("rooftop".equals(id)){
            x = 1493;
            y = 272;
        }else if("mainroad".equals(id)){
            x = 2988;
            y = 1311;
        }else if("corridor".equals(id)){
            x = 1861;
            y = 715;
        }else if("tree".equals(id)){
            x = 441;
            y = 1736;
        }else if("canteen".equals(id)){
            x = 1108;
            y = 1455;
        }else if("principal".equals(id)){
            x = 1877;
            y = 342;
        }else if("coffee".equals(id)){
            x = 873;
            y = 1147;
        }else if("playground".equals(id)){
            x = 865;
            y = 272;
        }else if("warehouse".equals(id)){
            x = 2321;
            y = 342;
        }else if("summerfestival".equals(id)){
            x = 3405;
            y = 1992;
        }else if("library".equals(id)){
            x = 3288;
            y = 1024;
        }
        Layer layer = map.getLayerById(1);

       // if(layer == null){
        // Layer layer = map.createLayer(1);//1 地图剧情
        //  }
        MapObject object = layer.getMapObject("地图剧情" + id);
        if(object == null){
            MapMarkEntity entity1 = new MapMarkEntity("地图剧情" + id,x,y,"neta://com.moemoe.lalala/map_event_1.0?id="+storyId,iconId,140,140);
            container.removeMarkById("地图剧情" + id);
            container.addMark(entity1);
            addMarkToMap(context,entity1,layer);
           // view.onMapEventLoaded(container);
        }
    }

    @Override
    public void addMapMark(Context context,MapMarkContainer container, MapWidget map, String type) {
        addMapMark(context,map,container,type);
       // view.onMapMarkLoaded(container);
    }

    private ArrayList<MapDbEntity> getRandom(ArrayList<MapDbEntity> list,ArrayList<NearUserEntity.Point> posList){
        ArrayList<MapDbEntity> res = new ArrayList<>();
        int size = list.size();
        if(list.size() > posList.size()){
            Collections.shuffle(list);
            size = posList.size();
        }
        for(int i = 0;i < size;i++){
            MapDbEntity entity = list.get(i);
            NearUserEntity.Point point = posList.get(i);
            entity.setPointX(point.getX());
            entity.setPointY(point.getY());
            res.add(entity);
        }
        return res;
    }

    private void addMapMark(Context context, MapWidget map, MapMarkContainer container,String type) {
        Layer layer ;//0 全天可点击事件
        if("map".equals(type)){
            layer = map.createLayer(0);
        }else if("allUser".equals(type)){
            Layer tmp = map.getLayerById(2);
            if(tmp != null) map.removeLayer(2);
            layer = map.createLayer(2);
        }else if("birthdayUser".equals(type)){
            Layer tmp = map.getLayerById(3);
            if(tmp != null) map.removeLayer(3);
            layer = map.createLayer(3);
        }else if("followUser".equals(type)){
            Layer tmp = map.getLayerById(4);
            if(tmp != null) map.removeLayer(4);
            layer = map.createLayer(4);
        }else if("nearUser".equals(type)){
            Layer tmp = map.getLayerById(5);
            if(tmp != null) map.removeLayer(5);
            layer = map.createLayer(5);
        }else if("topUser".equals(type)){
            Layer tmp = map.getLayerById(6);
            if(tmp != null) map.removeLayer(6);
            layer = map.createLayer(6);
        }else {
            Layer tmp = map.getLayerById(100);
            if(tmp != null) map.removeLayer(100);
            layer = map.createLayer(100);
        }
        ArrayList<MapDbEntity> mapPics = (ArrayList<MapDbEntity>) GreenDaoManager.getInstance().getSession().getMapDbEntityDao()
                .queryBuilder()
                .where(MapDbEntityDao.Properties.Type.eq(type))
                .list();
        if(mapPics != null && mapPics.size() > 0){
            if("nearUser".equals(type)){
                String posStr = PreferenceUtils.getNearPosition(context);
                Gson gson = new Gson();
                ArrayList<NearUserEntity.Point> posList = gson.fromJson(posStr, new TypeToken<ArrayList<NearUserEntity.Point>>() {}.getType());
                if(posList != null){
                    mapPics = getRandom(mapPics,posList);
                }
            }else if("topUser".equals(type)){
                String posStr = PreferenceUtils.getTopUserPosition(context);
                Gson gson = new Gson();
                ArrayList<NearUserEntity.Point> posList = gson.fromJson(posStr, new TypeToken<ArrayList<NearUserEntity.Point>>() {}.getType());
                if(posList != null){
                    mapPics = getRandom(mapPics,posList);
                }
            }
            for(MapDbEntity entity : mapPics){
                String time = "-1";
                if(StringUtils.isasa()) {
                    time = "1";
                }
                if(StringUtils.issyougo()) {
                    time = "2";
                }
                if(StringUtils.isgogo()) {
                    time = "3";
                }
                if(StringUtils.istasogare()) {
                    time = "4";
                }
                if(StringUtils.isyoru2()) {
                    time = "5";
                }
                if(StringUtils.ismayonaka()) {
                    time = "6";
                }
                if(entity.getShows().contains(time)){
                    if(entity.getDownloadState() == 2){
                        if(FileUtil.isExists(StorageUtils.getMapRootPath() + entity.getFileName())){
                            MapMarkEntity entity1 = new MapMarkEntity(entity.getName(),entity.getPointX(),entity.getPointY(),entity.getSchema(),entity.getFileName(),entity.getImage_w(),entity.getImage_h());
                            container.addMark(entity1);
                            addMarkToMap(context,entity1,layer);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void loadMapPics() {
        apiService.loadMapPics()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<MapEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<MapEntity> entities) {
                        if(view != null) view.onLoadMapPics(entities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void checkBuild(int buildVersion,int appVersion) {
        apiService.checkBuild(buildVersion,appVersion)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<BuildEntity>() {
                    @Override
                    public void onSuccess(BuildEntity s) {
                        if(view != null) view.checkBuildSuccess(s);
                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
    }

    @Override
    public void getEventList() {
        apiService.getEventList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<NetaEvent>>() {
                    @Override
                    public void onSuccess(ArrayList<NetaEvent> events) {
                        if(view != null) view.getEventSuccess(events);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void saveEvent(NetaEvent event) {
        apiService.saveEvent(event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.saveEventSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    private void addMarkToMap(Context context,MapMarkEntity entity,Layer layer){
        Drawable drawable;
        if(!TextUtils.isEmpty(entity.getPath())){
            drawable = Drawable.createFromPath(StorageUtils.getMapRootPath() + entity.getPath());
        }else {
            if(entity.getBg() == 0) return;
            drawable = ContextCompat.getDrawable(context,entity.getBg());
        }
        if(drawable != null){
            MapObject object = new MapObject(entity.getId()
                    ,drawable
                    ,entity.getX()
                    ,entity.getY()
                    ,0
                    ,0
                    ,true
                    ,true
                    ,entity.getW()
                    ,entity.getH());
            layer.addMapObject(object);
        }else {
            FileUtil.deleteFile(StorageUtils.getMapRootPath() + entity.getPath());
        }
    }

    @Override
    public void release() {
        view = null;
    }
}
