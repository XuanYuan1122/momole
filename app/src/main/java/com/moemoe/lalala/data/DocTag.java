package com.moemoe.lalala.data;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/7/12 0012.
 */
public class DocTag {
    public   String id;
    public   String type;
    public   String name;
    public   long likes;
    public   boolean liked;

    public void readFromJsonContent(Context context,JSONObject json){
        id = json.optString("id");
        type = json.optString("type");
        name = json.optString("name");
        likes = json.optInt("likes");
        liked = json.optBoolean("liked");
    }

    public static ArrayList<DocTag> readFromJsonList(Context context,String s){
        ArrayList<DocTag> tags = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(s);
            if(array != null){
                for (int i = 0;i < array.length();i++){
                    JSONObject json = array.optJSONObject(i);
                    DocTag tag = new DocTag();
                    tag.readFromJsonContent(context,json);
                    tags.add(tag);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tags;
    }
}
