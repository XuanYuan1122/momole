package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.moemoe.lalala.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2017/6/7.
 */

public class RichDocListEntity implements Parcelable{

    private ArrayList<RichEntity> list;
    private ArrayList<RichEntity> hideList;
    private ArrayList<DocTagEntity> tags;
    private String title;
    private String docId;
    private String musicPath;
    private String musicTitle;
    private String folderId;
    private Image cover;
    private int time;
    private boolean hidType;
    private String bgCover;

    public RichDocListEntity(){
        list = new ArrayList<>();
        hideList = new ArrayList<>();
        tags = new ArrayList<>();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RichDocListEntity> CREATOR = new Creator<RichDocListEntity>() {
        @Override
        public RichDocListEntity createFromParcel(Parcel parcel) {
            RichDocListEntity info = new RichDocListEntity();
            Bundle bundle = parcel.readBundle(getClass().getClassLoader());
            info.list = bundle.getParcelableArrayList("list");
            info.hideList = bundle.getParcelableArrayList("hideList");
            info.tags = bundle.getParcelableArrayList("tags");
            info.title = bundle.getString("title");
            info.docId = bundle.getString("docId");
            info.musicPath = bundle.getString("musicPath");
            info.musicTitle = bundle.getString("musicTitle");
            info.folderId = bundle.getString("folderId");
            info.bgCover = bundle.getString("bgCover");
            info.time = bundle.getInt("time");
            info.cover = bundle.getParcelable("cover");
            info.hidType = bundle.getBoolean("hidType");
            return info;
        }

        @Override
        public RichDocListEntity[] newArray(int i) {
            return new RichDocListEntity[0];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("list",list);
        bundle.putParcelableArrayList("hideList",hideList);
        bundle.putParcelableArrayList("tags",tags);
        bundle.putString("title",title);
        bundle.putString("docId",docId);
        bundle.putString("musicPath",musicPath);
        bundle.putString("musicTitle",musicTitle);
        bundle.putString("folderId",folderId);
        bundle.putString("bgCover",bgCover);
        bundle.putInt("time",time);
        bundle.putParcelable("cover",cover);
        bundle.putBoolean("hidType",hidType);
        dest.writeBundle(bundle);
    }

    public static RichDocListEntity toEntity(String str){
        try {
            RichDocListEntity entity = new RichDocListEntity();
            JSONObject o = new JSONObject(str);
            entity.setBgCover(o.getString("bgCover"));
            entity.setHidType(o.getBoolean("hideType"));
            entity.setTitle(o.getString("title"));
            JSONArray listArry = o.getJSONArray("list");
            for(int i = 0;i < listArry.length();i++){
                Object res = listArry.get(i);
                RichEntity entity1 = new RichEntity();
                if(res instanceof String){
                    entity1.setInputStr((CharSequence) res);
                }else {
                    JSONObject imgO = (JSONObject) res;
                    Image image = new Image();
                    image.setPath(imgO.getString("path"));
                    image.setW(imgO.getInt("w"));
                    image.setH(imgO.getInt("h"));
                    image.setSize(imgO.getLong("size"));
                    entity1.setImage(image);
                }
                entity.getList().add(entity1);
            }

            JSONArray hideListArry = o.getJSONArray("hideList");
            for(int i = 0;i < hideListArry.length();i++){
                Object res = hideListArry.get(i);
                RichEntity entity1 = new RichEntity();
                if(res instanceof String){
                    entity1.setInputStr((CharSequence) res);
                }else {
                    JSONObject imgO = (JSONObject) res;
                    Image image = new Image();
                    image.setPath(imgO.getString("path"));
                    image.setW(imgO.getInt("w"));
                    image.setH(imgO.getInt("h"));
                    image.setSize(imgO.getLong("size"));
                    entity1.setImage(image);
                }
                entity.getHideList().add(entity1);
            }
            entity.setMusicPath(o.getString("musicPath"));
            entity.setMusicTitle(o.getString("musicTitle"));
            entity.setTime(o.getInt("time"));

            if(o.has("cover")){
                JSONObject cover = o.getJSONObject("cover");
                Image c = new Image();
                c.setPath(cover.getString("path"));
                c.setH(cover.getInt("h"));
                c.setW(cover.getInt("w"));
                entity.setCover(c);
            }
            entity.setFolderId(o.getString("folderId"));

            JSONArray tagArry = o.getJSONArray("tags");
            for(int i = 0;i < tagArry.length();i++){
                JSONObject tagO = tagArry.getJSONObject(i);
                DocTagEntity tagEntity = new DocTagEntity();
                tagEntity.setLikes(tagO.getLong("likes"));
                tagEntity.setName(tagO.getString("name"));
                entity.getTags().add(tagEntity);
            }
            return entity;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String toJsonString(RichDocListEntity entity){
        try {
            JSONObject o = new JSONObject();
            o.put("bgCover",entity.getBgCover());
            o.put("hideType",entity.isHidType());
            o.put("title",entity.getTitle());
            JSONArray listArry = new JSONArray();
            for(RichEntity richEntity : entity.getList()){
                if(!TextUtils.isEmpty(richEntity.getInputStr())){
                    listArry.put(richEntity.getInputStr().toString());
                }else if(richEntity.getImage() != null && !TextUtils.isEmpty(richEntity.getImage().getPath())){
                    JSONObject image = new JSONObject();
                    image.put("path",richEntity.getImage().getPath());
                    image.put("h",richEntity.getImage().getH());
                    image.put("w",richEntity.getImage().getW());
                    image.put("size",richEntity.getImage().getSize());
                    listArry.put(image);
                }
            }
            o.put("list",listArry);

            JSONArray hideListArry = new JSONArray();
            for(RichEntity richEntity : entity.getHideList()){
                if(!TextUtils.isEmpty(richEntity.getInputStr())){
                    hideListArry.put(richEntity.getInputStr().toString());
                }else if(richEntity.getImage() != null && !TextUtils.isEmpty(richEntity.getImage().getPath())){
                    JSONObject image = new JSONObject();
                    image.put("path",richEntity.getImage().getPath());
                    image.put("h",richEntity.getImage().getH());
                    image.put("w",richEntity.getImage().getW());
                    image.put("size",richEntity.getImage().getSize());
                    hideListArry.put(image);
                }
            }
            o.put("hideList",hideListArry);

            o.put("musicPath",entity.getMusicPath());
            o.put("musicTitle",entity.getMusicTitle());
            o.put("time",entity.getTime());

            if(entity.getCover() != null){
                JSONObject cover = new JSONObject();
                cover.put("path",entity.getCover().getPath());
                cover.put("w",entity.getCover().getW());
                cover.put("h",entity.getCover().getH());
                o.put("cover",cover);
            }

            o.put("folderId",entity.getFolderId());

            JSONArray tagArry = new JSONArray();
            for(DocTagEntity tag : entity.getTags()){
                JSONObject tagO = new JSONObject();
                tagO.put("id",tag.getId());
                tagO.put("likes",tag.getLikes());
                tagO.put("name",tag.getName());
                tagArry.put(tagO);
            }
            o.put("tags",tagArry);
            return o.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<RichEntity> getList() {
        return list;
    }

    public void setList(ArrayList<RichEntity> list) {
        this.list = list;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public ArrayList<RichEntity> getHideList() {
        return hideList;
    }

    public void setHideList(ArrayList<RichEntity> hideList) {
        this.hideList = hideList;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }

    public Image getCover() {
        return cover;
    }

    public void setCover(Image cover) {
        this.cover = cover;
    }

    public String getMusicTitle() {
        return musicTitle;
    }

    public void setMusicTitle(String musicTitle) {
        this.musicTitle = musicTitle;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public ArrayList<DocTagEntity> getTags() {
        return tags;
    }

    public void setTags(ArrayList<DocTagEntity> tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isHidType() {
        return hidType;
    }

    public void setHidType(boolean hidType) {
        this.hidType = hidType;
    }

    public String getBgCover() {
        return bgCover;
    }

    public void setBgCover(String bgCover) {
        this.bgCover = bgCover;
    }
}
