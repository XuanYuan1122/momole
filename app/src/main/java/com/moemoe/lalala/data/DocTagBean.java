package com.moemoe.lalala.data;

import android.text.TextUtils;

import com.moemoe.lalala.network.Otaku;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Ben on 2015/12/16 0016.
 */
public class DocTagBean extends BasicBean{

    public String uuid;

    public String tag_name;

    public long create_time;

    public int plus_num;

    public boolean plus_flag;

    /**
     * belong to which doc
     */
    public String doc_uuid;


    /**
     * 序列号 json 字符串
     * @param jsonContent
     * @return
     */
    public static ArrayList<DocTagBean> readFromJsonString(String jsonContent) {
        ArrayList<DocTagBean> ret = new ArrayList<>();
        if (!TextUtils.isEmpty(jsonContent) && !jsonContent.equals("null")) {
            try {
                JSONArray array = new JSONArray(jsonContent);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    if (obj != null ) {
                        DocTagBean bean = new DocTagBean();
                        bean.uuid = obj.optString("id");
                        bean.tag_name = obj.optString("tag_name");
                        bean.plus_num = obj.optInt("plus_num");
                        bean.plus_flag = Otaku.SERVER_Y.equals(obj.optString("plus_flag"));

                        ret.add(bean);
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return ret;
    }


    public static String toJsonString(ArrayList<DocTagBean> beans) {
        String ret = null;
        // 标签bean
        if (beans != null && beans.size() > 0) {
            JSONArray array = new JSONArray();
            for (DocTagBean bean : beans) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("id", bean.uuid);
                    obj.put("tag_name", bean.tag_name);
                   // obj.put("doc_uuid", bean.doc_uuid);
                    obj.put("plus_num", bean.plus_num);
                    obj.put("plus_flag", bean.plus_flag ? Otaku.SERVER_Y : Otaku.SERVER_N);
                    array.put(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            ret = array.toString();
        }
        return ret;
    }

}
