package com.moemoe.lalala.data;

import android.content.Context;
import android.text.TextUtils;

import com.moemoe.lalala.R;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/4/21 0021.
 */
public class CalendarDayItem {


    public String type;// BANNER_X55 | BAR | RSS | DOC_V_1 | DOC_V_2 | DOC_G_2 | DOC_H_1
    public CalendarData data;

    public static ArrayList<CalendarDayItem> readFromJsonList(Context context,String jsonStr){
        ArrayList<CalendarDayItem> items = new ArrayList<>(0);
        try {
            JSONArray array = new JSONArray(jsonStr);
            if(array != null){
                for(int i = 0;i < array.length();i++){
                    JSONObject json = array.optJSONObject(i);
                    CalendarDayItem item = new CalendarDayItem();
                    item.readFromJsonContent(context,json.toString());
                    items.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  items;
    }

    public void readFromJsonContent(Context context,String jsonStr){
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            if(jsonObject != null){
                type = jsonObject.optString("type");
                if(type.equals(CalendarDayType.BANNER_X55.value)){
                    data = new CalendarDayBanner();
                    data.readFromJsonContent(context,jsonObject.optString("data"));
                }else if(type.equals(CalendarDayType.BAR.value)){
                    data = new CalendarDayBar();
                    data.readFromJsonContent(context,jsonObject.optString("data"));
                }else if(type.equals(CalendarDayType.RSS.value)){
                    data = new CalendarDayRss();
                    data.readFromJsonContent(context,jsonObject.optString("data"));
                }else if(type.equals(CalendarDayType.DOC_V_1.value) || type.equals(CalendarDayType.DOC_V_2.value) || type.equals(CalendarDayType.DOC_G_2.value ) || type.equals(CalendarDayType.DOC_V_3.value )){
                    data = new CalendarDoc();
                    data.readFromJsonContent(context,jsonObject.optString("data"));
                }else if(type.equals(CalendarDayType.DOC_H_1.value)){
                    data = new CalendarDayDocH1();
                    data.readFromJsonContent(context,jsonObject.optString("data"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static class  CalendarData{
        public void readFromJsonContent(Context context,String jsonStr){
        }
    }

    public class CalendarDayBanner extends CalendarData{
        public ArrayList<CalendarDayBannerItem> items;

        public CalendarDayBanner(){
            items = new ArrayList<>();
        }

        @Override
        public void readFromJsonContent(Context context,String jsonStr){
            try {
                JSONObject json = new JSONObject(jsonStr);
                JSONArray array = json.optJSONArray("items");
                if(array != null){
                    for(int i = 0;i < array.length();i++){
                        JSONObject jsonObject = array.optJSONObject(i);
                        CalendarDayBannerItem rss = new CalendarDayBannerItem();
                        rss.readFromJsonContent(context,jsonObject.toString());
                        items.add(rss);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class CalendarDayBannerItem{
        public Image image;
        public String targetType;
        public String targetId;

        public CalendarDayBannerItem(){
            image = new Image();
        }

        public void readFromJsonContent(Context context,String jsonStr){
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                targetType = jsonObject.optString("targetType");
                targetId = jsonObject.optString("targetId");
                JSONObject json = jsonObject.optJSONObject("image");
                if(json != null){
                    image.real_path = Otaku.URL_QINIU + json.optString("path");
                    image.w = json.optInt("w");
                    image.h = json.optInt("h");
                    image.path = StringUtils.getUrl(context,json.optString("path"),image.w,image.h);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public class CalendarDayBar extends CalendarData{
        public String id;
        public String name;
        public String title;
        public Image icon;
        public boolean refresh;
        public boolean titleVisible;
        public String refreshName;
        public int pageSize;
        public int curIndex = -1;
        public int refreshPosition = -1;

        public CalendarDayBar(){
            icon = new Image();
        }

        @Override
        public void readFromJsonContent(Context context,String jsonStr){
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(jsonStr);
                id = jsonObject.optString("id");
                title = jsonObject.optString("title");
                name = jsonObject.optString("name");
                refreshName = jsonObject.optString("refreshName");
                refresh = jsonObject.optBoolean("refresh");
                pageSize = jsonObject.optInt("pageSize");
                titleVisible = jsonObject.optBoolean("titleVisible");
                JSONObject json = jsonObject.optJSONObject("icon");
                if(json != null){
                    icon.real_path = Otaku.URL_QINIU + json.optString("path");
                    icon.w = json.optInt("w");
                    icon.h = json.optInt("h");
                    icon.path = StringUtils.getUrl(context,json.optString("path"),icon.w,icon.h);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class CalendarDayRss extends CalendarData{
        public int total;
        public  ArrayList<RssInstance> rssInstances;

        public CalendarDayRss(){
            rssInstances = new ArrayList<>();
        }

        @Override
        public void readFromJsonContent(Context context,String jsonStr){
            try {
                JSONObject json = new JSONObject(jsonStr);
                total = json.optInt("total");
                JSONArray array = json.optJSONArray("rssInstances");
                if(array != null){
                    for(int i = 0;i < array.length();i++){
                        JSONObject jsonObject = array.optJSONObject(i);
                        RssInstance rss = new RssInstance();
                        rss.readFromJsonContent(context,jsonObject.toString());
                        rssInstances.add(rss);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static class RssInstance{
        public String title;
        public String tip;
        public  String type;
        public  String target;
        public  boolean unread;

        public void readFromJsonContent(Context context,String jsonStr){
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(jsonStr);
                title = jsonObject.optString("title");
                tip = jsonObject.optString("tip");
                type = jsonObject.optString("type");
                target = jsonObject.optString("target");
                unread = jsonObject.optBoolean("unread");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public CalendarDoc createCalendarItemFromJson(Context context,String jsonStr){
        CalendarDoc doc = new CalendarDoc();
        doc.readFromJsonContent(context,jsonStr);
        String[] strs = doc.ui.split("#");
        type = strs[0];
        doc.ui = strs[1];
        return doc;
    }

    public static class CalendarDoc extends CalendarData{
        public String refId;
        public  String ui;
        public  String mark;
        public  String id;
        public  String title;
        public  String content;
        public  ArrayList<Image> images;
        public  Image icon;
        public  int likes;
        public  int comments;
        public  String userName;
        public   String updateTime;
        public   String musicUrl;
        public  String musicName;

        public CalendarDoc(){
            icon = new Image();
            images = new ArrayList<>();
        }

        public void readFromJson(Context context,String jsonStr){
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(jsonStr);
                String uiTmp = jsonObject.optString("ui");
                String[] strs = uiTmp.split("#");
                ui = strs[1];
                mark = jsonObject.optString("mark");
                refId = jsonObject.optString("refId");
                id = jsonObject.optString("id");
                title = jsonObject.optString("title");
                content = jsonObject.optString("content");
                likes = jsonObject.optInt("likes");
                comments = jsonObject.optInt("comments");
                userName = jsonObject.optString("username");
                updateTime = jsonObject.optString("updateTime");
                musicUrl = jsonObject.optString("musicUrl");
                musicName = jsonObject.optString("musicName");
                JSONObject json = jsonObject.optJSONObject("icon");
                if(json != null){
                    icon.real_path = Otaku.URL_QINIU + json.optString("path");
                    icon.w = json.optInt("w");
                    icon.h = json.optInt("h");
                    icon.path = StringUtils.getUrl(context,json.optString("path"),icon.w,icon.h);
                }
                JSONArray jsonArray = jsonObject.optJSONArray("images");
                if(jsonArray != null){
                    for(int i = 0;i < jsonArray.length();i++){
                        JSONObject json1 = jsonArray.optJSONObject(i);
                        Image image = new Image();
                        image.real_path = Otaku.URL_QINIU + json1.optString("path");
                        image.w = json1.optInt("w");
                        image.h = json1.optInt("h");
                        image.path = StringUtils.getUrl(context,json1.optString("path"),image.w,image.h);
                        images.add(image);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void readFromJsonContent(Context context,String jsonStr){
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(jsonStr);
                ui = jsonObject.optString("ui");
                mark = jsonObject.optString("mark");
                id = jsonObject.optString("id");
                title = jsonObject.optString("title");
                content = jsonObject.optString("content");
                likes = jsonObject.optInt("likes");
                comments = jsonObject.optInt("comments");
                userName = jsonObject.optString("username");
                updateTime = jsonObject.optString("updateTime");
                musicUrl = jsonObject.optString("musicUrl");
                musicName = jsonObject.optString("musicName");
                JSONObject json = jsonObject.optJSONObject("icon");
                if(json != null){
                    icon.real_path = Otaku.URL_QINIU + json.optString("path");
                    icon.w = json.optInt("w");
                    icon.h = json.optInt("h");
                    icon.path = StringUtils.getUrl(context, json.optString("path"), icon.w, icon.h);
                }
                JSONArray jsonArray = jsonObject.optJSONArray("images");
                if(jsonArray != null){
                    for(int i = 0;i < jsonArray.length();i++){
                        JSONObject json1 = jsonArray.optJSONObject(i);
                        Image image = new Image();
                        image.real_path = Otaku.URL_QINIU + json1.optString("path");
                        image.w = json1.optInt("w");
                        image.h = json1.optInt("h");
                        image.path = StringUtils.getUrl(context, json1.optString("path"), image.w, image.h);
                        images.add(image);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class CalendarDayDocH1 extends CalendarData{
        public ArrayList<CalendarDoc> docList;

        public CalendarDayDocH1(){
            docList = new ArrayList<>();
        }

        @Override
        public void readFromJsonContent(Context context,String jsonStr){
            try {
                JSONObject json = new JSONObject(jsonStr);
                JSONArray array = json.optJSONArray("docList");
                if(array != null){
                    for(int i = 0;i < array.length();i++){
                        JSONObject jsonObject = array.optJSONObject(i);
                        CalendarDoc doc = new CalendarDoc();
                        doc.readFromJsonContent(context,jsonObject.toString());
                        docList.add(doc);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
