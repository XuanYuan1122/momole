package com.moemoe.lalala.event;

/**
 *
 * Created by yi on 2018/1/16.
 */

public class RefreshListEvent {

    private String typeId;

    public RefreshListEvent(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
}
