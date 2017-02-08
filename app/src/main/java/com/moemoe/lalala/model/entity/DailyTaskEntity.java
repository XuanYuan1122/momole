package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by yi on 2016/12/21.
 */

public class DailyTaskEntity {
    @SerializedName("checkState")
    private boolean checkState;
    @SerializedName("level")
    private int level;
    @SerializedName("nowScore")
    private int nowScore;
    @SerializedName("signCoin")
    private int signCoin;
    @SerializedName("signDay")
    private int signDay;
    @SerializedName("upperLimit")
    private int upperLimit;
    @SerializedName("items")
    private ArrayList<TaskItem> items;
    @SerializedName("signItem")
    private ArrayList<SignItem> signItem;

    public DailyTaskEntity(){
        items = new ArrayList<>();
        signItem = new ArrayList<>();
    }

    public class TaskItem{
        @SerializedName("desc")
        private String desc;
        @SerializedName("nowScore")
        private int nowScore;
        @SerializedName("taskName")
        private String taskName;
        @SerializedName("upperLimit")
        private int upperLimit;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public int getNowScore() {
            return nowScore;
        }

        public void setNowScore(int nowScore) {
            this.nowScore = nowScore;
        }

        public String getTaskName() {
            return taskName;
        }

        public void setTaskName(String taskName) {
            this.taskName = taskName;
        }

        public int getUpperLimit() {
            return upperLimit;
        }

        public void setUpperLimit(int upperLimit) {
            this.upperLimit = upperLimit;
        }
    }

    public class SignItem{
        @SerializedName("coin")
        private int coin;
        @SerializedName("order")
        private int order;
        @SerializedName("score")
        private int score;

        public int getCoin() {
            return coin;
        }

        public void setCoin(int coin) {
            this.coin = coin;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }

    public boolean isCheckState() {
        return checkState;
    }

    public void setCheckState(boolean checkState) {
        this.checkState = checkState;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getNowScore() {
        return nowScore;
    }

    public void setNowScore(int nowScore) {
        this.nowScore = nowScore;
    }

    public int getSignCoin() {
        return signCoin;
    }

    public void setSignCoin(int signCoin) {
        this.signCoin = signCoin;
    }

    public int getSignDay() {
        return signDay;
    }

    public void setSignDay(int signDay) {
        this.signDay = signDay;
    }

    public int getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(int upperLimit) {
        this.upperLimit = upperLimit;
    }

    public ArrayList<TaskItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<TaskItem> items) {
        this.items = items;
    }

    public ArrayList<SignItem> getSignItem() {
        return signItem;
    }

    public void setSignItem(ArrayList<SignItem> signItem) {
        this.signItem = signItem;
    }
}
