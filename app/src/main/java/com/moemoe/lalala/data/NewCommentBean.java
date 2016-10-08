package com.moemoe.lalala.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.TextUtils;

import com.moemoe.lalala.R;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/4/18 0018.
 */
public class NewCommentBean {
    public String id;
    public String content;
    public String fromUserId;
    public String fromUserName;
    public Image fromUserIcon;
    public int fromUserLevel;
    public String fromUserLevelName;
    public int fromUserLevelColor;
    public  String fromUserSex;
    public  String toUserId;
    public  String toUserName;
    public  Image toUserIcon;
    public int toUserLevel;
    public String toUserLevelName;
    public  int toUserLevelColor;
    public  String toUserSex;
    public  String createTime;
    public ArrayList<Image> images;

    public NewCommentBean(){
        fromUserIcon = new Image();
        toUserIcon = new Image();
        images = new ArrayList<>();
    }


    public static ArrayList<NewCommentBean> readFromJsonList(Context context,String json){
        ArrayList<NewCommentBean> res = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for(int i = 0;i < array.length();i++){
                JSONObject object = array.optJSONObject(i);
                NewCommentBean bean = new NewCommentBean();
                bean.readFromJsonContent(context,object.toString());
                res.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return res;
    }

    public void readFromJsonContent(Context context,String jsonStr){
        try {
            JSONObject json = new JSONObject(jsonStr);
            id = json.optString("id");
            content = json.optString("content");
            fromUserId = json.optString("fromUserId");
            fromUserName = json.optString("fromUsername");
            fromUserLevelName = json.optString("fromUserLevelName");
            fromUserLevelColor = readColorStr(json.optString("fromUserLevelColor"), context.getResources().getColor(R.color.main_title_cyan));
            fromUserSex = json.optString("fromUserSex");
            fromUserIcon.real_path = Otaku.URL_QINIU + json.optString("fromUserIcon");
            fromUserIcon.w = json.optInt("fromUserIconW");
            fromUserIcon.h = json.optInt("fromUserIconH");
            fromUserIcon.path = StringUtils.getUrl(context, json.optString("fromUserIcon"), fromUserIcon.w, fromUserIcon.h);
            fromUserLevel = json.optInt("fromUserLevel");
            toUserId = json.optString("toUserId");
            toUserName = json.optString("toUsername");
            toUserLevelName = json.optString("toUserLevelName");
            toUserLevelColor =  readColorStr(json.optString("toUserLevelColor"), context.getResources().getColor(R.color.main_title_cyan));
            toUserSex = json.optString("toUserSex");
            toUserIcon.real_path = Otaku.URL_QINIU + json.optString("toUserIcon");
            toUserIcon.w = json.optInt("toUserIconW");
            toUserIcon.h = json.optInt("toUserIconH");
            toUserIcon.path = StringUtils.getUrl(context, json.optString("toUserIcon"), toUserIcon.w, toUserIcon.h);
            toUserLevel = json.optInt("toUserLevel");
            createTime = json.optString("createTime");
            JSONArray jsonArray = json.optJSONArray("images");
            if(jsonArray != null){
                for(int i = 0;i < jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Image image = new Image();
                    image.real_path =  Otaku.URL_QINIU + jsonObject.optString("path");
                    image.w = jsonObject.optInt("w");
                    image.h = jsonObject.optInt("h");
                    image.path = StringUtils.getUrl(context, jsonObject.optString("path"), image.w, image.h);
                    images.add(image);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected int readColorStr(String str, int defaultColor) {
        int color = defaultColor;
        if (!TextUtils.isEmpty(str)) {
            try {
                if (!str.startsWith("#")) {
                    str = "#" + str;
                }
                color = Color.parseColor(str);
            } catch (Exception e) {
            }
        }
        return color;
    }
}
