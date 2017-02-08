package com.moemoe.lalala.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi on 2016/11/11.
 */

public class SignBean {
    @SerializedName("checkState")
    private boolean checkState;
    @SerializedName("day")
    private int day;

    public boolean isCheckState() {
        return checkState;
    }

    public void setCheckState(boolean checkState) {
        this.checkState = checkState;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
