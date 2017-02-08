package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi on 2016/11/30.
 */

public class BannerEntity {
    @SerializedName("bg")
    private Image bg;
    @SerializedName("schema")
    private String schema;

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public Image getBg() {
        return bg;
    }

    public void setBg(Image bg) {
        this.bg = bg;
    }
}
