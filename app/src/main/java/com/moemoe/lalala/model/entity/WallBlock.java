package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Haru on 2016/7/5 0005.
 */
public class WallBlock {
    private String id;
    @SerializedName("bg")
    private Image bg;
    @SerializedName("name")
    private String name;
    @SerializedName("ltX")
    private int ltX;
    @SerializedName("ltY")
    private int ltY;
    @SerializedName("w")
    private int w;
    @SerializedName("h")
    private int h;
    @SerializedName("schema")
    private String schema;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public int getLtX() {
        return ltX;
    }

    public void setLtX(int ltX) {
        this.ltX = ltX;
    }

    public int getLtY() {
        return ltY;
    }

    public void setLtY(int ltY) {
        this.ltY = ltY;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
