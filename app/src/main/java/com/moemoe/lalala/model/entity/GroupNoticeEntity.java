package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

/**
 * Created by yi on 2017/10/30.
 */
@Entity
public class GroupNoticeEntity {

    @Id
    @SerializedName("id")
    private String id;
    @SerializedName("cover")
    private String cover;
    @SerializedName("extra")
    private String extra;
    @SerializedName("message")
    private String message;
    @SerializedName("name")
    private String name;
    @SerializedName("createTime")
    private String createTime;
    @SerializedName("state")
    private boolean state;
    private boolean isDeal;

    @Generated(hash = 816112167)
    public GroupNoticeEntity(String id, String cover, String extra, String message,
            String name, String createTime, boolean state, boolean isDeal) {
        this.id = id;
        this.cover = cover;
        this.extra = extra;
        this.message = message;
        this.name = name;
        this.createTime = createTime;
        this.state = state;
        this.isDeal = isDeal;
    }

    @Generated(hash = 558138900)
    public GroupNoticeEntity() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public boolean isDeal() {
        return isDeal;
    }

    public void setDeal(boolean deal) {
        isDeal = deal;
    }

    public boolean getState() {
        return this.state;
    }

    public boolean getIsDeal() {
        return this.isDeal;
    }

    public void setIsDeal(boolean isDeal) {
        this.isDeal = isDeal;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
