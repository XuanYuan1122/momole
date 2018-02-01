package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * 分享文件夹
 * Created by yi on 2017/9/20.
 */

public class ShareFolderEntity implements Parcelable {
    private String folderId;// 文件夹ID
    private String folderType;// 文件夹类型
    private String folderName; // 文件夹名称
    private String folderCover;// 文件夹封面
    private String updateTime; // 更新时间
    private ArrayList<String> folderTags; // 标签
    private UserTopEntity createUser;// 创建人
    private int coin;
    private int items;

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<ShareFolderEntity> CREATOR = new Parcelable.Creator<ShareFolderEntity>() {
        @Override
        public ShareFolderEntity createFromParcel(Parcel in) {
            ShareFolderEntity entity = new ShareFolderEntity();
            Bundle bundle;
            bundle = in.readBundle(getClass().getClassLoader());
            entity.folderId = bundle.getString("folderId");
            entity.createUser = bundle.getParcelable("createUser");
            entity.folderType = bundle.getString("folderType");
            entity.folderName = bundle.getString("folderName");
            entity.folderCover = bundle.getString("folderCover");
            entity.updateTime = bundle.getString("updateTime");
            entity.folderTags = bundle.getStringArrayList("folderTags");
            entity.coin = bundle.getInt("coin");
            entity.items = bundle.getInt("items");
            return entity;
        }

        @Override
        public ShareFolderEntity[] newArray(int size) {
            return new ShareFolderEntity[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putString("folderId",folderId);
        bundle.putParcelable("createUser",createUser);
        bundle.putString("folderType", folderType);
        bundle.putString("folderName", folderName);
        bundle.putString("folderCover", folderCover);
        bundle.putString("updateTime", updateTime);
        bundle.putStringArrayList("folderTags", folderTags);
        bundle.putInt("coin", coin);
        bundle.putInt("items", items);
        parcel.writeBundle(bundle);
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

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getFolderType() {
        return folderType;
    }

    public void setFolderType(String folderType) {
        this.folderType = folderType;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderCover() {
        return folderCover;
    }

    public void setFolderCover(String folderCover) {
        this.folderCover = folderCover;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public ArrayList<String> getFolderTags() {
        return folderTags;
    }

    public void setFolderTags(ArrayList<String> folderTags) {
        this.folderTags = folderTags;
    }

    public UserTopEntity getCreateUser() {
        return createUser;
    }

    public void setCreateUser(UserTopEntity createUser) {
        this.createUser = createUser;
    }
}
