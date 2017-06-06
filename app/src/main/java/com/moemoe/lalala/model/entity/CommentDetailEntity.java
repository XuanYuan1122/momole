package com.moemoe.lalala.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi on 2017/2/13.
 */

public class CommentDetailEntity {
    @SerializedName("docComment")
    private NewCommentEntity docComment;
    @SerializedName("docTitle")
    private String docTitle;
    @SerializedName("myCreate")
    private boolean myCreate;

    public NewCommentEntity getDocComment() {
        return docComment;
    }

    public void setDocComment(NewCommentEntity docComment) {
        this.docComment = docComment;
    }

    public String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    public boolean isMyCreate() {
        return myCreate;
    }

    public void setMyCreate(boolean myCreate) {
        this.myCreate = myCreate;
    }
}
