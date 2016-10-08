package com.moemoe.lalala.data;

import android.content.Context;

import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Haru on 2016/5/9 0009.
 */
public class CalendarEvent {
    public String day;
    public Image img;

    public static HashMap<String,CalendarEvent> readFromJsonArray(Context context,String jsonStr){
        HashMap<String,CalendarEvent> list = new HashMap<>();
        try {
            JSONArray array = new JSONArray(jsonStr);
            if(array != null){
                for(int i = 0;i<array.length();i++){
                    JSONObject json = array.optJSONObject(i);
                    CalendarEvent bean = new CalendarEvent();
                    bean.readFromJson(context,json.toString());
                    list.put(bean.day, bean);
                }
            }
        }catch (Exception e){

        }
        return list;
    }

    public void readFromJson(Context context,String jsonStr){
        try{
            JSONObject json = new JSONObject(jsonStr);
            day = json.optString("day");
            img = new Image();
            img.real_path = Otaku.URL_QINIU +  json.optString("icon");
            img.w = json.optInt("iconW");
            img.h = json.optInt("iconH");
            img.path = StringUtils.getUrl(context,json.optString("icon"),img.w,img.h);
        }catch (Exception e){

        }
    }
}
