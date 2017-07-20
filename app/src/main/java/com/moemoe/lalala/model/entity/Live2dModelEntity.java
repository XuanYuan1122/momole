package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 *
 * Created by yi on 2016/9/27.
 */

public class Live2dModelEntity {
    @SerializedName("condition")
    private String condition;
    @SerializedName("have")
    private boolean have;
    @SerializedName("img")
    private String img;
    @SerializedName("info")
    private String info;
    @SerializedName("name")
    private String name;
    @SerializedName("resourcePath")
    private String resourcePath;
    @SerializedName("type")
    private String type;

    private String localPath;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public boolean isHave() {
        return have;
    }

    public void setHave(boolean have) {
        this.have = have;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocalPath(){
        return "live2d/len/model_" + type + ".json";
    }
}
