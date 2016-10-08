package com.moemoe.lalala.data;

import android.content.Context;

import org.json.JSONObject;

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
}
