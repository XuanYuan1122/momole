package com.moemoe.lalala.view.widget.menu;

import android.content.Context;

import java.util.ArrayList;

/**
 * 
 * @author karant
 * 
 */
public class PopupMenuItems {

	public static final int INVALID_ID = -1;
	private ArrayList<MenuItem> mItems = new ArrayList<>();

	// private Resources mResources;

	public PopupMenuItems(Context context) {
		// mResources = context.getResources();
	}

	public void clear() {
		mItems.clear();
	}

	public MenuItem findItem(int id) {
		final int size = getCount();
		for (int i = 0; i < size; i++) {
			MenuItem item = mItems.get(i);
			if (item.getmItemId() == id) {
				return item;
			}
		}
		return null;
	}

	public int findItemIndex(int id) {
		final int size = getCount();
		for (int i = 0; i < size; i++) {
			MenuItem item = mItems.get(i);
			if (item.getmItemId() == id) {
				return i;
			}
		}
		return -1;
	}

	public void removeItem(int itemId) {
		removeItemAtInt(findItemIndex(itemId));
	}

	private void removeItemAtInt(int index) {
		if ((index < 0) || (index >= mItems.size())) {
			return;
		}
		mItems.remove(index);
	}

	/**
	 * 
	 * @param menuItem
	 *            不能为空，并且ID不能重复
	 */
	public void addMenuItem(MenuItem menuItem) {
		mItems.add(menuItem);
	}

	public void insertMenuItem(MenuItem menuItem, int pos){
		if(pos < mItems.size()){
			mItems.add(pos, menuItem);
		}
	}

	public MenuItem getItem(int index) {
		return mItems.get(index);
	}

	public int getItemId(int index) {
		MenuItem menuItem = mItems.get(index);
		if (menuItem == null) {
			return INVALID_ID;
		} else {
			return menuItem.getmItemId();
		}
	}

	public int getCount() {
		return mItems.size();
	}

	protected void changeItemTextById(int id, String text) {
		int index = findItemIndex(id);
		if (index >= 0) {
			mItems.remove(index);
			mItems.add(index, new MenuItem(id, text));
		}
	}
	
	protected boolean setShowDot(int id, boolean isShowDot){
		int index = findItemIndex(id);
		if (index >= 0) {
			mItems.get(index).setShowDot(isShowDot);
			return true;
		}else{
			return false;
		}
	}
	
	protected boolean setShowNewsNum(int id, int num){
		int index = findItemIndex(id);
		if (index >= 0) {
			mItems.get(index).setNewsNum(num);
			return true;
		}else{
			return false;
		}
	}
}
