package com.moemoe.lalala.model.entity;

/**
 * Created by yi on 2016/12/29.
 */

public class CommentListSendEntity {
    public String comment;
    public String toUserId;
    public String userId;

    public CommentListSendEntity(String comment, String toUserId, String userId) {
        this.comment = comment;
        this.toUserId = toUserId;
        this.userId = userId;
    }
}
