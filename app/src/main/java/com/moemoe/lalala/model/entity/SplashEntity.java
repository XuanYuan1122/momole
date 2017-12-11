package com.moemoe.lalala.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by yi on 2017/11/23.
 */
@Entity
public class SplashEntity {
    /**
     id (string, optional): id ,
     imagePath (string, optional): 显示图片的地址 ,
     showSeconds (integer, optional): 显示秒数 ,
     skip (boolean, optional): 是否可跳过,true可以 false 不可以 ,
     targetUrl (string, optional): 跳转URL
     */
    @Id
    private String id;
    private String imagePath;
    private Integer showSeconds;
    private Boolean skip;
    private String targetUrl;

    @Generated(hash = 160698797)
    public SplashEntity(String id, String imagePath, Integer showSeconds,
            Boolean skip, String targetUrl) {
        this.id = id;
        this.imagePath = imagePath;
        this.showSeconds = showSeconds;
        this.skip = skip;
        this.targetUrl = targetUrl;
    }

    @Generated(hash = 1625228737)
    public SplashEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Integer getShowSeconds() {
        return showSeconds;
    }

    public void setShowSeconds(Integer showSeconds) {
        this.showSeconds = showSeconds;
    }

    public Boolean getSkip() {
        return skip;
    }

    public void setSkip(Boolean skip) {
        this.skip = skip;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
}
