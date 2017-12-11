package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2017/12/1.
 */

public class DepartmentGroupEntity {
    /**
     authority (boolean, optional): 是否为官方 ,
     cover (string, optional): 群封面 ,
     createTime (string, optional): 创建时间 ,
     createUser (string, optional): 群创建人 ,
     desc (string, optional): 群简介 ,
     groupName (string, optional): 群组名称 ,
     groupNo (string, optional): 群号 ,
     id (string, optional): 群组ID ,
     join (boolean, optional): 是否已经加入 ,
     userList (Array[用户简介信息-返回], optional): 用户信息(查询群组时为空,群组详情时不为空) ,
     users (integer, optional): 群人数
     */
    private boolean authority;
    private String cover;
    private String createTime;
    private String createUser;
    private String desc;
    private String groupName;
    private String groupNo;
    private String id;
    private boolean join;
    private ArrayList<UserTopEntity> userList;
    private int users;

    public boolean isAuthority() {
        return authority;
    }

    public void setAuthority(boolean authority) {
        this.authority = authority;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isJoin() {
        return join;
    }

    public void setJoin(boolean join) {
        this.join = join;
    }

    public ArrayList<UserTopEntity> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<UserTopEntity> userList) {
        this.userList = userList;
    }

    public int getUsers() {
        return users;
    }

    public void setUsers(int users) {
        this.users = users;
    }
}
