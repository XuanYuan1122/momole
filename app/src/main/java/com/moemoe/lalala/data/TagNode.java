package com.moemoe.lalala.data;

import android.content.Context;

import com.moemoe.lalala.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/7/12 0012.
 */
public class TagNode {
    public String id;
    public String name;
    public String type;
    public Image icon;
    public Image bg;
    public String[] texts;
    public String createUser;
    public String createTime;
    public String updateUser;
    public String updateTime;
    public int version;
    public boolean canMark;
    public boolean canDoc;
    public int docNum;
    public int commentNum;

    public void readFromJson(Context context,String str){
        try {
            JSONObject json = new JSONObject(str);
            id = json.optString("id");
            name = json.optString("name");
            type = json.optString("type");
            createUser = json.optString("createUser");
            createTime = json.optString("createTime");
            updateUser = json.optString("updateUser");
            updateTime = json.optString("updateTime");
            version = json.optInt("version");
            docNum = json.optInt("docNum");
            commentNum = json.optInt("commentNum");
            canMark = json.optBoolean("canMark");
            canDoc = json.optBoolean("canDoc");
            JSONArray array = json.optJSONArray("texts");
            ArrayList<String> temp = new ArrayList<>();
            for(int i = 0;i < array.length();i++){
                temp.add(array.optString(i));
            }
            texts = temp.toArray(new String[]{});
            JSONObject bgj = json.optJSONObject("bg");
            bg = new Image();
            bg.real_path = StringUtils.getUrl(context, bgj.optString("path"), bg.w, bg.h);
            bg.w = bgj.optInt("w");
            bg.h = bgj.optInt("h");
            bg.path = StringUtils.getUrl(context, bgj.optString("path"), bg.w, bg.h);
            icon = new Image();
            JSONObject iconj = json.optJSONObject("icon");
            icon.real_path = StringUtils.getUrl(context, iconj.optString("path"), icon.w, bg.h);
            icon.w = iconj.optInt("w");
            icon.h = iconj.optInt("h");
            icon.path = StringUtils.getUrl(context, iconj.optString("path"), icon.w, bg.h);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
