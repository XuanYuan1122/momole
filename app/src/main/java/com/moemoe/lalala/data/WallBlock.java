package com.moemoe.lalala.data;

import android.text.TextUtils;

import com.moemoe.lalala.network.Otaku;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/7/5 0005.
 */
public class WallBlock {
    public String id;
    public Image image;
    public String name;
    public int x;
    public int y;
    public int w;
    public int h;
    public String schema;

    public static ArrayList<WallBlock> readFromJsonArray(String str){
        ArrayList<WallBlock> wallBlocks = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(str);
            if(jsonArray != null){
                for (int i = 0;i<jsonArray.length();i++){
                    JSONObject json = jsonArray.optJSONObject(i);
                    WallBlock wallBlock = new WallBlock();
                    wallBlock.readFromJsonContent(json.toString());
                    wallBlocks.add(wallBlock);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            return wallBlocks;
        }
    }

    public void readFromJsonContent(String str){
        try {
            JSONObject json = new JSONObject(str);
            id = json.optString("id");
            name = json.optString("name");
            image = new Image();
            JSONObject img = json.optJSONObject("bg");
            if(!TextUtils.isEmpty(img.optString("path"))){
                image.path = Otaku.URL_QINIU + img.optString("path");
                image.w = img.optInt("w");
                image.h = img.optInt("h");
            }
            x = json.optInt("ltX");
            y = json.optInt("ltY");
            w = json.optInt("w");
            h = json.optInt("h");
            schema = json.optString("schema");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
