package com.moemoe.lalala.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by yi on 2017/3/10.
 */
@Entity
public class ChatUserEntity {

    @Id
    private String userId;
    private String userName;
    private String userIcon;

    @Generated(hash = 736678575)
    public ChatUserEntity(String userId, String userName, String userIcon) {
        this.userId = userId;
        this.userName = userName;
        this.userIcon = userIcon;
    }

    @Generated(hash = 1794951506)
    public ChatUserEntity() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
