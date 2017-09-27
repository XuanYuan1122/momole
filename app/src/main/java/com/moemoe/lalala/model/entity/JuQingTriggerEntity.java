package com.moemoe.lalala.model.entity;

import com.google.gson.JsonArray;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by yi on 2017/9/27.
 */
@Entity
public class JuQingTriggerEntity {
    @Id
    private String id;
    @Transient
    private JsonArray condition;
    private String extra;
    private boolean force;//是否为强制剧情
    private int level;//1:主线 2:支线 3:日常 ,
    private String roleOf;
    private String storyId;
    private String type;//map:地图剧情,mobile:手机剧情
    private String conditionStr;

    @Generated(hash = 1271634722)
    public JuQingTriggerEntity(String id, String extra, boolean force, int level,
            String roleOf, String storyId, String type, String conditionStr) {
        this.id = id;
        this.extra = extra;
        this.force = force;
        this.level = level;
        this.roleOf = roleOf;
        this.storyId = storyId;
        this.type = type;
        this.conditionStr = conditionStr;
    }

    @Generated(hash = 901926953)
    public JuQingTriggerEntity() {
    }

    public JsonArray getCondition() {
        return condition;
    }

    public void setCondition(JsonArray condition) {
        this.condition = condition;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getRoleOf() {
        return roleOf;
    }

    public void setRoleOf(String roleOf) {
        this.roleOf = roleOf;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getForce() {
        return this.force;
    }

    public String getConditionStr() {
        return this.conditionStr;
    }

    public void setConditionStr(String conditionStr) {
        this.conditionStr = conditionStr;
    }
}
