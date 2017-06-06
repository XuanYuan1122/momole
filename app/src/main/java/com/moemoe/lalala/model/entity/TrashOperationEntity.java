package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/12/13.
 */

public class TrashOperationEntity {
    public String type;//image text
    public ArrayList<Ids> ids;

    public TrashOperationEntity(String type) {
        this.type = type;
        ids = new ArrayList<>();
    }

    public static class Ids{
        public boolean fun;
        public String id;
        public Ids(String id,boolean fun){
            this.id = id;
            this.fun = fun;
        }
    }
}
