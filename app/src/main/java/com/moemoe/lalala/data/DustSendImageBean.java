package com.moemoe.lalala.data;

/**
 * Created by yi on 2016/11/9.
 */

public class DustSendImageBean {

    private Image image;
    private String title;

    public DustSendImageBean(String title, Image image){
        this.title = title;
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
