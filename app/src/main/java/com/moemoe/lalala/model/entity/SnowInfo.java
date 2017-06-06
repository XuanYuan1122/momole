package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by yi on 2016/12/20.
 */

public class SnowInfo {
    @SerializedName("myNumber")
    private int myNumber;
    @SerializedName("myRank")
    private int myRank;
    @SerializedName("rankList")
    private ArrayList<RankInfo> rankList;

    public SnowInfo(){
        rankList = new ArrayList<>();
    }

    public class RankInfo{
        @SerializedName("index")
        private int index;
        @SerializedName("nickName")
        private String nickName;
        @SerializedName("number")
        private int number;
        @SerializedName("userId")
        private String userId;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    public int getMyNumber() {
        return myNumber;
    }

    public void setMyNumber(int myNumber) {
        this.myNumber = myNumber;
    }

    public int getMyRank() {
        return myRank;
    }

    public void setMyRank(int myRank) {
        this.myRank = myRank;
    }

    public ArrayList<RankInfo> getRankList() {
        return rankList;
    }

    public void setRankList(ArrayList<RankInfo> rankList) {
        this.rankList = rankList;
    }
}
