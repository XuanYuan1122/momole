package com.moemoe.lalala.data;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Haru on 2016/7/12 0012.
 */
@Table(name = "t_featured_v1.0")
public class FeaturedBean {
    @Column(name = "uuid" ,isId = true,autoGen = false)
    public String uuid;
    @Column(name = "json")
    public String json;

    @SerializedName("bg")
    private Image bg;
    @SerializedName("name")
    private String name;
    @SerializedName("schema")
    private String schema;

    public Image getBg() {
        return bg;
    }

    public void setBg(Image bg) {
        this.bg = bg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    //    public static ArrayList<FeaturedBean> readFromJsonList(Context context, String jsonContent){
//        ArrayList<FeaturedBean> res = new ArrayList<>();
//        try {
//            JSONArray array = new JSONArray(jsonContent);
//            for(int i = 0; i < array.length(); i++){
//                JSONObject one = (JSONObject)array.get(i);
//                FeaturedBean bean = new FeaturedBean();
//                bean.readFromJsonContent(context, one.toString());
//                res.add(bean);
//            }
//
//        } catch (Exception e) {
//        }
//        return res;
//    }
//
//    public void readFromJsonContent(Context context, String jsonContent){
//        try {
//            JSONObject json = new JSONObject(jsonContent);
//            schema = json.optString("schema");
//            name = json.optString("name");
//            bg = new Image();
//            JSONObject img = json.optJSONObject("bg");
//            bg.real_path = StringUtils.getUrl(context, img.optString("path"), bg.w, bg.h);
//            bg.w = img.optInt("w");
//            bg.h =  img.optInt("h");
//            bg.path = StringUtils.getUrl(context, img.optString("path"), bg.w, bg.h);
//        } catch (Exception e) {
//        }
//    }
}
