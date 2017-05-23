package com.moemoe.lalala.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;

/**
 * 私信列表
 * Created by yi on 2017/3/10.
 */
@Entity
public class PrivateMessageItemEntity {

    @Id
    private String talkId;
    private String icon;
    private String name;
    private String content;
    private Date updateTime;
    private Integer dot;
    private boolean isNew;
    private boolean state;//0.正常 1.屏蔽

    @Generated(hash = 2101591343)
    public PrivateMessageItemEntity(String talkId, String icon, String name,
            String content, Date updateTime, Integer dot, boolean isNew,
            boolean state) {
        this.talkId = talkId;
        this.icon = icon;
        this.name = name;
        this.content = content;
        this.updateTime = updateTime;
        this.dot = dot;
        this.isNew = isNew;
        this.state = state;
    }

    @Generated(hash = 728768596)
    public PrivateMessageItemEntity() {
    }

    public String getTalkId() {
        return talkId;
    }

    public void setTalkId(String talkId) {
        this.talkId = talkId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDot() {
        return dot;
    }

    public void setDot(Integer dot) {
        this.dot = dot;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean getIsNew() {
        return this.isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean isState() {
        return this.state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public boolean getState() {
        return this.state;
    }
}
