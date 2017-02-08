package com.moemoe.lalala.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by yi on 2016/11/28.
 */

@Entity
public class NetaDb {
    @Id
    private Long id;
    @Unique
    private String uuid;

    private String listId;

    private String dataJson;

    @Generated(hash = 2120760080)
    public NetaDb(Long id, String uuid, String listId, String dataJson) {
        this.id = id;
        this.uuid = uuid;
        this.listId = listId;
        this.dataJson = dataJson;
    }

    @Generated(hash = 1090598508)
    public NetaDb() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getListId() {
        return this.listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getDataJson() {
        return this.dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }
}
