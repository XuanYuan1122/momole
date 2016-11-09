package com.moemoe.lalala.data;

import android.content.Context;

import com.app.annotation.Column;
import com.app.annotation.Table;
import com.moemoe.lalala.R;
import com.moemoe.lalala.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/7/12 0012.
 */
@Table(name = "doc_list_data")
public class DocItemBean extends BasicBean{

    @Column(name = "id",isId = true,autoGen = false)
    public String id;
    @Column(name = "json")
    public String json;

    public TagDocUser user;
    public TagDocDesc doc;
    public ArrayList<DocTag> tags;

    public DocItemBean(){
        tags = new ArrayList<>();
    }

    public class TagDocUser{
        public String id;
        public String nickname;
        public Image icon;
        public int level;
        public String levelName;
        public int level_color;

        public void readFromJsonContent(Context context, String jsonContent){
            try {
                JSONObject json = new JSONObject(jsonContent);
                id = json.optString("id");
                nickname = json.optString("nickname");
                levelName = json.optString("levelName");
                level_color = readColorStr(json.optString("level_color"), context.getResources().getColor(R.color.main_title_cyan));
                level = json.optInt("level");
                icon = new Image();
                JSONObject img = json.optJSONObject("icon");
                icon.real_path = StringUtils.getUrl(context, img.optString("path"), icon.w, icon.h);
                icon.w = img.optInt("w");
                icon.h =  img.optInt("h");
                icon.path = StringUtils.getUrl(context, img.optString("path"), icon.w, icon.h);
            } catch (Exception e) {
            }
        }
    }

    public static class TagDocDesc{
        public String id;
        public String schema;
        public String title;
        public String content;
        public ArrayList<Image> images;
        public TagDocDescMusic music;
        public int likes;
        public int comments;
        public String updateTime;

        public TagDocDesc(){
            images = new ArrayList<>();
        }

        public void readFromJsonContent(Context context, String jsonContent){
            try {
                JSONObject json = new JSONObject(jsonContent);
                id = json.optString("id");
                schema = json.optString("schema");
                title = json.optString("title");
                content = json.optString("content");
                likes = json.optInt("likes");
                comments = json.optInt("comments");
                JSONArray array = json.optJSONArray("images");
                for(int i = 0;i < array.length();i++){
                    JSONObject img = array.optJSONObject(i);
                    Image image = new Image();
                    image.real_path = StringUtils.getUrl(context, img.optString("path"), image.w, image.h);
                    image.w = img.optInt("w");
                    image.h =  img.optInt("h");
                    image.path = StringUtils.getUrl(context, img.optString("path"),image.w, image.h);
                    images.add(image);
                }
                updateTime = json.optString("updateTime");
                music = new TagDocDescMusic();
                music.readFromJsonContent(context,json.optString("music"));
            } catch (Exception e) {
            }
        }
    }

    public static class TagDocDescMusic{
        public String name;
        public int timestamp;
        public String url;
        public Image cover;

        public void readFromJsonContent(Context context, String jsonContent){
            try {
                JSONObject json = new JSONObject(jsonContent);
                name = json.optString("name");
                url = json.optString("url");
                timestamp = json.optInt("timestamp");
                cover = new Image();
                JSONObject img = json.optJSONObject("cover");
                cover.real_path = StringUtils.getUrl(context, img.optString("path"), cover.w, cover.h);
                cover.w = img.optInt("w");
                cover.h =  img.optInt("h");
                cover.path = StringUtils.getUrl(context, img.optString("path"), cover.w, cover.h);
            } catch (Exception e) {
            }
        }
    }

    public static ArrayList<DocItemBean> readFromJsonList(Context context, String jsonContent){
        ArrayList<DocItemBean> res = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(jsonContent);
            for(int i = 0; i < array.length(); i++){
                JSONObject one = (JSONObject)array.get(i);
                DocItemBean bean = new DocItemBean();
                bean.readFromJsonContent(context, one.toString());
                res.add(bean);
            }

        } catch (Exception e) {
        }
        return res;
    }

    public void readFromJsonContent(Context context, String jsonContent){
        try {
            JSONObject json = new JSONObject(jsonContent);
            user = new TagDocUser();
            user.readFromJsonContent(context,json.optString("user"));
            doc = new TagDocDesc();
            doc.readFromJsonContent(context,json.optString("doc"));
            JSONArray array = json.optJSONArray("tags");
            for(int i = 0;i < array.length();i++){
                JSONObject j = array.optJSONObject(i);
                DocTag tag = new DocTag();
                tag.readFromJsonContent(context,j);
                tags.add(tag);
            }
        } catch (Exception e) {
        }
    }
}
