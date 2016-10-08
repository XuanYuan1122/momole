package com.moemoe.lalala.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Haru on 2016/5/12 0012.
 */
public class AppUpdateInfo {

    public String url;
    public int updateStatus;//0.no 1.update 2.force-update
    public String title;
    public String content;

    public void readFromJsonContent(String str){
        try {
            JSONObject json = new JSONObject(str);
            url = json.optString("url");
            updateStatus = json.optInt("updateStatus");
            title = json.optString("title");
            content = json.optString("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
