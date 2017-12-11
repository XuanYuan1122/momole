package com.moemoe.lalala.model.entity;

/**
 * Created by yi on 2017/12/1.
 */

public class LuntanTabEntity {
    private String id;
    private String name;
    private boolean canDoc;

    public boolean isCanDoc() {
        return canDoc;
    }

    public void setCanDoc(boolean canDoc) {
        this.canDoc = canDoc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
