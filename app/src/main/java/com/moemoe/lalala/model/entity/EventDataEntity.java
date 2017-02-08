package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 * Created by 云 on 2016/9/30.
 */

public class EventDataEntity {
    public int background;
    public String content;
    public int content_color;
    public ArrayList<String> content_a;
    public int content_a_color;
    public int background_a;
    public ArrayList<String> content_b;
    public int content_b_color;
    public int background_b;


    public EventDataEntity(int background
            , String content
            , int content_color
            , ArrayList<String> content_a
            , ArrayList<String> content_b){
        this.background = background;
        this.content = content;
        this.content_color = content_color;
        this.content_a = content_a;
        this.content_b = content_b;
    }
}
