package com.moemoe.lalala.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/28.
 */
@Entity
public class AuthorInfo {
    @Id
    private long id;
    private String headPath;
    @Unique
    private String userId;
    private String openId;
    private String platform;
    private String token;
    private String password;
    private String phone;
    private int coin;
    private String userName;
    private int level;
    private boolean openBag;
    private String rcToken;
    private String vipTime;
    private int inviteNum;

    @Transient
    private boolean inspector;
    @Transient
    private ArrayList<DeskMateEntity> deskMateEntities;
    @Transient
    private int ticketNum;

    public AuthorInfo(){
        headPath = "";
        userId = "";
        openId = "";
        platform = "";
        token = "";
        password = "";
        phone = "";
        coin = 0;
        userName = "";
        level = 1;
        openBag = false;
        rcToken = "";
        deskMateEntities = new ArrayList<>();
    }


    @Generated(hash = 2101622612)
    public AuthorInfo(long id, String headPath, String userId, String openId,
            String platform, String token, String password, String phone, int coin,
            String userName, int level, boolean openBag, String rcToken,
            String vipTime, int inviteNum) {
        this.id = id;
        this.headPath = headPath;
        this.userId = userId;
        this.openId = openId;
        this.platform = platform;
        this.token = token;
        this.password = password;
        this.phone = phone;
        this.coin = coin;
        this.userName = userName;
        this.level = level;
        this.openBag = openBag;
        this.rcToken = rcToken;
        this.vipTime = vipTime;
        this.inviteNum = inviteNum;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHeadPath() {
        return headPath;
    }

    public void setHeadPath(String headPath) {
        this.headPath = headPath;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
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

    public boolean getOpenBag() {
        return this.openBag;
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

    public ArrayList<DeskMateEntity> getDeskMateEntities() {
        return deskMateEntities;
    }

    public void setDeskMateEntities(ArrayList<DeskMateEntity> deskMateEntities) {
        this.deskMateEntities = deskMateEntities;
    }

    public String getVipTime() {
        return vipTime;
    }

    public void setVipTime(String vipTime) {
        this.vipTime = vipTime;
    }

    public int getInviteNum() {
        return inviteNum;
    }

    public void setInviteNum(int inviteNum) {
        this.inviteNum = inviteNum;
    }

    public int getTicketNum() {
        return ticketNum;
    }

    public void setTicketNum(int ticketNum) {
        this.ticketNum = ticketNum;
    }
}
