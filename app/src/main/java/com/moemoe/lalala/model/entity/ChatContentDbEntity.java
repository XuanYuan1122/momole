package com.moemoe.lalala.model.entity;

import com.moemoe.lalala.greendao.gen.ChatContentDbEntityDao;
import com.moemoe.lalala.greendao.gen.ChatUserEntityDao;
import com.moemoe.lalala.greendao.gen.DaoSession;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Date;

/**
 * Created by yi on 2017/3/10.
 */
@Entity
public class ChatContentDbEntity {

    private String content;
    private String contentType;
    private Date createTime;
    @Id
    private String id;
    private String talkId;
    private String userId;
    private boolean state;//true 已读 false 未读
    @ToOne(joinProperty = "userId")
    private ChatUserEntity user;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 467748131)
    private transient ChatContentDbEntityDao myDao;
    @Generated(hash = 1867105156)
    private transient String user__resolvedKey;

    public ChatContentDbEntity(ChatContentEntity entity){
        content = entity.getContent();
        contentType = entity.getContentType();
        createTime = entity.getCreateTime();
        id = entity.getId();
        talkId = entity.getTalkId();
        userId = entity.getUserId();
        state = false;
        user = new ChatUserEntity();
        user.setUserIcon(entity.getUserIcon());
        user.setUserName(entity.getUserName());
        user.setUserId(entity.getUserId());
    }

    @Generated(hash = 894740987)
    public ChatContentDbEntity(String content, String contentType, Date createTime, String id,
            String talkId, String userId, boolean state) {
        this.content = content;
        this.contentType = contentType;
        this.createTime = createTime;
        this.id = id;
        this.talkId = talkId;
        this.userId = userId;
        this.state = state;
    }

    @Generated(hash = 298892738)
    public ChatContentDbEntity() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public boolean getState() {
        return this.state;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 767078184)
    public ChatUserEntity getUser() {
        String __key = this.userId;
        if (user__resolvedKey == null || user__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ChatUserEntityDao targetDao = daoSession.getChatUserEntityDao();
            ChatUserEntity userNew = targetDao.load(__key);
            synchronized (this) {
                user = userNew;
                user__resolvedKey = __key;
            }
        }
        return user;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 666624000)
    public void setUser(ChatUserEntity user) {
        synchronized (this) {
            this.user = user;
            userId = user == null ? null : user.getUserId();
            user__resolvedKey = userId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1952271982)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getChatContentDbEntityDao() : null;
    }
}
