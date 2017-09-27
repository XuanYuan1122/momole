package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 * 转发动态
 * Created by yi on 2017/9/20.
 */

public class RetweetEntity {
    private String createUserName; //创建人
    private String createUserId;//创建人ID
    private String content;
    private ArrayList<Image> images;

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }
}
