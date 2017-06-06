package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Haru on 2016/7/12 0012.
 */
public class FeaturedEntity {
    @SerializedName("bg")
    private Image bg;
    @SerializedName("name")
    private String name;
    @SerializedName("schema")
    private String schema;

    public Image getBg() {
        return bg;
    }

    public void setBg(Image bg) {
        this.bg = bg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

}
