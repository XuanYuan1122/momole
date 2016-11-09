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
 * Created by Haru on 2016/5/12 0012.
 */
@Table(name = "reply")
public class ReplyBean {
    @Column(name ="id",isId = true,autoGen = false)
    public String uuid;
    public String id;
    @Column(name = "json")
    public String json;

    public String from;
    public String fromName;
    public Image fromIcon;
    public String to;
    public String content;
    public String date;
    public String schema;


    public static ArrayList<ReplyBean> readFromJsonList(Context context,String jsonstr){
        ArrayList<ReplyBean> beans = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(jsonstr);
            if(array != null){
                for(int i = 0;i < array.length();i++){
                    JSONObject json = array.optJSONObject(i);
                    ReplyBean bean = new ReplyBean();
                    bean.readFromJsonContent(context,json.toString());
                    beans.add(bean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return beans;
    }

    public void readFromJsonContent(Context context,String jsonStr){
        try {
            JSONObject json = new JSONObject(jsonStr);
            id = json.optString("id");
            from = json.optString("from");
            fromName = json.optString("fromName");
            fromIcon = new Image();
            JSONObject jsonObject = json.optJSONObject("fromIcon");
            fromIcon.real_path = Otaku.URL_QINIU + jsonObject.optString("path");
            fromIcon.w = jsonObject.optInt("w");
            fromIcon.h = jsonObject.optInt("h");
            fromIcon.path = StringUtils.getUrl(context,jsonObject.optString("path"),fromIcon.w,fromIcon.h);
            to = json.optString("to");
            content = json.optString("content");
            date = json.optString("date");
            schema = json.optString("schema");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
