package com.moemoe.lalala.event;

import com.moemoe.lalala.model.entity.AlarmClockEntity;

/**
 * Created by yi on 2017/5/17.
 */

public class AlarmEvent {
    private AlarmClockEntity entity;
    private int type;//1 create 2 delete 3 update 4 cancel

    public AlarmEvent(AlarmClockEntity entity,int type) {
        this.entity = entity;
        this.type = type;
    }

    public AlarmClockEntity getEntity() {
        return entity;
    }

    public void setEntity(AlarmClockEntity entity) {
        this.entity = entity;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
