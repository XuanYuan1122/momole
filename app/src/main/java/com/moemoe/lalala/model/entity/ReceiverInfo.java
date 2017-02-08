package com.moemoe.lalala.model.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Haru on 2016/6/6 0006.
 */
public class ReceiverInfo {
    public String logoUrl;
    public String title;
    public String content;
    public String schema;
    public boolean showNotify;
    public String type;

    public void readFromJsonContent(String str){
        try {
            JSONObject json = new JSONObject(str);
            logoUrl = json.optString("logoUrl");
            title = json.optString("title");
            content = json.optString("content");
            schema = json.optString("schema");
            type = json.optString("type");
            showNotify = json.optBoolean("showNotify");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
