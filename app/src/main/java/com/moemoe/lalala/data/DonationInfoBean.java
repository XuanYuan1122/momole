package com.moemoe.lalala.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/8.
 */

public class DonationInfoBean{

    @SerializedName("myCoin")
    private int myCoin;
    @SerializedName("mySurplusCoin")
    private int mySurplusCoin;
    @SerializedName("sumCoin")
    private int sumCoin;
    @SerializedName("myRank")
    private int myRank;
    @SerializedName("rankList")
    private ArrayList<RankBean> rankList;

    public int getMyCoin() {
        return myCoin;
    }

    public void setMyCoin(int myCoin) {
        this.myCoin = myCoin;
    }

    public int getMySurplusCoin() {
        return mySurplusCoin;
    }

    public void setMySurplusCoin(int mySurplusCoin) {
        this.mySurplusCoin = mySurplusCoin;
    }

    public int getSumCoin() {
        return sumCoin;
    }

    public void setSumCoin(int sumCoin) {
        this.sumCoin = sumCoin;
    }

    public int getMyRank() {
        return myRank;
    }

    public void setMyRank(int myRank) {
        this.myRank = myRank;
    }

    public ArrayList<RankBean> getRankList() {
        return rankList;
    }

    public void setRankList(ArrayList<RankBean> rankList) {
        this.rankList = rankList;
    }

    public class RankBean {
        @SerializedName("coin")
        private int coin;
        @SerializedName("index")
        private int index;
        @SerializedName("nickName")
        private String nickName;
        @SerializedName("userId")
        private String userId;

        public int getCoin() {
            return coin;
        }

        public void setCoin(int coin) {
            this.coin = coin;
        }

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

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

}
