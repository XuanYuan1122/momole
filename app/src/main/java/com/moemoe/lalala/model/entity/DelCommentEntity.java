package com.moemoe.lalala.model.entity;

/**
 * Created by yi on 2017/1/6.
 */

public class DelCommentEntity {
    public String commentId;
    public String docId;
    public String reason;
    public String reasonType;

    public DelCommentEntity(String commentId, String docId, String reason, String reasonType) {
        this.commentId = commentId;
        this.docId = docId;
        this.reason = reason;
        this.reasonType = reasonType;
    }
}
