package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/12/6.
 */

public class MapMarkEntity {
    private String id;
    private int x;
    private int y;
    private int w;
    private int h;
    private String schema;
    private String content;
    private ArrayList<String> contents;
    private int bg;

    public MapMarkEntity(String id, int x, int y, String schema, ArrayList<String> contents, int bg) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.schema = schema;
        this.contents = contents;
        this.bg = bg;
    }

    public MapMarkEntity(String id, int x, int y, String schema, String content, int bg) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.schema = schema;
        this.content = content;
        this.bg = bg;
    }

    public MapMarkEntity(String id, int x, int y, String schema, int bg) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.schema = schema;
        this.bg = bg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public ArrayList<String> getContents() {
        return contents;
    }

    public void setContents(ArrayList<String> contents) {
        this.contents = contents;
    }

    public int getBg() {
        return bg;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
}
