package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yi on 2017/5/16.
 */

public class RichEntity implements Parcelable {
    private CharSequence inputStr;
    private Image image;

    public RichEntity(){
        image = new Image();
    }

    public CharSequence getInputStr() {
        return inputStr;
    }

    public void setInputStr(CharSequence inputStr) {
        this.inputStr = inputStr;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RichEntity> CREATOR = new Creator<RichEntity>() {
        @Override
        public RichEntity createFromParcel(Parcel parcel) {
            RichEntity info = new RichEntity();
            Bundle bundle = parcel.readBundle();
            info.inputStr = bundle.getCharSequence("inputStr");
            info.image = bundle.getParcelable("image");
            return info;
        }

        @Override
        public RichEntity[] newArray(int i) {
            return new RichEntity[0];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence("inputStr",inputStr);
        bundle.putParcelable("image",image);
        parcel.writeBundle(bundle);
    }
}
