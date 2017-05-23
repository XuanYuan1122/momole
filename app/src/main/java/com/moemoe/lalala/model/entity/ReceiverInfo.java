package com.moemoe.lalala.model.entity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
    public String messageType;
    public JsonObject data;
    public Integer id;

    public void readFromJsonContent(String str){
        JsonObject json =  new JsonParser().parse(str).getAsJsonObject();
        logoUrl = json.get("logoUrl").getAsString();
        title = json.get("title").getAsString();
        content = json.get("content").getAsString();
        schema = json.get("schema").getAsString();
        type = json.get("type").getAsString();
        showNotify = json.get("showNotify").getAsBoolean();
        if(json.has("data")) data = new JsonParser().parse(json.get("data").getAsString()).getAsJsonObject();
        if (json.has("id")) id = json.get("id").getAsInt();
        if(json.has("messageType")) messageType = json.get("messageType").getAsString();
    }
}
