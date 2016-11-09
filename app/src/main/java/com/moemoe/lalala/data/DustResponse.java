package com.moemoe.lalala.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Haru on 2016/9/6.
 */
public class DustResponse {
    public String id;
    public boolean overTimes;
    public int timesLimit;

    public void readFromJsonContent(String str){
        try {
            JSONObject json = new JSONObject(str);
            id = json.optString("id");
            overTimes = json.optBoolean("overTimes");
            timesLimit = json.optInt("timesLimit");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
