package com.moemoe.lalala.model.entity;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by yi on 2017/9/27.
 */
@Entity
public class JuQingDoneEntity {

    @Id
    private String storyId;
    private long timestamp;
    private String createTime;

    @Generated(hash = 1187117588)
    public JuQingDoneEntity() {
    }

    @Generated(hash = 1355037543)
    public JuQingDoneEntity(String storyId, long timestamp, String createTime) {
        this.storyId = storyId;
        this.timestamp = timestamp;
        this.createTime = createTime;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
