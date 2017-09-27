package com.moemoe.lalala.utils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by yi on 2017/9/27.
 */
@Entity
public class JuQingDoneEntity {
    @Id
    private String id;
    private long time;

    @Generated(hash = 961956697)
    public JuQingDoneEntity(String id, long time) {
        this.id = id;
        this.time = time;
    }

    @Generated(hash = 1187117588)
    public JuQingDoneEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
