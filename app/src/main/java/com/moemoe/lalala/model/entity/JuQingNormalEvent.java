package com.moemoe.lalala.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by yi on 2017/11/10.
 */
@Entity
public class JuQingNormalEvent {
    @Id
    private String id;

    @Generated(hash = 2115742641)
    public JuQingNormalEvent(String id) {
        this.id = id;
    }

    @Generated(hash = 54209545)
    public JuQingNormalEvent() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
