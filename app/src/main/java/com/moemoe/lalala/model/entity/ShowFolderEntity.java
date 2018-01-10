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
    private String folderId;// 文件夹ID

    private String cover; // 封面

    private String folderName; // 文件夹名称

    private String createUser;// 创建人

    private String createUserName; // 创建人名称

    private ArrayList<String> texts;// 标签

    private String userIcon;

    private String type;

    private int coin;

    private int items;

    private String time;

    private boolean select;

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
            info.folderId = bundle.getString("folderId");
            info.cover = bundle.getString("cover");
            info.folderName = bundle.getString("folderName");
            info.createUser = bundle.getString("createUser");
            info.createUserName = bundle.getString("createUserName");
            info.type = bundle.getString("type");
            info.texts = bundle.getStringArrayList("texts");
            info.select = bundle.getBoolean("select");
            info.coin = bundle.getInt("coin");
            info.items = bundle.getInt("items");
            info.time = bundle.getString("time");
            info.userIcon = bundle.getString("userIcon");
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
        bundle.putString("folderId",folderId);
        bundle.putString("cover",cover);
        bundle.putString("folderName",folderName);
        bundle.putString("createUser",createUser);
        bundle.putString("createUserName",createUserName);
        bundle.putString("type",type);
        bundle.putStringArrayList("texts",texts);
        bundle.putBoolean("select",select);
        bundle.putInt("coin",coin);
        bundle.putInt("items",items);
        bundle.putString("time",time);
        bundle.putString("userIcon",userIcon);
        parcel.writeBundle(bundle);
    }
}
