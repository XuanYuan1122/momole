package com.moemoe.lalala.utils;

import android.util.Xml;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.moemoe.lalala.greendao.gen.JuQIngStoryEntityDao;
import com.moemoe.lalala.greendao.gen.JuQingDoneEntityDao;
import com.moemoe.lalala.greendao.gen.JuQingTriggerEntityDao;
import com.moemoe.lalala.model.entity.CircleTimeTrigger;
import com.moemoe.lalala.model.entity.DeskMateEntity;
import com.moemoe.lalala.model.entity.ImpressionTrigger;
import com.moemoe.lalala.model.entity.JuQingShowEntity;
import com.moemoe.lalala.model.entity.JuQIngStoryEntity;
import com.moemoe.lalala.model.entity.JuQingTriggerEntity;
import com.moemoe.lalala.model.entity.LevelTrigger;
import com.moemoe.lalala.model.entity.PreposeTrigger;
import com.moemoe.lalala.model.entity.ProgressTrigger;
import com.moemoe.lalala.model.entity.SpecificTimeTrigger;

import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

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
        dao.insertOrReplaceInTx(list);
    }

    /**
     * 已完成的剧情
     */
    public static void saveJuQingDone(String id,long time){
        JuQingDoneEntityDao dao = GreenDaoManager.getInstance().getSession().getJuQingDoneEntityDao();
        JuQingDoneEntity entity = new JuQingDoneEntity(id,time);
        dao.insertOrReplace(entity);
    }

    /**
     * 检查是否有满足的剧情
     * @return
     */
    public static String checkJuQing(Calendar calendar){
        String res = "";
        List<JuQingTriggerEntity> list = GreenDaoManager.getInstance().getSession().getJuQingTriggerEntityDao().loadAll();
        for(JuQingTriggerEntity entity : list){
            if("mobile".equals(entity.getType())){//只关注手机剧情
                JsonArray condition = new Gson().fromJson(entity.getConditionStr(),JsonArray.class);
                if(condition.size() > 0){
                    if(checkCondition(condition,entity.getStoryId(),calendar)){
                        res = entity.getStoryId();
                        break;
                    }
                }
            }
        }
        return res;
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
                .where(JuQingDoneEntityDao.Properties.Id.eq(entity.getStory()))
                .unique();
        JuQingDoneEntity entity2 = dao.queryBuilder()
                .where(JuQingDoneEntityDao.Properties.Id.eq(storyId))
                .unique();
        if(entity1 != null && entity2 == null){
            long cur = calendar.getTimeInMillis();
            long between = entity.getDay() * 24 * 60 * 60 * 1000 + entity.getHour() * 60 * 60 * 1000 + entity.getMinute() * 60 * 1000 + entity.getSecond() * 1000;
            if(cur - entity1.getTime() >= between){
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
                .where(JuQingDoneEntityDao.Properties.Id.eq(entity.getStory()))
                .unique();
        if(entity1 != null){
            long cur = calendar.getTimeInMillis();
            long between = entity.getDay() * 24 * 60 * 60 * 1000 + entity.getHour() * 60 * 60 * 1000 + entity.getMinute() * 60 * 1000 + entity.getSecond() * 1000;
            if(cur - entity1.getTime() >= between){
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
        JuQingTriggerEntity entity = GreenDaoManager.getInstance().getSession().getJuQingTriggerEntityDao().queryBuilder()
                .where(JuQingTriggerEntityDao.Properties.StoryId.eq(id))
                .unique();
        if(entity != null){
            res = entity.isForce();
        }

        return res;
    }

    public static ArrayList<JuQingShowEntity> getJuQingShow(String id){
        ArrayList<JuQingShowEntity> resList = new ArrayList<>();
        JuQIngStoryEntity entity = GreenDaoManager.getInstance().getSession().getJuQIngStoryEntityDao().load(id);
        if(entity != null){
            JsonArray array = new Gson().fromJson(entity.getContent(),JsonArray.class);
            for (int i = 0;i < array.size();i++){
                JsonObject json = array.get(i).getAsJsonObject();
                JuQingShowEntity entity1 = new JuQingShowEntity();
                entity1.setIndex(json.get("index").getAsInt());
                entity1.setName(json.get("name").getAsString());
                entity1.setText(json.get("talk").getAsJsonObject().get("text").getAsString());
                entity1.setExtra(entity.getExtra());
                LinkedHashMap<String,Integer> map = new LinkedHashMap<>();
                JsonArray option = json.get("option").getAsJsonArray();
                JsonArray nextnode = json.get("nextnode").getAsJsonArray();
                for(int n = 0;n< option.size();n++){
                    map.put(option.get(n).getAsString(),nextnode.get(n).getAsInt());
                }
                entity1.setChoice(map);
                resList.add(entity1);
            }
        }
        return resList;
    }
}
