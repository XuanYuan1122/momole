package com.moemoe.lalala.data;

import android.content.Context;

import com.app.annotation.Column;
import com.app.annotation.Table;
import com.moemoe.lalala.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/7/12 0012.
 */
@Table(name = "top_featured")
public class FeaturedBean {
    @Column(name = "uuid" ,isId = true,autoGen = false)
    public String uuid;
    @Column(name = "json")
    public String json;

    public Image bg;
    public String name;
    public String schema;


    public static ArrayList<FeaturedBean> readFromJsonList(Context context, String jsonContent){
        ArrayList<FeaturedBean> res = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(jsonContent);
            for(int i = 0; i < array.length(); i++){
                JSONObject one = (JSONObject)array.get(i);
                FeaturedBean bean = new FeaturedBean();
                bean.readFromJsonContent(context, one.toString());
                res.add(bean);
            }

        } catch (Exception e) {
        }
        return res;
    }

    public void readFromJsonContent(Context context, String jsonContent){
        try {
            JSONObject json = new JSONObject(jsonContent);
            schema = json.optString("schema");
            name = json.optString("name");
            bg = new Image();
            JSONObject img = json.optJSONObject("bg");
            bg.real_path = StringUtils.getUrl(context, img.optString("path"), bg.w, bg.h);
            bg.w = img.optInt("w");
            bg.h =  img.optInt("h");
            bg.path = StringUtils.getUrl(context, img.optString("path"), bg.w, bg.h);
        } catch (Exception e) {
        }
    }
}
