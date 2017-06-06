package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/12/14.
 */

public class TrashPut {
    public String title;
    public ArrayList<String> tags;
    public String content;
    public String path;
    public Integer h;
    public Integer w;

    public TrashPut(){
        tags = new ArrayList<>();
    }
}
