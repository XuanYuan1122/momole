package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/12.
 */

public class LoginResultEntity {
    @SerializedName("token")
    private String token;
    @SerializedName("userId")
    private String userId;
    @SerializedName("new")
    private boolean isNew;
    @SerializedName("coin")
    private int coin;
    @SerializedName("headPath")
    private String headPath;
    @SerializedName("userName")
    private String userName;
    @SerializedName("level")
    private int level;
    @SerializedName("openBag")
    private boolean openBag;
    @SerializedName("inspector")
    private boolean inspector;
    @SerializedName("rcToken")
    private String rcToken;
    @SerializedName("deskMateList")
    private ArrayList<DeskMateEntity> deskMateList;

    public LoginResultEntity(){
        deskMateList = new ArrayList<>();
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public String getHeadPath() {
        return headPath;
    }

    public void setHeadPath(String headPath) {
        this.headPath = headPath;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isOpenBag() {
        return openBag;
    }

    public void setOpenBag(boolean openBag) {
        this.openBag = openBag;
    }

    public boolean isInspector() {
        return inspector;
    }

    public void setInspector(boolean inspector) {
        this.inspector = inspector;
    }

    public String getRcToken() {
        return rcToken;
    }

    public void setRcToken(String rcToken) {
        this.rcToken = rcToken;
    }

    public ArrayList<DeskMateEntity> getDeskMateList() {
        return deskMateList;
    }

    public void setDeskMateList(ArrayList<DeskMateEntity> deskMateList) {
        this.deskMateList = deskMateList;
    }

}
