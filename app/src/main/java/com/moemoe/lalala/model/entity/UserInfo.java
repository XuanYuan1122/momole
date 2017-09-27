package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi on 2016/12/15.
 */
public class UserInfo implements Parcelable{
    @SerializedName("background")
    private String background;
    @SerializedName("birthday")
    private String birthday;
    @SerializedName("coin")
    private int coin ;
    @SerializedName("followers")
    private int followers;
    @SerializedName("following")
    private boolean following;
    @SerializedName("headPath")
    private String headPath;
    @SerializedName("registerTime")
    private String registerTime;
    @SerializedName("sex")
    private String sex;
    @SerializedName("showFans")
    private boolean showFans;
    @SerializedName("showFavorite")
    private boolean showFavorite;
    @SerializedName("showFollow")
    private boolean showFollow;
    @SerializedName("userId")
    private String userId;
    @SerializedName("userName")
    private String userName;
    @SerializedName("signature")
    private String signature;
    @SerializedName("size")
    private long size;
    @SerializedName("openBag")
    private boolean openBag;
    @SerializedName("black")
    private boolean black;
    @SerializedName("rcTargetId")
    private String rcTargetId;
    @SerializedName("userNo")
    private int userNo;
    @SerializedName("followCount")
    private int followCount;
   // @SerializedName("inviteUserName")
   // private String inviteUserName;
  //  @SerializedName("vipTime")
   // private String vipTime;


    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
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

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public String getHeadPath() {
        return headPath;
    }

    public void setHeadPath(String headPath) {
        this.headPath = headPath;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public boolean isShowFavorite() {
        return showFavorite;
    }

    public void setShowFavorite(boolean showFavorite) {
        this.showFavorite = showFavorite;
    }

    public boolean isShowFollow() {
        return showFollow;
    }

    public void setShowFollow(boolean showFollow) {
        this.showFollow = showFollow;
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public boolean isShowFans() {
        return showFans;
    }

    public void setShowFans(boolean showFans) {
        this.showFans = showFans;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isOpenBag() {
        return openBag;
    }

    public void setOpenBag(boolean openBag) {
        this.openBag = openBag;
    }

    public boolean isBlack() {
        return black;
    }

    public void setBlack(boolean black) {
        this.black = black;
    }

    public String getRcTargetId() {
        return rcTargetId;
    }

    public void setRcTargetId(String rcTargetId) {
        this.rcTargetId = rcTargetId;
    }

    public int getUserNo() {
        return userNo;
    }

  //  public String getInviteUserName() {
   //     return inviteUserName;
   // }

   // public void setInviteUserName(String inviteUserName) {
   //     this.inviteUserName = inviteUserName;
  //  }

    public void setUserNo(int userNo) {
        this.userNo = userNo;
    }

    public int getFollowCount() {
        return followCount;
    }

    public void setFollowCount(int followCount) {
        this.followCount = followCount;
    }

//    public String getVipTime() {
//        return vipTime;
//    }
//
//    public void setVipTime(String vipTime) {
//        this.vipTime = vipTime;
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel parcel) {
            UserInfo info = new UserInfo();
            Bundle bundle = parcel.readBundle(getClass().getClassLoader());
            info.background = bundle.getString("background");
            info.birthday = bundle.getString("birthday");
            info.coin = bundle.getInt("coin");
            info.followers = bundle.getInt("followers");
            info.userNo = bundle.getInt("userNo");
            info.followCount = bundle.getInt("followCount");
            info.following = bundle.getBoolean("following");
            info.headPath = bundle.getString("headPath");
            info.registerTime = bundle.getString("registerTime");
            info.sex = bundle.getString("sex");
            info.showFans = bundle.getBoolean("showFans");
            info.openBag = bundle.getBoolean("openBag");
            info.showFavorite = bundle.getBoolean("showFavorite");
            info.showFollow = bundle.getBoolean("showFollow");
            info.userId = bundle.getString("userId");
            info.userName = bundle.getString("userName");
            info.signature = bundle.getString("signature");
            info.rcTargetId = bundle.getString("rcTargetId");
        //    info.inviteUserName = bundle.getString("inviteUserName");
         //   info.vipTime = bundle.getString("vipTime");
            info.size = bundle.getLong("size");
            info.black = bundle.getBoolean("black");
            return info;
        }

        @Override
        public UserInfo[] newArray(int i) {
            return new UserInfo[0];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putString("background",background);
        bundle.putString("birthday",birthday);
        bundle.putInt("coin",coin);
        bundle.putInt("followCount",followCount);
        bundle.putInt("followers",followers);
        bundle.putInt("userNo",userNo);
        bundle.putBoolean("following",following);
        bundle.putString("headPath",headPath);
        bundle.putString("registerTime",registerTime);
        bundle.putString("sex",sex);
        bundle.putBoolean("showFans",showFans);
        bundle.putBoolean("showFavorite",showFavorite);
        bundle.putBoolean("showFollow",showFollow);
        bundle.putBoolean("openBag",openBag);
        bundle.putString("userId",userId);
        bundle.putString("userName",userName);
        bundle.putString("signature",signature);
        bundle.putString("rcTargetId",rcTargetId);
    //    bundle.putString("inviteUserName",inviteUserName);
     //   bundle.putString("vipTime",vipTime);
        bundle.putLong("size",size);
        bundle.putBoolean("black",black);
        parcel.writeBundle(bundle);
    }
}
