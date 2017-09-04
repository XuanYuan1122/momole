package com.moemoe.lalala.model.entity;

/**
 * Created by yi on 2016/11/10.
 */

public class ReportEntity {
    private String reason;
    private String reasonType;
    private String targetId;
    private String targetType;
    private String userId;

    public ReportEntity(String reason, String reasonType, String targetId, String targetType) {
        this.reason = reason;
        this.reasonType = reasonType;
        this.targetId = targetId;
        this.targetType = targetType;
    }

    public ReportEntity(String reason, String reasonType, String targetId, String targetType, String userId) {
        this.reason = reason;
        this.reasonType = reasonType;
        this.targetId = targetId;
        this.targetType = targetType;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReasonType() {
        return reasonType;
    }

    public void setReasonType(String reasonType) {
        this.reasonType = reasonType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
}
