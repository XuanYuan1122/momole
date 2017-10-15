package com.moemoe.lalala.model.entity;

/**
 * Created by yi on 2017/10/10.
 */

public class SendSubmissionEntity {
    public String departmentId;
    public String docId;

    public SendSubmissionEntity(String departmentId, String docId) {
        this.departmentId = departmentId;
        this.docId = docId;
    }
}
