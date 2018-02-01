package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class MessageDynamicEntity implements Parcelable {
	private String date;
	private String userId;
	private String userName;
	private String headPath;
	private String showMsg;

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<MessageDynamicEntity> CREATOR = new Creator<MessageDynamicEntity>() {
		@Override
		public MessageDynamicEntity createFromParcel(Parcel parcel) {
			MessageDynamicEntity info = new MessageDynamicEntity();
			Bundle bundle = parcel.readBundle(getClass().getClassLoader());
			info.date = bundle.getString("date");
			info.userId = bundle.getString("userId");
			info.userName = bundle.getString("userName");
			info.headPath = bundle.getString("headPath");
			info.showMsg = bundle.getString("showMsg");
			return info;
		}

		@Override
		public MessageDynamicEntity[] newArray(int i) {
			return new MessageDynamicEntity[0];
		}
	};

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		Bundle bundle = new Bundle();
		bundle.putString("date",date);
		bundle.putString("userId",userId);
		bundle.putString("userName",userName);
		bundle.putString("headPath",headPath);
		bundle.putString("showMsg",showMsg);
		parcel.writeBundle(bundle);
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getHeadPath() {
		return headPath;
	}

	public void setHeadPath(String headPath) {
		this.headPath = headPath;
	}

	public String getShowMsg() {
		return showMsg;
	}

	public void setShowMsg(String showMsg) {
		this.showMsg = showMsg;
	}
}
