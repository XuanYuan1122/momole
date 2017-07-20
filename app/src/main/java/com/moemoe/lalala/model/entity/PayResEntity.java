package com.moemoe.lalala.model.entity;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * Created by yi on 2017/7/18.
 */

public class PayResEntity {
    @SerializedName("charge")
    private JsonObject charge;
    @SerializedName("success")
    private boolean success;

    public JsonObject getCharge() {
        return charge;
    }

    public void setCharge(JsonObject charge) {
        this.charge = charge;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
