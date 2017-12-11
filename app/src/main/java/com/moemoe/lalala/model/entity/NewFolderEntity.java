package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by yi on 2017/8/18.
 */

public class NewFolderEntity implements Parcelable {

    private boolean buy;
    private int buyNum;
    private int coin;
    private String createTime;
    private String createUserId;
    private String createUserName;
    private boolean favorite;
    private String favoriteNum;
    private String folderId;
    private String folderName;
    private String cover;
    private boolean follow;
    private ArrayList<String> texts;
    private ArrayList<ShowFolderEntity> topList;
    private ArrayList<ShowFolderEntity> recommendList;
    private String type;
    private Image userIcon;
    private int maxNum;
    private int nowNum;

    public NewFolderEntity(){
        texts = new ArrayList<>();
        topList = new ArrayList<>();
        recommendList = new ArrayList<>();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NewFolderEntity> CREATOR = new Creator<NewFolderEntity>() {
        @Override
        public NewFolderEntity createFromParcel(Parcel parcel) {
            NewFolderEntity info = new NewFolderEntity();
            Bundle bundle = parcel.readBundle(getClass().getClassLoader());
            info.createTime = bundle.getString("createTime");
            info.createUserId = bundle.getString("createUserId");
            info.createUserName = bundle.getString("createUserName");
            info.favoriteNum = bundle.getString("favoriteNum");
            info.folderId = bundle.getString("folderId");
            info.folderName = bundle.getString("folderName");
            info.type = bundle.getString("type");
            info.cover = bundle.getString("cover");
            info.buy = bundle.getBoolean("buy");
            info.favorite = bundle.getBoolean("favorite");
            info.follow = bundle.getBoolean("follow");
            info.buyNum = bundle.getInt("buyNum");
            info.coin = bundle.getInt("coin");
            info.maxNum = bundle.getInt("maxNum");
            info.nowNum = bundle.getInt("nowNum");
            info.texts = bundle.getStringArrayList("texts");
            info.topList = bundle.getParcelableArrayList("topList");
            info.recommendList = bundle.getParcelableArrayList("recommendList");
            info.userIcon = bundle.getParcelable("userIcon");
            return info;
        }

        @Override
        public NewFolderEntity[] newArray(int i) {
            return new NewFolderEntity[0];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putString("createTime",createTime);
        bundle.putString("createUserId",createUserId);
        bundle.putString("createUserName",createUserName);
        bundle.putString("favoriteNum",favoriteNum);
        bundle.putString("folderId",folderId);
        bundle.putString("folderName",folderName);
        bundle.putString("type",type);
        bundle.putString("cover",cover);
        bundle.putBoolean("buy",buy);
        bundle.putBoolean("favorite",favorite);
        bundle.putBoolean("follow",follow);
        bundle.putInt("buyNum",buyNum);
        bundle.putInt("coin",coin);
        bundle.putInt("nowNum",nowNum);
        bundle.putInt("maxNum",maxNum);
        bundle.putStringArrayList("texts",texts);
        bundle.putParcelableArrayList("topList",topList);
        bundle.putParcelableArrayList("recommendList",recommendList);
        bundle.putParcelable("userIcon",userIcon);
        parcel.writeBundle(bundle);
    }

    public Integer getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(Integer maxNum) {
        this.maxNum = maxNum;
    }

    public Integer getNowNum() {
        return nowNum;
    }

    public void setNowNum(Integer nowNum) {
        this.nowNum = nowNum;
    }

    public boolean isBuy() {
        return buy;
    }

    public void setBuy(boolean buy) {
        this.buy = buy;
    }

    public int getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(int buyNum) {
        this.buyNum = buyNum;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getFavoriteNum() {
        return favoriteNum;
    }

    public void setFavoriteNum(String favoriteNum) {
        this.favoriteNum = favoriteNum;
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

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }

    public ArrayList<String> getTexts() {
        return texts;
    }

    public void setTexts(ArrayList<String> texts) {
        this.texts = texts;
    }

    public ArrayList<ShowFolderEntity> getTopList() {
        return topList;
    }

    public void setTopList(ArrayList<ShowFolderEntity> topList) {
        this.topList = topList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Image getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(Image userIcon) {
        this.userIcon = userIcon;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public ArrayList<ShowFolderEntity> getRecommendList() {
        return recommendList;
    }

    public void setRecommendList(ArrayList<ShowFolderEntity> recommendList) {
        this.recommendList = recommendList;
    }
}
