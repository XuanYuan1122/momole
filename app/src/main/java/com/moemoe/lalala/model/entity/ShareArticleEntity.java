package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * 分享文章
 * Created by yi on 2017/9/20.
 */

public class ShareArticleEntity implements Parcelable {
    private String docId;// 文章ID
    private UserTopEntity docCreateUser;// 文章创建人
    private String title;// 标题
    private String content;// 内容
    private String cover;// 封面
    private String createTime;// 发布时间
    private ArrayList<UserFollowTagEntity> texts;
    private int readNum;

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<ShareArticleEntity> CREATOR = new Parcelable.Creator<ShareArticleEntity>() {
        @Override
        public ShareArticleEntity createFromParcel(Parcel in) {
            ShareArticleEntity entity = new ShareArticleEntity();
            Bundle bundle;
            bundle = in.readBundle(getClass().getClassLoader());
            entity.docId = bundle.getString("docId");
            entity.docCreateUser = bundle.getParcelable("docCreateUser");
            entity.title = bundle.getString("title");
            entity.content = bundle.getString("content");
            entity.cover = bundle.getString("cover");
            entity.createTime = bundle.getString("createTime");
            entity.texts = bundle.getParcelableArrayList("texts");
            entity.readNum = bundle.getInt("readNum");
            return entity;
        }

        @Override
        public ShareArticleEntity[] newArray(int size) {
            return new ShareArticleEntity[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putString("docId",docId);
        bundle.putParcelable("docCreateUser",docCreateUser);
        bundle.putString("title", title);
        bundle.putString("content", content);
        bundle.putString("cover", cover);
        bundle.putString("createTime", createTime);
        bundle.putParcelableArrayList("texts", texts);
        bundle.putInt("readNum", readNum);
        parcel.writeBundle(bundle);
    }

    public int getReadNum() {
        return readNum;
    }

    public void setReadNum(int readNum) {
        this.readNum = readNum;
    }

    public ArrayList<UserFollowTagEntity> getTexts() {
        return texts;
    }

    public void setTexts(ArrayList<UserFollowTagEntity> texts) {
        this.texts = texts;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public UserTopEntity getDocCreateUser() {
        return docCreateUser;
    }

    public void setDocCreateUser(UserTopEntity docCreateUser) {
        this.docCreateUser = docCreateUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
}
