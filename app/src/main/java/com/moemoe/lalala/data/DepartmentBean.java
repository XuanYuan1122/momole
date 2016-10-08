package com.moemoe.lalala.data;

import android.content.Context;

import com.app.annotation.Column;
import com.app.annotation.Table;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/8/11 0011.
 */
@Table(name = "department_doc_list_data")
public class DepartmentBean {
    @Column(name = "id",isId = true,autoGen = false)
    public String id;
    @Column(name = "json")
    public String json;

    public String before;
    public String list;

    public static class DepartmentDoc{
        public String ui;
        public String schema;
        public String title;
        public String content;
        public ArrayList<Image> images;
        public Image icon;
        public String musicUrl;
        public String musicName;
        public int likes;
        public int comments;
        public String userName;
        public String updateTime;
        public String mark;
        public String uiTitle;

        public DepartmentDoc(){
            images = new ArrayList<>();
            icon = new Image();
        }

        public void readFromJsonContent(Context context,String str){
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(str);
                ui = jsonObject.optString("ui");
                mark = jsonObject.optString("mark");
                title = jsonObject.optString("title");
                content = jsonObject.optString("content");
                likes = jsonObject.optInt("likes");
                comments = jsonObject.optInt("comments");
                userName = jsonObject.optString("username");
                updateTime = jsonObject.optString("updateTime");
                musicUrl = jsonObject.optString("musicUrl");
                musicName = jsonObject.optString("musicName");
                schema = jsonObject.optString("schema");
                uiTitle = jsonObject.optString("uiTitle");
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
                        image.path = StringUtils.getUrl(context,json1.optString("path"),image.w,image.h);
                        images.add(image);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void readFromJsonContent(String str){
        try {
            JSONObject json = new JSONObject(str);
            before = json.optString("before");
            list = json.optJSONArray("list").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<DepartmentDoc> readFromJsonList(Context context,String str){
        ArrayList<DepartmentDoc> items = new ArrayList<>(0);
        try {
            JSONArray array = new JSONArray(str);
            if(array != null){
                for(int i = 0;i < array.length();i++){
                    JSONObject json = array.optJSONObject(i);
                    DepartmentDoc item = new DepartmentDoc();
                    item.readFromJsonContent(context,json.toString());
                    items.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  items;
    }
}
