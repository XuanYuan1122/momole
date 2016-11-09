package com.moemoe.lalala.data;

/**
 * Created by yi on 2016/11/9.
 */

public class DustImageBean {

    private String image;
    private String title;

    public DustImageBean(String title,String image){
        this.title = title;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
