package com.moemoe.lalala.model.entity;

import com.google.gson.JsonObject;

/**
 *
 * Created by yi on 2017/11/21.
 */

public class DiscoverEntity {

    private String from;
    private Long timestamp;
    private String type;
    private JsonObject obj;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonObject getObj() {
        return obj;
    }

    public void setObj(JsonObject obj) {
        this.obj = obj;
    }
}
