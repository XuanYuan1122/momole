package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yi on 2017/8/22.
 */

public class ManHua2Entity implements Parcelable {
    private String cover;
    private String createUser;
    private String createUserName;
    private String folderId;
    private String folderName;
    private int items;
    private boolean select;

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ManHua2Entity> CREATOR = new Creator<ManHua2Entity>() {
        @Override
        public ManHua2Entity createFromParcel(Parcel parcel) {
            ManHua2Entity info = new ManHua2Entity();
            Bundle bundle = parcel.readBundle(getClass().getClassLoader());
            info.cover = bundle.getString("cover");
            info.createUser = bundle.getString("createUser");
            info.createUserName = bundle.getString("createUserName");
            info.folderId = bundle.getString("folderId");
            info.folderName = bundle.getString("folderName");
            info.items = bundle.getInt("items");
            return info;
        }

        @Override
        public ManHua2Entity[] newArray(int i) {
            return new ManHua2Entity[0];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putString("cover",cover);
        bundle.putString("createUser",createUser);
        bundle.putString("createUserName",createUserName);
        bundle.putString("folderId",folderId);
        bundle.putString("folderName",folderName);
        bundle.putInt("items",items);
        parcel.writeBundle(bundle);
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
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

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getItems() {
        return items;
    }

    public void setItems(int items) {
        this.items = items;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
