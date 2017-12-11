package com.moemoe.lalala.model.entity;

public class FeedNoticeBagEntity {
	private UserTopEntity user;// 用户信息
	private String showMsg;// 显示的文字
	private boolean delete; // 是否被删除
	private ShareFolderEntity folder; // 文件夹信息
	private int coin; // 花费的节操数
	private String schema;

	public UserTopEntity getUser() {
		return user;
	}

	public void setUser(UserTopEntity user) {
		this.user = user;
	}

	public String getShowMsg() {
		return showMsg;
	}

	public void setShowMsg(String showMsg) {
		this.showMsg = showMsg;
	}

	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public int getCoin() {
		return coin;
	}

	public void setCoin(int coin) {
		this.coin = coin;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public ShareFolderEntity getFolder() {
		return folder;
	}

	public void setFolder(ShareFolderEntity folder) {
		this.folder = folder;
	}
}
