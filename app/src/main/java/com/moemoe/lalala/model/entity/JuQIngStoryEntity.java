package com.moemoe.lalala.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by yi on 2017/9/27.
 */
@Entity
public class JuQIngStoryEntity {
    @Id
    private String storyId;
    private String content;//json
    private String extra;
    private int level;//1:主线 2:支线 3:日常 ,

    @Generated(hash = 1119522479)
    public JuQIngStoryEntity(String storyId, String content, String extra,
            int level) {
        this.storyId = storyId;
        this.content = content;
        this.extra = extra;
        this.level = level;
    }

    @Generated(hash = 1727472124)
    public JuQIngStoryEntity() {
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
