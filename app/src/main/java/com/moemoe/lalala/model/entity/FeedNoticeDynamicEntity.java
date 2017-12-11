package com.moemoe.lalala.model.entity;

public class FeedNoticeDynamicEntity {
	private UserTopEntity user;// 操作人ID

	private String showMsg; //显示的文字
	private String title;// 动态创建人
	private String icon;// icon
	private String content;// 内容
	private String schema; // schema

	private int coin;
	private boolean delete; // 是否被删除

	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

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

	public int getCoin() {
		return coin;
	}

	public void setCoin(int coin) {
		this.coin = coin;
	}
}
