package com.moemoe.lalala.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 聊天组与用户 多对多中间表
 *
 * Created by yi on 2017/3/10.
 */
@Entity
public class GroupUserEntity {

    @Id(autoincrement = true)
    private Long id;
    private String talkId;
    private String userId;

    @Generated(hash = 875804838)
    public GroupUserEntity(Long id, String talkId, String userId) {
        this.id = id;
        this.talkId = talkId;
        this.userId = userId;
    }

    @Generated(hash = 440491583)
    public GroupUserEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTalkId() {
        return talkId;
    }

    public void setTalkId(String talkId) {
        this.talkId = talkId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
