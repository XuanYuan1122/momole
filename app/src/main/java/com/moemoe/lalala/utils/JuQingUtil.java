package com.moemoe.lalala.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Xml;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.greendao.gen.JuQIngStoryEntityDao;
import com.moemoe.lalala.greendao.gen.JuQingDoneEntityDao;
import com.moemoe.lalala.greendao.gen.JuQingTriggerEntityDao;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.CircleTimeTrigger;
import com.moemoe.lalala.model.entity.DeskMateEntity;
import com.moemoe.lalala.model.entity.ImpressionTrigger;
import com.moemoe.lalala.model.entity.JuQingMapShowEntity;
import com.moemoe.lalala.model.entity.JuQingShowEntity;
import com.moemoe.lalala.model.entity.JuQIngStoryEntity;
import com.moemoe.lalala.model.entity.JuQingTriggerEntity;
import com.moemoe.lalala.model.entity.LevelTrigger;
import com.moemoe.lalala.model.entity.MapDbEntity;
import com.moemoe.lalala.model.entity.PreposeTrigger;
import com.moemoe.lalala.model.entity.ProgressTrigger;
import com.moemoe.lalala.model.entity.SpecificTimeTrigger;

import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import zlc.season.rxdownload2.RxDownload;
import zlc.season.rxdownload2.entity.DownloadStatus;

/**
 * Created by yi on 2017/9/27.
 */

public class JuQingUtil {

    /**
     * 剧情触发器
     * @param entity
     */
    public static void saveJuQingTrigger(JuQingTriggerEntity entity){
        JuQingTriggerEntityDao dao = GreenDaoManager.getInstance().getSession().getJuQingTriggerEntityDao();
        dao.insertOrReplace(entity);
    }

    /**
     * 剧情触发器list
     * @param list
     */
    public static void saveJuQingTriggerList(ArrayList<JuQingTriggerEntity> list){
        GreenDaoManager.getInstance().getSession().getJuQingTriggerEntityDao().deleteAll();
        for (JuQingTriggerEntity entity : list){
            entity.setConditionStr(entity.getCondition().toString());
            saveJuQingTrigger(entity);
        }
    }

    /**
     * 剧情
     * @param entity
     */
    public static void saveJuQingStory(JuQIngStoryEntity entity){
        JuQIngStoryEntityDao dao = GreenDaoManager.getInstance().getSession().getJuQIngStoryEntityDao();
        dao.insertOrReplace(entity);
    }

    /**
     * 剧情list
     * @param list
     */
    public static void saveJuQingStoryList(ArrayList<JuQIngStoryEntity> list){
        JuQIngStoryEntityDao dao = GreenDaoManager.getInstance().getSession().getJuQIngStoryEntityDao();
        dao.deleteAll();
        dao.insertOrReplaceInTx(list);
    }

    /**
     * 已完成的剧情
     */
    public static void saveJuQingDone(ArrayList<JuQingDoneEntity> entities){
        if(entities.size() > 0){
            JuQingDoneEntityDao dao = GreenDaoManager.getInstance().getSession().getJuQingDoneEntityDao();
            dao.deleteAll();
            dao.insertOrReplaceInTx(entities);
        }
    }

    /**
     * 已完成的剧情
     */
    public static void saveJuQingDone(String id,long time){
        JuQingDoneEntityDao dao = GreenDaoManager.getInstance().getSession().getJuQingDoneEntityDao();
        JuQingDoneEntity entity = new JuQingDoneEntity(id,time,"");
        dao.insertOrReplace(entity);
    }

    /**
     *     String res = "";
     String name = "";
     String type = "";
     String extra = "";
     * 检查是否有满足的剧情
     * @return
     */
    public static  ArrayList<JuQingTriggerEntity> checkJuQingAll(Calendar calendar){
        ArrayList<JuQingTriggerEntity> res = new ArrayList<>();
        List<JuQingTriggerEntity> list = GreenDaoManager.getInstance().getSession().getJuQingTriggerEntityDao().loadAll();
        for(JuQingTriggerEntity entity : list){
            JuQingDoneEntity tmp = GreenDaoManager.getInstance().getSession().getJuQingDoneEntityDao().load(entity.getStoryId());
            if(tmp != null) continue;
            JsonArray condition = new Gson().fromJson(entity.getConditionStr(),JsonArray.class);
            if(condition.size() > 0){
                try {
                    if(checkCondition(condition,entity.getStoryId(),calendar)){
                        res.add(entity);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    public static String[] checkJuQingMobile(Calendar calendar,String role){//{"map": "111"}
        String res = "";
        String name = "";
        List<JuQingTriggerEntity> list = GreenDaoManager.getInstance().getSession().getJuQingTriggerEntityDao().loadAll();
        for(JuQingTriggerEntity entity : list){
            if("mobile".equals(entity.getType())){//只关注手机剧情
                JuQingDoneEntity tmp = GreenDaoManager.getInstance().getSession().getJuQingDoneEntityDao().load(entity.getStoryId());
                if(tmp != null) continue;
                JsonArray condition = new Gson().fromJson(entity.getConditionStr(),JsonArray.class);
                if(condition.size() > 0){
                    if(checkCondition(condition,entity.getStoryId(),calendar) && entity.getRoleOf().equals(role)){
                        res = entity.getStoryId();
                        name = entity.getRoleOf();
                        break;
                    }
                }
            }
        }
        return new String[]{res,name};
    }

    public static String[] checkJuQingMobile(Calendar calendar){//{"map": "111"}
        String res = "";
        String name = "";
        List<JuQingTriggerEntity> list = GreenDaoManager.getInstance().getSession().getJuQingTriggerEntityDao().loadAll();
        for(JuQingTriggerEntity entity : list){
            if("mobile".equals(entity.getType())){//只关注手机剧情
                JuQingDoneEntity tmp = GreenDaoManager.getInstance().getSession().getJuQingDoneEntityDao().load(entity.getStoryId());
                if(tmp != null) continue;
                JsonArray condition = new Gson().fromJson(entity.getConditionStr(),JsonArray.class);
                if(condition.size() > 0){
                    if(checkCondition(condition,entity.getStoryId(),calendar)){
                        res = entity.getStoryId();
                        name = entity.getRoleOf();
                        break;
                    }
                }
            }
        }
        return new String[]{res,name};
    }

    /**
     * 检查是否有满足的剧情
     * @return
     */
    public static String[] checkJuQingAll(Calendar calendar, String role){
        String res = "";
        String name = "";
        List<JuQingTriggerEntity> list = GreenDaoManager.getInstance().getSession().getJuQingTriggerEntityDao().loadAll();
        for(JuQingTriggerEntity entity : list){
            if("mobile".equals(entity.getType()) && entity.getRoleOf().equals(role)){//只关注手机剧情,角色名
                JsonArray condition = new Gson().fromJson(entity.getConditionStr(),JsonArray.class);
                if(condition.size() > 0){
                    if(checkCondition(condition,entity.getStoryId(),calendar)){
                        res = entity.getStoryId();
                        name = entity.getRoleOf();
                        break;
                    }
                }
            }
        }
        return new String[]{res,name};
    }

    public static boolean checkCondition(JsonArray condition,String storyId,Calendar calendar){
        boolean res = true;
        for(int i = 0;i < condition.size();i++){
            JsonObject item = condition.get(i).getAsJsonObject();
            String type = item.get("type").getAsString();
            if("circleTime".equals(type)){//循环时间
                res = res && checkCircleTime(item,calendar);
            }else if("specificTime".equals(type)){//具体时间
                res = res && checkSpecificTime(item,calendar);
            }else if("progress".equals(type)){//剧情进度
                res = res && checkProgressTime(item,storyId,calendar);
            }else if("prepose".equals(type)){//前置剧情
                res = res && checkPreposeTime(item,calendar);
            }else if("impression".equals(type)){//好感度
                res = res && checkImpressionTime(item);
            }else if("level".equals(type)){//等级
                res = res && checkLevelTime(item);
            }else if("weather".equals(type)){//天气
                //TODO 目前没有天气
            }
        }
        return res;
    }

    public static boolean checkCircleTime(JsonObject item,Calendar calendar){
        boolean res = false;
        CircleTimeTrigger entity = new Gson().fromJson(item,CircleTimeTrigger.class);
        String start = StringUtils.addZero(entity.getStartHour()) + ":" + StringUtils.addZero(entity.getStartMinute()) + ":" + StringUtils.addZero(entity.getStartSecond());
        String end = StringUtils.addZero(entity.getEndHour()) + ":" + StringUtils.addZero(entity.getEndMinute()) + ":" + StringUtils.addZero(entity.getEndSecond());
        if(StringUtils.matchCurrentTime(calendar,start,end)){
            if(entity.getWeek() == 0){
                res = true;
            }else {
                Calendar time = Calendar.getInstance();
                int week = time.get(Calendar.DAY_OF_WEEK) - 1;
                if(week == 0) week = 7;
                if(week == entity.getWeek()){
                    res = true;
                }
            }
        }
        return res;
    }

    public static boolean checkSpecificTime(JsonObject item,Calendar calendar){
        boolean res = false;
        SpecificTimeTrigger entity = new Gson().fromJson(item,SpecificTimeTrigger.class);
        String start = StringUtils.addZero(entity.getStartHour()) + ":" + StringUtils.addZero(entity.getStartMinute()) + ":" + StringUtils.addZero(entity.getStartSecond());
        String end = StringUtils.addZero(entity.getEndHour()) + ":" + StringUtils.addZero(entity.getEndMinute()) + ":" + StringUtils.addZero(entity.getEndSecond());
        if(StringUtils.matchCurrentTime(calendar,start,end) && StringUtils.matchYear(calendar,Integer.valueOf(entity.getStartYear()),Integer.valueOf(entity.getEndYear()))){
            res = true;
        }
        return res;
    }

    public static boolean checkProgressTime(JsonObject item,String storyId,Calendar calendar){
        boolean res = false;
        ProgressTrigger entity = new Gson().fromJson(item,ProgressTrigger.class);
        JuQingDoneEntityDao dao = GreenDaoManager.getInstance().getSession().getJuQingDoneEntityDao();
        JuQingDoneEntity entity1 = dao.queryBuilder()
                .where(JuQingDoneEntityDao.Properties.StoryId.eq(entity.getStory()))
                .unique();
        JuQingDoneEntity entity2 = dao.queryBuilder()
                .where(JuQingDoneEntityDao.Properties.StoryId.eq(storyId))
                .unique();
        if(entity1 != null && entity2 == null){
            long cur = calendar.getTimeInMillis();
            long between = entity.getDay() * 24 * 60 * 60 * 1000 + entity.getHour() * 60 * 60 * 1000 + entity.getMinute() * 60 * 1000 + entity.getSecond() * 1000;
            if(cur - entity1.getTimestamp() >= between){
                res = true;
            }
        }
        return res;
    }

    public static boolean checkPreposeTime(JsonObject item,Calendar calendar){
        boolean res = false;
        PreposeTrigger entity = new Gson().fromJson(item,PreposeTrigger.class);
        JuQingDoneEntityDao dao = GreenDaoManager.getInstance().getSession().getJuQingDoneEntityDao();
        JuQingDoneEntity entity1 = dao.queryBuilder()
                .where(JuQingDoneEntityDao.Properties.StoryId.eq(entity.getStory()))
                .unique();
        if(entity1 != null){
            long cur = calendar.getTimeInMillis();
            long between = entity.getDay() * 24 * 60 * 60 * 1000 + entity.getHour() * 60 * 60 * 1000 + entity.getMinute() * 60 * 1000 + entity.getSecond() * 1000;
            if(cur - entity1.getTimestamp() >= between){
                res = true;
            }
        }
        return res;
    }

    public static boolean checkImpressionTime(JsonObject item){
        boolean res = false;
        ImpressionTrigger entity = new Gson().fromJson(item,ImpressionTrigger.class);
        ArrayList<DeskMateEntity> list = PreferenceUtils.getAuthorInfo().getDeskMateEntities();
        for(DeskMateEntity tmp : list){
            if(tmp.getRoleOf().equals(entity.getRoleOf()) && tmp.getLikes() >= entity.getLikes()){
                res = true;
            }
        }
        return res;
    }

    public static boolean checkLevelTime(JsonObject item){
        boolean res = false;
        LevelTrigger entity = new Gson().fromJson(item,LevelTrigger.class);
        int level = PreferenceUtils.getAuthorInfo().getLevel();
        if(level >= entity.getLevel()){
            res = true;
        }
        return res;
    }

    public static boolean isForce(String id){
        boolean res = false;
        try {
            JuQingTriggerEntity entity = GreenDaoManager.getInstance().getSession().getJuQingTriggerEntityDao().queryBuilder()
                    .where(JuQingTriggerEntityDao.Properties.StoryId.eq(id))
                    .unique();
            if(entity != null){
                res = entity.isForce();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static int getLevel(String id){
        JuQIngStoryEntity entity = GreenDaoManager.getInstance().getSession().getJuQIngStoryEntityDao().load(id);
        if(entity != null) return entity.getLevel();
        return -1;
    }

    public static ArrayList<JuQingMapShowEntity> getMapEventShow(String id){
        ArrayList<JuQingMapShowEntity> res = new ArrayList<>();
        JuQIngStoryEntity entity = GreenDaoManager.getInstance().getSession().getJuQIngStoryEntityDao().load(id);
        if(entity != null) {
            JsonArray array = new Gson().fromJson(entity.getJson(), JsonArray.class);
            for (int i = 0;i < array.size();i++){
                JsonObject json = array.get(i).getAsJsonObject();
                JuQingMapShowEntity entity1 = new JuQingMapShowEntity();
                entity1.setIndex(json.get("index").getAsInt());
                entity1.setName(json.get("name").getAsString());

                JsonObject vol = json.get("vol").getAsJsonObject();
                entity1.getVol().setFile(vol.get("file").getAsString());
                if(vol.has("md5")) entity1.getVol().setMd5(vol.get("md5").getAsString());
                if(!"无".equals(entity1.getVol().getFile())){
                    String name = entity1.getVol().getFile().substring(entity1.getVol().getFile().lastIndexOf("/") + 1);
                    if(!name.endsWith(".temp.kira")){
                        name = name.substring(0,name.indexOf(".")) + ".temp.kira";   //png.kira
                    }
                    entity1.getVol().setLocalPath(StorageUtils.getMapRootPath() + name);
                }

                JsonObject bgm = json.get("bgm").getAsJsonObject();
                entity1.getBgm().setFile(bgm.get("file").getAsString());
                if(bgm.has("md5")) entity1.getBgm().setMd5(bgm.get("md5").getAsString());
                if(!"无".equals(entity1.getBgm().getFile())){
                    String name = entity1.getBgm().getFile().substring(entity1.getBgm().getFile().lastIndexOf("/") + 1);
                    if(!name.endsWith(".temp.kira")){
                        name = name.substring(0,name.indexOf(".")) + ".temp.kira";   //png.kira
                    }
                    entity1.getBgm().setLocalPath(StorageUtils.getMapRootPath() + name);
                }

                JsonObject talk = json.get("talk").getAsJsonObject();
                entity1.getTalk().setText(talk.get("text").getAsString());
                entity1.getTalk().setEffect(talk.get("effect").getAsString());

                JsonObject pose = json.get("character_pose").getAsJsonObject();
                entity1.getPose().setFile(pose.get("file").getAsString());
                if(pose.has("md5")) entity1.getPose().setMd5(pose.get("md5").getAsString());
                entity1.getPose().setEffect(pose.get("effect").getAsString());
                if(!"无".equals(entity1.getPose().getFile())){
                    String name = entity1.getPose().getFile().substring(entity1.getPose().getFile().lastIndexOf("/") + 1);
                    if(!name.endsWith(".temp.kira")){
                        name = name.substring(0,name.indexOf(".")) + ".temp.kira";   //png.kira
                    }
                    entity1.getPose().setLocalPath(StorageUtils.getMapRootPath() + name);
                }

                JsonObject face = json.get("character_face").getAsJsonObject();
                entity1.getFace().setFile(face.get("file").getAsString());
                if(face.has("md5")) entity1.getFace().setMd5(face.get("md5").getAsString());
                entity1.getFace().setEffect(face.get("effect").getAsString());
                if(!"无".equals(entity1.getFace().getFile())){
                    String name = entity1.getFace().getFile().substring(entity1.getFace().getFile().lastIndexOf("/") + 1);
                    if(!name.endsWith(".temp.kira")){
                        name = name.substring(0,name.indexOf(".")) + ".temp.kira";   //png.kira
                    }
                    entity1.getFace().setLocalPath(StorageUtils.getMapRootPath() + name);
                }

                JsonObject extra = json.get("character_extra").getAsJsonObject();
                entity1.getExtra().setFile(extra.get("file").getAsString());
                if(extra.has("md5")) entity1.getExtra().setMd5(extra.get("md5").getAsString());
                entity1.getExtra().setEffect(extra.get("effect").getAsString());
                if(!"无".equals(entity1.getExtra().getFile())){
                    String name = entity1.getExtra().getFile().substring(entity1.getExtra().getFile().lastIndexOf("/") + 1);
                    if(!name.endsWith(".temp.kira")){
                        name = name.substring(0,name.indexOf(".")) + ".temp.kira";   //png.kira
                    }
                    entity1.getExtra().setLocalPath(StorageUtils.getMapRootPath() + name);
                }

                JsonObject cg = json.get("CG").getAsJsonObject();
                entity1.getCg().setFile(cg.get("file").getAsString());
                if(cg.has("md5")) entity1.getCg().setMd5(cg.get("md5").getAsString());
                entity1.getCg().setEffect(cg.get("effect").getAsString());
                if(!"无".equals(entity1.getCg().getFile())){
                    String name = entity1.getCg().getFile().substring(entity1.getCg().getFile().lastIndexOf("/") + 1);
                    if(!name.endsWith(".temp.kira")){
                        name = name.substring(0,name.indexOf(".")) + ".temp.kira";   //png.kira
                    }
                    entity1.getCg().setLocalPath(StorageUtils.getMapRootPath() + name);
                }

                JsonObject bg = json.get("background").getAsJsonObject();
                entity1.getBg().setFile(bg.get("file").getAsString());
                if(bg.has("md5")) entity1.getBg().setMd5(bg.get("md5").getAsString());
                entity1.getBg().setEffect(bg.get("effect").getAsString());
                if(!"无".equals(entity1.getBg().getFile())){
                    String name = entity1.getBg().getFile().substring(entity1.getBg().getFile().lastIndexOf("/") + 1);
                    if(!name.endsWith(".temp.kira")){
                        name = name.substring(0,name.indexOf(".")) + ".temp.kira";   //png.kira
                    }
                    entity1.getBg().setLocalPath(StorageUtils.getMapRootPath() + name);
                }

                JsonObject item = json.get("item").getAsJsonObject();
                entity1.getItem().setFile(item.get("file").getAsString());
                if(item.has("md5")) entity1.getItem().setMd5(item.get("md5").getAsString());
                entity1.getItem().setEffect(item.get("effect").getAsString());
                if(!"无".equals(entity1.getItem().getFile())){
                    String name = entity1.getItem().getFile().substring(entity1.getItem().getFile().lastIndexOf("/") + 1);
                    if(!name.endsWith(".temp.kira")){
                        name = name.substring(0,name.indexOf(".")) + ".temp.kira";   //png.kira
                    }
                    entity1.getItem().setLocalPath(StorageUtils.getMapRootPath() + name);
                }

                if(json.has("option")){
                    LinkedHashMap<String,Integer> map = new LinkedHashMap<>();
                    JsonArray option = json.get("option").getAsJsonArray();
                    JsonArray nextnode = json.get("nextnode").getAsJsonArray();
                    for(int n = 0;n< option.size();n++){
                        map.put(option.get(n).getAsString(),nextnode.get(n).getAsInt());
                    }
                    entity1.setChoice(map);
                }else {
                    entity1.setChoice(new LinkedHashMap<String, Integer>());
                }

                res.add(entity1);
            }
        }
        return res;
    }

    public static ArrayList<JuQingShowEntity> getJuQingShow(String id){
        ArrayList<JuQingShowEntity> resList = new ArrayList<>();
        JuQIngStoryEntity entity = GreenDaoManager.getInstance().getSession().getJuQIngStoryEntityDao().load(id);
        if(entity != null){
            JsonArray array = new Gson().fromJson(entity.getJson(),JsonArray.class);
            for (int i = 0;i < array.size();i++){
                JsonObject json = array.get(i).getAsJsonObject();
                JuQingShowEntity entity1 = new JuQingShowEntity();
                entity1.setIndex(json.get("index").getAsInt());
                entity1.setName(json.get("name").getAsString());
                entity1.setText(json.get("talk").getAsJsonObject().get("text").getAsString());
                entity1.setExtra(entity.getExtra());
                if(json.has("option")){
                    LinkedHashMap<String,Integer> map = new LinkedHashMap<>();
                    JsonArray option = json.get("option").getAsJsonArray();
                    JsonArray nextnode = json.get("nextnode").getAsJsonArray();
                    for(int n = 0;n< option.size();n++){
                        map.put(option.get(n).getAsString(),nextnode.get(n).getAsInt());
                    }
                    entity1.setChoice(map);
                }else {
                    entity1.setChoice(new LinkedHashMap<String, Integer>());
                }
                resList.add(entity1);
            }
        }
        return resList;
    }

    public static void downLoadFiles(Context context, HashSet<String> entities, Observer<String> callback){//1.未下载 2.下载完成 3.下载失败
        final RxDownload downloadSub = RxDownload.getInstance(context)
                .maxThread(6)
                .maxRetryCount(6)
                .defaultSavePath(StorageUtils.getMapRootPath())
                .retrofit(MoeMoeApplication.getInstance().getNetComponent().getRetrofit());
        Observable.fromIterable(entities)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(@NonNull final String str) throws Exception {
                        return  Observable.create(new ObservableOnSubscribe<String>() {
                            @Override
                            public void subscribe(@NonNull final ObservableEmitter<String> res) throws Exception {
                                String name = str.substring(str.lastIndexOf("/") + 1);
                                if(!name.endsWith(".temp.kira")){
                                    name = name.substring(0,name.indexOf(".")) + ".temp.kira";   //png.kira
                                }
                                final String finalName = name;
                                downloadSub.download(ApiService.URL_QINIU + str,name)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(Schedulers.io())
                                        .subscribe(new Observer<DownloadStatus>() {

                                            private Disposable disposable;

                                            @Override
                                            public void onError(Throwable e) {
                                                downloadSub.deleteServiceDownload(ApiService.URL_QINIU + str,true).subscribe();
                                                res.onError(e);
                                                if(disposable != null && !disposable.isDisposed()){
                                                    disposable.dispose();
                                                }
                                            }

                                            @Override
                                            public void onComplete() {
                                                downloadSub.deleteServiceDownload(ApiService.URL_QINIU + str,false).subscribe();
                                                res.onNext(finalName);
                                                res.onComplete();
                                            }

                                            @Override
                                            public void onSubscribe(@NonNull Disposable d) {
                                                disposable = d;
                                            }

                                            @Override
                                            public void onNext(DownloadStatus downloadStatus) {

                                            }
                                        });
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }


}
