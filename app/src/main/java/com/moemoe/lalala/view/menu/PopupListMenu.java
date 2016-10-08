package com.moemoe.lalala.view.menu;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.moemoe.lalala.R;

/**
 * 
 * @author karant
 * 
 */
public class PopupListMenu {

	// Constants
	private static final String TAG = "PopupListMenu";
	public static final int LOCATION_LEFT_TOP = 1;
	public static final int LOCATION_RIGHT_TOP = 2;
	public static final int LOCATION_RIGHT_BOTTOM = 3;
	public static final int LOCATION_LEFT_BOTTOM = 4;
	public static final int LOCATION_BOTTOM_CENTER = 5;
	public static final int LOCATION_TOP_CENTER = 6;

	// Views
	private PopupWindow mPopup;
	private Drawable mPopupBg;
	private ListView mListView;
	private View mFootView;

	// Variables
	private PopupMenuItems mMenuItems;
	private MenuItemAdapter mAdapter;
	private boolean mIsMenuKey = true;
	private Context mContext;
	private int mColor = Color.BLACK;
	private float listHeight, listWidth;
	/**
	 * 用于计算字符宽高
	 */
	private TextPaint mPaint;
	/**
	 * 3.0 以下风格界面中需要在显示位置中添加偏移；3.0及以上版本，该值为0
	 */
	private float offset;
	private float mMinPopWidth;
	private int mLocation;

	public PopupListMenu(Context context) {
		this(context, new PopupMenuItems(context), null);
	}

	public PopupListMenu(Context context, PopupMenuItems menuItems) {
		this(context, menuItems, null);
	}
//	
//	public PopupListMenu(Context context, PopupMenuItems menuItems, boolean mHoloLightStyle, boolean black) {
//		this(context, menuItems, mHoloLightStyle, black, null);
//	}
	
	public PopupListMenu(Context context, PopupMenuItems menuItems, View footView) {
		mContext = context;
		mFootView = footView;
		this.mMenuItems = menuItems;
		init();
	}

	private void setPaint() {
		View mItem = LayoutInflater.from(mContext).inflate(R.layout.popup_list_menu_item, null, false);
		TextView tv = (TextView) mItem.findViewById(R.id.title);
		mPaint = tv.getPaint();
	}

	private void init() {
		mLocation = LOCATION_LEFT_BOTTOM;
		offset = mContext.getResources().getDimensionPixelSize(R.dimen.popup_menu_offset);
//		offset = 0;	// 3.0后不需要偏移
		
		mMinPopWidth = mContext.getResources().getDimensionPixelSize(R.dimen.popup_menu_min_width);

		Drawable divider;
//		if(mBlackStyle){
//			// 3.0 字白色，底黑色
//			mPopupBg = new ColorDrawable(mContext.getResources().getColor(R.color.bg_menu_black_style));
//			mColor = Color.WHITE;
//			divider = new ColorDrawable(mContext.getResources().getColor(R.color.divider_menu_blak_style));
//		}else if(mHoloLightStyle){
//			mPopupBg = mContext.getResources().getDrawable(R.drawable.menu_dropdown_panel_holo_light);
//			divider = mContext.getResources().getDrawable(R.drawable.list_divider_holo_light);
//			mColor = Color.BLACK;
//		}else{
			mPopupBg = mContext.getResources().getDrawable(R.color.popup_menu_background);
			divider = mContext.getResources().getDrawable(R.color.popup_divider_bg);
			mColor = mContext.getResources().getColor(R.color.txt_menu_item);
//		}
		setPaint();
		
		View contentView = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.popup_list_menu_listview, null, false);

		mPopup = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		mPopup.setBackgroundDrawable(mPopupBg);
		mPopup.setOutsideTouchable(true);
		mPopup.setFocusable(true);
		mPopup.getContentView().setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent motionevent) {
				if (mPopup != null && mPopup.isShowing()) {
					mPopup.dismiss();
					mPopup.setFocusable(false);
				}
				return true;
			}
		});

		mListView = (ListView) contentView.findViewById(R.id.popup_menu_listview);
		mListView.setDivider(divider);
		mListView.setDividerHeight(1);	// 1px
		
		if(mFootView != null){
			mListView.addFooterView(mFootView);
		}
		mAdapter = new MenuItemAdapter();
		mListView.setAdapter(mAdapter);
		mListView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_BACK:
					if (mPopup != null && mPopup.isShowing()) {
						mPopup.dismiss();
						mPopup.setFocusable(false);
					}
					break;

				case KeyEvent.KEYCODE_MENU:
					if (mIsMenuKey) {
						if (mPopup != null && mPopup.isShowing()) {
							mPopup.dismiss();
							mPopup.setFocusable(false);
						}
					} else {
						mIsMenuKey = true;
					}

					break;
				}
				return true;
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterview, View view, int i, long l) {
				if (mPopup != null && mPopup.isShowing()) {
					mPopup.dismiss();
					mPopup.setFocusable(false);
				}
				if (mClickListener != null)
					mClickListener.OnMenuItemClick((int) l);
			}
		});

		// measulWidthHeight();
	}

	private void measulWidthHeight(int location, View mAnchor) {
		int itemHeight = mContext.getResources().getDimensionPixelSize(R.dimen.popup_menu_item_height)
				+ mListView.getDividerHeight();
		listHeight = itemHeight * mAdapter.getCount() + offset;
		if (location == LOCATION_LEFT_TOP || location == LOCATION_RIGHT_TOP) {
			int[] xy = new int[2];
			// mAnchor.getLocationInWindow(xy);
			// Log.d(TAG, "getLocationInWindow x = " + xy[0] + "|y = " + xy[1]);
			mAnchor.getLocationOnScreen(xy);
			// Log.d(TAG, "getLocationOnScreen x = " + xy[0] + "|y = " + xy[1]);
			float maxHeight = xy[1];
			if (listHeight > maxHeight) {
				listHeight = maxHeight - itemHeight;
				mPopup.setHeight((int) listHeight);
			}
		}
		listWidth = mPaint.measureText(getLongestText())
				+ mContext.getResources().getDimensionPixelSize(R.dimen.popup_menu_item_text_padding);
		if (listWidth < mMinPopWidth) {
			listWidth = mMinPopWidth;
		}
		mPopup.setWidth((int) listWidth);
	}

	private String getLongestText() {
		String back = "";
		for (int i = 0; i < mAdapter.getCount(); i++) {
			MenuItem item = (MenuItem) mAdapter.getItem(i);
			if (item.getmItemText().length() > back.length()) {
				back = item.getmItemText();
			}
		}
		return back;
	}

	public void setPoppuMenuItems(PopupMenuItems menuItems) {
		this.mMenuItems = menuItems;
		mAdapter.notifyDataSetChanged();
		// measulWidthHeight();
	}

	public void setLocation(int location) {
		mLocation = location;
	}

	/** 调用setLocation来设置要显示的位置 */
	public void showMenu(View mAnchor) {
		showMenu(mAnchor, mLocation);
	}

	public void showMenuByMenuKey(View mAnchor) {
		showMenuByMenuKey(mAnchor, mLocation);
	}

	public void showMenu(View mAnchor, int location) {
		mAdapter.notifyDataSetChanged();
		mLocation = location;
		if (mPopup != null && mPopup.isShowing()) {
			mPopup.dismiss();
			mPopup.setFocusable(false);
		} else {
			if (mAdapter.getCount() > 0) {
				Log.e(TAG, "mAnchor.getTop() =" + mAnchor.getTop());
				measulWidthHeight(mLocation, mAnchor);
				float offsetX = getOffsetX(mLocation, mAnchor.getWidth());
				float offsetY = getOffsetY(mLocation, mAnchor.getHeight());
				mPopup.showAsDropDown(mAnchor, (int) offsetX, (int) offsetY);
				mPopup.setFocusable(true);
				mPopup.update();
				Log.e(TAG, "mAnchor.offsetY =" + offsetY + ", " + mAnchor.getHeight());
			}
		}
	}

	public void showMenuByMenuKey(View mAnchor, int location) {
		mLocation = location;
		showMenu(mAnchor, mLocation);
		setIsMenuKey(false);
	}
	
//	public void setBlackStyle(){
//		// 3.0 字白色，底黑色
//		mListView.setBackgroundColor(mContext.getResources().getColor(R.color.page_list_item_note_background));
//		mColor = Color.WHITE;
//		mListView.setDivider(new ColorDrawable(mContext.getResources().getColor(R.color.page_list_divider)));
//		mListView.setDividerHeight(1);
//	}

	private void setIsMenuKey(boolean mIsMenuKey) {
		this.mIsMenuKey = mIsMenuKey;
	}

	private float getOffsetX(int location, float mAnchorW) {
		switch (location) {
		case LOCATION_LEFT_TOP:
			return -listWidth + mAnchorW;
		case LOCATION_RIGHT_BOTTOM:
			return 0;
		case LOCATION_RIGHT_TOP:
			return 0;
		case LOCATION_BOTTOM_CENTER:
			return -listWidth / 2.0f + mAnchorW / 2.0f;
		case LOCATION_TOP_CENTER:
			return -listWidth / 2.0f + mAnchorW / 2.0f;
		default:// LOCATION_LEFT_BOTTOM
			return -listWidth + mAnchorW;
		}
	}

	private float getOffsetY(int location, float mAnchor) {
		switch (location) {
		case LOCATION_LEFT_TOP:
			return -listHeight - mAnchor;
		case LOCATION_RIGHT_BOTTOM:
			return -offset;
		case LOCATION_RIGHT_TOP:
			return -listHeight - mAnchor;
		case LOCATION_BOTTOM_CENTER:
			return -offset;
		case LOCATION_TOP_CENTER:
			return -listHeight - mAnchor;
		default:// LOCATION_LEFT_BOTTOM
			return -offset;
		}
	}

	class MenuItemAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mMenuItems.getCount();
		}

		@Override
		public Object getItem(int i) {
			return mMenuItems.getItem(i);
		}

		@Override
		public long getItemId(int i) {
			return mMenuItems.getItemId(i);
		}

		@Override
		public View getView(int i, View view, ViewGroup viewgroup) {
			if (view == null) {
				view = LayoutInflater.from(mContext).inflate(R.layout.popup_list_menu_item, null, false);
				view.setBackgroundResource(R.drawable.popmenu_selector_bg);
			}
			DotableTextView tv = (DotableTextView) view.findViewById(R.id.title);
			TextView news = (TextView)view.findViewById(R.id.tv_news_num);
			MenuItem item = (MenuItem) getItem(i);
			tv.setText(item.getmItemText());
			tv.setTextColor(mColor);
			tv.setShowDot(item.isShowDot());
			if(item.getNewsNum() > 0){
				news.setVisibility(View.VISIBLE);
				news.setText(item.getNewsNum() + "");
			}else{
				news.setVisibility(View.GONE);
			}
			return view;
		}

	}

	public interface MenuItemClickListener {
		public void OnMenuItemClick(int itemId);
	}

	private MenuItemClickListener mClickListener;

	public void setMenuItemClickListener(MenuItemClickListener l) {
		mClickListener = l;
	}

	public void changeItemTextById(int id, String text) {
		mMenuItems.changeItemTextById(id, text);
		mAdapter.notifyDataSetChanged();
	}
	
	public boolean setMenuItemShowDot(int menuId, boolean isShow){
		boolean res = mMenuItems.setShowDot(menuId, isShow);
		mAdapter.notifyDataSetChanged();
		return res;
	}
	
	public boolean setMenuItemShowNum(int menuId, int num){
		boolean res = mMenuItems.setShowNewsNum(menuId, num);
		mAdapter.notifyDataSetChanged();
		return res;
	}

	public PopupMenuItems getCurrentMenuItems() {
		return mMenuItems;
	}
	/**
	 * 获取当前popupmenu显示的状态
	 * @return
	 */
	public boolean isShowing(){
		boolean state = false;
		if(mPopup != null){
			state = mPopup.isShowing();
		}
		return state;
	}
}
