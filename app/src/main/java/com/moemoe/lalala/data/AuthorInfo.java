package com.moemoe.lalala.data;

import com.google.gson.annotations.SerializedName;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Haru on 2016/4/27 0027.
 */
@Table(name = "t_author_v1.0")
public class AuthorInfo {
    public static final String SEX_MALE = "M";
    public static final String SEX_FEMALE = "F";
    @Column(name = "birthday")
    @SerializedName("birthday")
    private String birthday;
    @Column(name = "coin")
    @SerializedName("coin")
    private int coin ;
    @Column(name = "headPath")
    @SerializedName("headPath")
    private String headPath;
    @Column(name = "level")
    @SerializedName("level")
    private int level;
    @Column(name = "levelColor")
    @SerializedName("levelColor")
    private String levelColor;
    @Column(name = "levelName")
    @SerializedName("levelName")
    private String levelName;
    @Column(name = "levelScoreEnd")
    @SerializedName("levelScoreEnd")
    private int levelScoreEnd;
    @Column(name = "levelScoreStart")
    @SerializedName("levelScoreStart")
    private int levelScoreStart;
    @Column(name = "registerTime")
    @SerializedName("registerTime")
    private String registerTime;
    @Column(name = "score")
    @SerializedName("score")
    private int score;
    @Column(name = "sex")
    @SerializedName("sex")
    private String sex;
    @Column(name = "userId",isId = true,autoGen = false)
    @SerializedName("userId")
    private String userId;
    @Column(name = "userName")
    @SerializedName("userName")
    private String userName;
    @Column(name = "openId")
    private String openId;
    @Column(name = "platform")
    private String platform;
    @Column(name = "token")
    private String token;
    @Column(name = "password")
    private String password;
    @Column(name = "phone")
    private String phone;
    @Column(name = "devId")
    private String devId;

    public AuthorInfo(){
        birthday="";
        coin = 0;
        headPath = "";
        level = 0;
        levelColor = "";
        levelName = "";
        levelScoreEnd = 0;
        levelScoreStart = 0;
        registerTime = "";
        score = 0;
        sex = "";
        userId = "";
        userName = "";
        openId = "";
        platform = "";
        token = "";
        password = "";
        phone = "";
        devId = "";
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

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLevelColor() {
        return levelColor;
    }

    public void setLevelColor(String levelColor) {
        this.levelColor = levelColor;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public int getLevelScoreEnd() {
        return levelScoreEnd;
    }

    public void setLevelScoreEnd(int levelScoreEnd) {
        this.levelScoreEnd = levelScoreEnd;
    }

    public int getLevelScoreStart() {
        return levelScoreStart;
    }

    public void setLevelScoreStart(int levelScoreStart) {
        this.levelScoreStart = levelScoreStart;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
