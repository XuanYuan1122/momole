package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 * Created by yi on 2017/2/10.
 */

public class DelTagEntity {
    public String docId;
    public ArrayList<String> tagIds;

    public DelTagEntity(String docId, ArrayList<String> tagIds) {
        this.docId = docId;
        this.tagIds = tagIds;
    }
}
