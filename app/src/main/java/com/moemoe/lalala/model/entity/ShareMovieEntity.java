package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ShareMovieEntity implements Parcelable {
	private String folderId;// 文件夹ID
	private String fileId;// 音乐ID
	private String fileName; // 文件夹名称
	private String fileCover;// 文件夹封面
	private String updateTime; // 更新时间
	private ArrayList<UserFollowTagEntity> fileTags; // 标签
	private UserTopEntity createUser;// 创建人
	private int playNum;
	private int barrageNum;
	private int coin;
	private String timestamp;

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<ShareMovieEntity> CREATOR = new Creator<ShareMovieEntity>() {
		@Override
		public ShareMovieEntity createFromParcel(Parcel parcel) {
			ShareMovieEntity info = new ShareMovieEntity();
			Bundle bundle = parcel.readBundle(getClass().getClassLoader());
			info.folderId = bundle.getString("folderId");
			info.fileId = bundle.getString("fileId");
			info.fileName = bundle.getString("fileName");
			info.fileCover = bundle.getString("fileCover");
			info.updateTime = bundle.getString("updateTime");
			info.fileTags = bundle.getParcelableArrayList("fileTags");
			info.createUser = bundle.getParcelable("createUser");
			info.coin = bundle.getInt("coin");
			info.playNum = bundle.getInt("playNum");
			info.barrageNum = bundle.getInt("barrageNum");
			info.timestamp = bundle.getString("timestamp");
			return info;
		}

		@Override
		public ShareMovieEntity[] newArray(int i) {
			return new ShareMovieEntity[0];
		}
	};

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		Bundle bundle = new Bundle();
		bundle.putString("folderId",folderId);
		bundle.putString("fileId",fileId);
		bundle.putString("fileName",fileName);
		bundle.putString("fileCover",fileCover);
		bundle.putString("updateTime",updateTime);
		bundle.putParcelable("createUser",createUser);
		bundle.putParcelableArrayList("fileTags",fileTags);
		bundle.putInt("coin",coin);
		bundle.putInt("playNum",playNum);
		bundle.putInt("barrageNum",barrageNum);
		bundle.putString("timestamp",timestamp);
		parcel.writeBundle(bundle);
	}

	public String getFolderId() {
		return folderId;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileCover() {
		return fileCover;
	}

	public void setFileCover(String fileCover) {
		this.fileCover = fileCover;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}


	public UserTopEntity getCreateUser() {
		return createUser;
	}

	public void setCreateUser(UserTopEntity createUser) {
		this.createUser = createUser;
	}

	public int getPlayNum() {
		return playNum;
	}

	public void setPlayNum(int playNum) {
		this.playNum = playNum;
	}

	public int getCoin() {
		return coin;
	}

	public void setCoin(int coin) {
		this.coin = coin;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public ArrayList<UserFollowTagEntity> getFileTags() {
		return fileTags;
	}

	public void setFileTags(ArrayList<UserFollowTagEntity> fileTags) {
		this.fileTags = fileTags;
	}

	public int getBarrageNum() {
		return barrageNum;
	}

	public void setBarrageNum(int barrageNum) {
		this.barrageNum = barrageNum;
	}
}
