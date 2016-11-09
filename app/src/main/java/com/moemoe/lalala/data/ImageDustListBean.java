package com.moemoe.lalala.data;

import com.moemoe.lalala.R;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yi on 2016/9/23.
 */

public class ImageDustListBean {
    public String serverStatus;//OPEN | CLOSE
    public ArrayList<DustInfo> items;

    public ImageDustListBean(){
        items = new ArrayList<>();
    }

    public static class DustInfo{
        public String id;
        public String title;
       // public String content;
        public Image image;
        public int fun;
        public int shit;
        public int background;
        public boolean isOpen;

        public void readFromJsonStr(String str){
            try {
                JSONObject json = new JSONObject(str);
                id = json.optString("id");
                title = json.optString("title");
               // content = json.optString("content");
                image = new Image();
                JSONObject imgJson = json.optJSONObject("image");
                image.h = imgJson.optInt("h");
                image.w = imgJson.optInt("w");
                image.real_path = Otaku.URL_QINIU + imgJson.optString("path");
                image.local_path = imgJson.optString("path");
                image.path = StringUtils.getUrl(null,imgJson.optString("path"),image.w,image.h);
                fun = json.optInt("fun");
                shit = json.optInt("shit");
                isOpen = json.optBoolean("is_open");
                if(fun >= 20 && fun / shit > 2){
                    background = R.drawable.bg_spitball_paper_golden;
                }else if(shit >= 20 && shit > fun){
                    background = R.drawable.bg_spitball_paper_dirty;
                }else {
                    background = R.drawable.bg_spitball_paper_clean;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public static ArrayList<DustInfo> readListFromJson(String str){
        ArrayList<DustInfo> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(str);
            if(array != null){
                for(int i = 0;i < array.length();i++){
                    JSONObject json = array.optJSONObject(i);
                    DustInfo info = new DustInfo();
                    info.readFromJsonStr(json.toString());
                    list.add(info);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ImageDustListBean readFromJsonStr(String str){
        ImageDustListBean bean = new ImageDustListBean();
        try {
            JSONObject json = new JSONObject(str);
            bean.serverStatus = json.optString("serverStatus");
            JSONArray array = json.optJSONArray("items");
            if(array != null){
                for(int i = 0;i < array.length();i++){
                    JSONObject jsonObject = array.optJSONObject(i);
                    DustInfo info = new DustInfo();
                    info.readFromJsonStr(jsonObject.toString());
                    bean.items.add(info);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bean;
    }

    public static String saveToJson(ArrayList<DustInfo> infos){
        try {
            JSONObject res = new JSONObject();
            JSONArray array = new JSONArray();
            for(DustInfo info : infos){
                JSONObject json = new JSONObject();
                json.put("id",info.id);
                json.put("title",info.title);
                JSONObject imgJson = new JSONObject();
                imgJson.put("w",info.image.w);
                imgJson.put("h",info.image.h);
                imgJson.put("path",info.image.local_path);
                json.put("image",imgJson);
                json.put("fun",info.fun);
                json.put("shit",info.shit);
                json.put("is_open",info.isOpen);
                array.put(json);
            }
            res.put("serverStatus","OPEN");
            res.put("items",array);
            return res.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
