package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2017/8/18.
 */

public class ShowFolderEntity implements Parcelable {

    private String uuid;

    private String folderId;// 文件夹ID

    private String cover; // 封面

    private String folderName; // 文件夹名称

    private String createUser;// 创建人

    private String createUserName; // 创建人名称

    private ArrayList<String> texts;// 标签

    private ArrayList<UserFollowTagEntity> textsV2;

    private String userIcon;

    private String type;//TJ MH XS ZH SP YY MOVIE MUSIC

    private int coin;

    private int items;

    private String time;

    private boolean select;

    private int playNum;

    private int barrageNum;

    private String timestamp;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<UserFollowTagEntity> getTextsV2() {
        return textsV2;
    }

    public void setTextsV2(ArrayList<UserFollowTagEntity> textsV2) {
        this.textsV2 = textsV2;
    }

    public int getPlayNum() {
        return playNum;
    }

    public void setPlayNum(int playNum) {
        this.playNum = playNum;
    }

    public int getBarrageNum() {
        return barrageNum;
    }

    public void setBarrageNum(int barrageNum) {
        this.barrageNum = barrageNum;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getItems() {
        return items;
    }

    public void setItems(int items) {
        this.items = items;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public ArrayList<String> getTexts() {
        return texts;
    }

    public void setTexts(ArrayList<String> texts) {
        this.texts = texts;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShowFolderEntity> CREATOR = new Creator<ShowFolderEntity>() {
        @Override
        public ShowFolderEntity createFromParcel(Parcel parcel) {
            ShowFolderEntity info = new ShowFolderEntity();
            Bundle bundle = parcel.readBundle(getClass().getClassLoader());
            info.uuid = bundle.getString("uuid");
            info.folderId = bundle.getString("folderId");
            info.cover = bundle.getString("cover");
            info.folderName = bundle.getString("folderName");
            info.createUser = bundle.getString("createUser");
            info.createUserName = bundle.getString("createUserName");
            info.type = bundle.getString("type");
            info.texts = bundle.getStringArrayList("texts");
            info.textsV2 = bundle.getParcelableArrayList("textsV2");
            info.select = bundle.getBoolean("select");
            info.coin = bundle.getInt("coin");
            info.items = bundle.getInt("items");
            info.playNum = bundle.getInt("playNum");
            info.barrageNum = bundle.getInt("barrageNum");
            info.time = bundle.getString("time");
            info.userIcon = bundle.getString("userIcon");
            info.timestamp = bundle.getString("timestamp");
            return info;
        }

        @Override
        public ShowFolderEntity[] newArray(int i) {
            return new ShowFolderEntity[0];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putString("uuid",uuid);
        bundle.putString("folderId",folderId);
        bundle.putString("cover",cover);
        bundle.putString("folderName",folderName);
        bundle.putString("createUser",createUser);
        bundle.putString("createUserName",createUserName);
        bundle.putString("type",type);
        bundle.putStringArrayList("texts",texts);
        bundle.putParcelableArrayList("textsV2",textsV2);
        bundle.putBoolean("select",select);
        bundle.putInt("coin",coin);
        bundle.putInt("items",items);
        bundle.putInt("playNum",playNum);
        bundle.putInt("barrageNum",barrageNum);
        bundle.putString("time",time);
        bundle.putString("userIcon",userIcon);
        bundle.putString("timestamp",timestamp);
        parcel.writeBundle(bundle);
    }
}
