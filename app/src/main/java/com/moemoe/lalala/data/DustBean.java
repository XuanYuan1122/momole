package com.moemoe.lalala.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/8/25.
 */
public class DustBean {
    public String serverStatus;//OPEN | CLOSE
    public DustInfo item;

    public static class DustInfo{
        public String id;
        public String title;
        public String content;
        public int likes;
        public int dislikes;
        public boolean overTimes;
        public int getTimes;
        public int background;
        public boolean isOpen;
    }

    public static DustBean readFromJsonContent(String str){
        DustBean dustBean = new DustBean();
        try {
            JSONObject json = new JSONObject(str);
            dustBean.serverStatus = json.optString("serverStatus");
            JSONObject jsonInfo = json.optJSONObject("item");
            DustInfo info = new DustInfo();
            info.id = jsonInfo.optString("id");
            info.title = jsonInfo.optString("title");
            info.content = jsonInfo.optString("content");
            info.likes = jsonInfo.optInt("likes");
            info.dislikes = jsonInfo.optInt("dislikes");
            info.overTimes = jsonInfo.optBoolean("overTimes");
            info.getTimes = jsonInfo.optInt("getTimes");
            dustBean.item = info;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return dustBean;
    }
}
