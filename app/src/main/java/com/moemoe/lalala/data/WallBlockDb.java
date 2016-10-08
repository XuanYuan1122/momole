package com.moemoe.lalala.data;

import android.text.TextUtils;

import com.app.annotation.Column;
import com.app.annotation.Table;
import com.moemoe.lalala.network.Otaku;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/7/5 0005.
 */
@Table(name = "wall_block")
public class WallBlockDb {
    @Column(name = "uuid",isId = true,autoGen = false)
    public String uuid;
    @Column(name = "wall_json")
    public String wallJson;
}
