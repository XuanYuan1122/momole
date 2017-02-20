package com.moemoe.lalala.model.entity;

/**
 * Created by yi on 2017/2/13.
 */

public class CommentDetailRqEntity {
    public String commentId;
    public String docId;

    public CommentDetailRqEntity(String commentId, String docId) {
        this.commentId = commentId;
        this.docId = docId;
    }
}
