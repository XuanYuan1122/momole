package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/10.
 */

public class CommentSendEntity {
    public String comment;
    public String docId;
    public ArrayList<Image> images;
    public String toUserId;

    public CommentSendEntity(String comment, String docId, ArrayList<Image> images, String toUserId) {
        this.comment = comment;
        this.docId = docId;
        this.images = images;
        this.toUserId = toUserId;
    }
}
