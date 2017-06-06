package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi on 2016/12/20.
 */

public class SnowEntity {
    @SerializedName("rankNum")
    private int rankNum;
    @SerializedName("snowNum")
    private int snowNum;

    public int getRankNum() {
        return rankNum;
    }

    public void setRankNum(int rankNum) {
        this.rankNum = rankNum;
    }

    public int getSnowNum() {
        return snowNum;
    }

    public void setSnowNum(int snowNum) {
        this.snowNum = snowNum;
    }
}
