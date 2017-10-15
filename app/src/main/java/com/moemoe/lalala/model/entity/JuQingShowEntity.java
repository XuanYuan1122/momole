package com.moemoe.lalala.model.entity;


import java.util.LinkedHashMap;


/**
 * Created by yi on 2017/9/27.
 */

public class JuQingShowEntity{
    private int index;
    private String text;
    private String name;
    private LinkedHashMap<String,Integer> choice;
    private String extra; //{"type": "game", "score": "200", "identify": "1"}
    private String path;
    private int otherPath;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedHashMap<String, Integer> getChoice() {
        return choice;
    }

    public void setChoice(LinkedHashMap<String, Integer> choice) {
        this.choice = choice;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getOtherPath() {
        return otherPath;
    }

    public void setOtherPath(int otherPath) {
        this.otherPath = otherPath;
    }
}
