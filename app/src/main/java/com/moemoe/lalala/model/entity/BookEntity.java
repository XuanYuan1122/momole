package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yi on 2017/3/27.
 */

public class BookEntity implements Parcelable {

    private String id;
    private String author;
    private String cover;
    private String shortInfo;
    private String title;
    private String path;
    private boolean isFromSD;
    private String lastChapter;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getShortInfo() {
        return shortInfo;
    }

    public void setShortInfo(String shortInfo) {
        this.shortInfo = shortInfo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isFromSD() {
        return isFromSD;
    }

    public void setFromSD(boolean fromSD) {
        isFromSD = fromSD;
    }

    public String getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }

    public static final Creator<BookEntity> CREATOR = new Creator<BookEntity>() {
        @Override
        public BookEntity createFromParcel(Parcel in) {
            BookEntity entity = new BookEntity();
            Bundle bundle = in.readBundle(getClass().getClassLoader());
            entity.id = bundle.getString("id");
            entity.author = bundle.getString("author");
            entity.cover = bundle.getString("cover");
            entity.shortInfo = bundle.getString("shortInfo");
            entity.title = bundle.getString("title");
            entity.path = bundle.getString("path");
            entity.isFromSD = bundle.getBoolean("isFromSD");
            entity.lastChapter = bundle.getString("lastChapter");
            return entity;
        }

        @Override
        public BookEntity[] newArray(int size) {
            return new BookEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        bundle.putString("author",author);
        bundle.putString("cover",cover);
        bundle.putString("shortInfo",shortInfo);
        bundle.putString("title",title);
        bundle.putString("path",path);
        bundle.putBoolean("isFromSD",isFromSD);
        bundle.putString("lastChapter",lastChapter);
        dest.writeBundle(bundle);
    }
}
