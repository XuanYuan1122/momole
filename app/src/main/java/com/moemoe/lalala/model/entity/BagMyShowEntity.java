package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 * Created by yi on 2017/8/16.
 */

public class BagMyShowEntity {

    private int mark;

    private String title;

    private int num;

    private ArrayList<ShowFolderEntity> items;

    public BagMyShowEntity(){

    }

    public BagMyShowEntity(int mark, String title, int num, ArrayList<ShowFolderEntity> items) {
        this.mark = mark;
        this.title = title;
        this.num = num;
        this.items = items;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public ArrayList<ShowFolderEntity> getItems() {
        return items;
    }

    public void setItems(ArrayList<ShowFolderEntity> items) {
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }
}
