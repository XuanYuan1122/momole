package com.moemoe.lalala.view.menu;

/**
 * 
 * @author karant
 * 
 */
public class MenuItem {

	private int mItemId;
	private String mItemText;
	private boolean mShowDot;
	private int mNewsNum;

	public MenuItem(int mItemId, String mItemText) {
		this.mItemId = mItemId;
		this.mItemText = mItemText;
	}
	
	public MenuItem(int mItemId, String mItemText, int newsNum) {
		this.mItemId = mItemId;
		this.mItemText = mItemText;
		mNewsNum = newsNum;
	}
	

	public int getNewsNum() {
		return mNewsNum;
	}

	public void setNewsNum(int mNewsNum) {
		this.mNewsNum = mNewsNum;
	}

	public int getmItemId() {
		return mItemId;
	}

	public void setmItemId(int mItemId) {
		this.mItemId = mItemId;
	}

	public String getmItemText() {
		return mItemText;
	}

	public void setmItemText(String mItemText) {
		this.mItemText = mItemText;
	}

	public void setShowDot(boolean isShowDot){
		mShowDot = isShowDot;
	}
	
	public boolean isShowDot(){
		return mShowDot;
	}
	
}
