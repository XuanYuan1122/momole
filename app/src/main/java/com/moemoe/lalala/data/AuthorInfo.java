package com.moemoe.lalala.data;

/**
 * Created by Haru on 2016/4/27 0027.
 */
public class AuthorInfo {
    private String mUUid = null;
    private String mUid = null;
    private String mUserName = null;
    private String mToken = null;
    private String mPlatform = null;
    private String mHeadPath = null;
    private String mPhone = null;
    private String mPassword = null;
    private String mGender = null;
    private String mDevId = null;
    private String slogan = null;
    private String level_name = null;
    private int score = -1;
    private int level = -1;
    private int level_score_end = -1;
    private int level_score_start = -1;
    private int level_color = -1;
    private long register_time = -1;
    private long birthday = -1;
    private int nice_num = -1;
    private int mUserId = -1;
    private int mCoin = -1;

    public String getLevel_name() {
        return level_name;
    }

    public void setLevel_name(String level_name) {
        this.level_name = level_name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel_score_end() {
        return level_score_end;
    }

    public void setLevel_score_end(int level_score_end) {
        this.level_score_end = level_score_end;
    }

    public int getLevel_score_start() {
        return level_score_start;
    }

    public void setLevel_score_start(int level_score_start) {
        this.level_score_start = level_score_start;
    }

    public int getLevel_color() {
        return level_color;
    }

    public void setLevel_color(int level_color) {
        this.level_color = level_color;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public long getRegister_time() {
        return register_time;
    }

    public void setRegister_time(long register_time) {
        this.register_time = register_time;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public int getNice_num() {
        return nice_num;
    }

    public void setNice_num(int nice_num) {
        this.nice_num = nice_num;
    }

    public String getmDevId() {
        return mDevId;
    }

    public void setmDevId(String mDevId) {
        this.mDevId = mDevId;
    }

    public String getmUUid() {
        return mUUid;
    }

    public void setmUUid(String mUUid) {
        this.mUUid = mUUid;
    }

    public String getmUid() {
        return mUid;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getmToken() {
        return mToken;
    }

    public void setmToken(String mToken) {
        this.mToken = mToken;
    }

    public String getmPlatform() {
        return mPlatform;
    }

    public void setmPlatform(String mPlatform) {
        this.mPlatform = mPlatform;
    }

    public String getmHeadPath() {
        return mHeadPath;
    }

    public void setmHeadPath(String mHeadPath) {
        this.mHeadPath = mHeadPath;
    }

    public String getmPhone() {
        return mPhone;
    }

    public void setmPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public int getmUserId() {
        return mUserId;
    }

    public void setmUserId(int mUserId) {
        this.mUserId = mUserId;
    }

    public String getmGender() {
        return mGender;
    }

    public void setmGender(String mGender) {
        this.mGender = mGender;
    }

    public void setmCoin(int coin){this.mCoin = coin;}

    public int getmCoin(){return mCoin;}
}
