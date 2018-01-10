package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2017/12/29.
 */

public class GamePriceInfoEntity {
    private float buyRevivalCoinsPrice;
    private float buyVIP;
    private ArrayList<BuyRole> buyRoles;

    public float getBuyRevivalCoinsPrice() {
        return buyRevivalCoinsPrice;
    }

    public void setBuyRevivalCoinsPrice(float buyRevivalCoinsPrice) {
        this.buyRevivalCoinsPrice = buyRevivalCoinsPrice;
    }

    public float getBuyVIP() {
        return buyVIP;
    }

    public void setBuyVIP(float buyVIP) {
        this.buyVIP = buyVIP;
    }

    public ArrayList<BuyRole> getBuyRoles() {
        return buyRoles;
    }

    public void setBuyRoles(ArrayList<BuyRole> buyRoles) {
        this.buyRoles = buyRoles;
    }

    public class BuyRole{
        private float price;
        private int id;

        public float getPrice() {
            return price;
        }

        public void setPrice(float price) {
            this.price = price;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
