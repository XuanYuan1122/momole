package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi on 2016/11/29.
 */

public class DocTagEntity implements Parcelable{
    @SerializedName("id")
    private  String id;
    @SerializedName("name")
    private  String name;
    @SerializedName("likes")
    private  long likes;
    @SerializedName("liked")
    private  boolean liked;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<DocTagEntity> CREATOR = new Parcelable.Creator<DocTagEntity>() {
        @Override
        public DocTagEntity createFromParcel(Parcel in) {
            DocTagEntity image = new DocTagEntity();
            Bundle bundle;
            bundle = in.readBundle(getClass().getClassLoader());
            image.id = bundle.getString("id");
            image.name = bundle.getString("name");
            image.liked = bundle.getBoolean("liked");
            image.likes = bundle.getLong("likes");
            return image;
        }

        @Override
        public DocTagEntity[] newArray(int size) {
            return new DocTagEntity[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        bundle.putString("name",name);
        bundle.putLong("likes", likes);
        bundle.putBoolean("liked", liked);
        dest.writeBundle(bundle);
    }
}
