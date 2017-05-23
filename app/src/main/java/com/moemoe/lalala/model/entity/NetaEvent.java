package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi on 2017/4/25.
 */

public class NetaEvent {
    @SerializedName("schedule")
    private String schedule;
    @SerializedName("sign")
    private String sign;

    public NetaEvent(String schedule, String sign) {
        this.schedule = schedule;
        this.sign = sign;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
