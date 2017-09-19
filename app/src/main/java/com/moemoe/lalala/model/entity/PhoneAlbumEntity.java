package com.moemoe.lalala.model.entity;

/**
 * Created by yi on 2017/9/12.
 */

public class PhoneAlbumEntity {
    private Image cover;
    private String id;
    private String name;

    public PhoneAlbumEntity(){
        cover = new Image();
    }

    public Image getCover() {
        return cover;
    }

    public void setCover(Image cover) {
        this.cover = cover;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
