package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 *
 * Created by yi on 2017/7/14.
 */

public class AddressEntity implements Parcelable{
    @SerializedName("address")
    private String address;
    @SerializedName("phone")
    private String phone;
    @SerializedName("userName")
    private String userName;

    public AddressEntity(){
        address = "";
        phone = "";
        userName = "";
    }

    public AddressEntity(String address, String phone, String userName) {
        this.address = address;
        this.phone = phone;
        this.userName = userName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<AddressEntity> CREATOR = new Parcelable.Creator<AddressEntity>() {
        @Override
        public AddressEntity createFromParcel(Parcel parcel) {
            AddressEntity info = new AddressEntity();
            Bundle bundle = parcel.readBundle(getClass().getClassLoader());
            info.address = bundle.getString("address");
            info.phone = bundle.getString("phone");
            info.userName = bundle.getString("userName");
            return info;
        }

        @Override
        public AddressEntity[] newArray(int i) {
            return new AddressEntity[0];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putString("address",address);
        bundle.putString("phone",phone);
        bundle.putString("userName",userName);
        parcel.writeBundle(bundle);
    }
}
