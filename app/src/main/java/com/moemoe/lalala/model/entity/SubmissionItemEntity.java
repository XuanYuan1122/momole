package com.moemoe.lalala.model.entity;

/**
 *
 * Created by yi on 2017/10/10.
 */

public class SubmissionItemEntity {
    private String id;//"投稿ID"
    private String docId;//"帖子ID"
    private String title;//帖子标题
    private String icon;//icon
    private int status;//状态:1:投稿成功  0:投稿中 2:被拒绝
    private String statusShow;//显示的文字
    private String createTime;//投稿时间
    private String departmentName;//投稿学部名称

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusShow() {
        return statusShow;
    }

    public void setStatusShow(String statusShow) {
        this.statusShow = statusShow;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
