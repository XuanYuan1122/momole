package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 * Created by yi on 2017/11/16.
 */

public class OrderTmp {
   public String productId;
   public ArrayList<String> users ;

    public OrderTmp(String productId, ArrayList<String> users) {
        this.productId = productId;
        this.users = users;
    }
}
