package com.moemoe.lalala.app;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.moemoe.lalala.R;

import java.util.List;


public class AlertDialog extends Dialog {
	Context context;

	ListView mListView;
	ImageView mIconView ;
	TextView mTitleView;
	TextView mMessageView;
	FrameLayout mCustomView;
	Button button_negative; // Negative
	Button button_neutral;  // Neutral
	Button button_positive; // Positive
	View mBottomPanel;
	View mTitlePanel;
	View mDivider;
	View mMessagePanel;
	View mBtnDivider1,mBtnDivider2;
	View mContentPanel;//中间层，在标题与底层按之间的中间层
	public AlertDialog(Context context, boolean cancelable,
					   OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init();
	}

	public AlertDialog(Context context, int theme) {
		super(context, theme);
		init();
	}

	public AlertDialog(Context context) {
		super(context);
		init();
	}
	void init() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = View.inflate(getContext(), R.layout.alert_dialog, null);
		init(view);
	}
    public void init(View view){
		mIconView = (ImageView)view.findViewById(R.id.icon_view);
		mTitleView = (TextView)view.findViewById(R.id.label_title);
		mMessageView= (TextView)view.findViewById(R.id.message_panel);
		mCustomView = (FrameLayout)view.findViewById(R.id.custom_panel);
		//order: button1(Negative),button2(Netural), button3(Positive)
		button_negative = (Button)view.findViewById(android.R.id.button1);
		button_neutral = (Button)view.findViewById(android.R.id.button2);
		button_positive = (Button)view.findViewById(android.R.id.button3);
		mBtnDivider1 = view.findViewById(R.id.divider_1);
		mBtnDivider2 = view.findViewById(R.id.divider_2);
		mBottomPanel = view.findViewById(R.id.bottom_panel);
		mTitlePanel = view.findViewById(R.id.title_panel);
		mDivider = view.findViewById(R.id.divider);
		mMessagePanel = view.findViewById(R.id.scrollView);
		mContentPanel = view.findViewById(R.id.content_panel);
		button_negative.setVisibility(View.GONE);
		button_neutral.setVisibility(View.GONE);
		button_positive.setVisibility(View.GONE);
//		mMessageView.setVisibility(View.GONE);
		mCustomView.setVisibility(View.GONE);
		mBottomPanel.setVisibility(View.GONE);
		mTitlePanel.setVisibility(View.GONE);
		mDivider.setVisibility(View.GONE);
		mMessagePanel.setVisibility(View.GONE);
		setContentView(view);
    }
	
	public ListView getListView() {
		if (mListView == null)
			mListView = (ListView) findViewById(R.id.list_panel);
		return mListView;
	}
	
	
	public void setIcon(Drawable icon) {
		mIconView.setImageDrawable(icon);
		makesureShowTitle();
		
	}
	public void setIcon(int iconId) {
		mIconView.setImageResource(iconId);
		makesureShowTitle();
	}
	
	
	public void setMessage(CharSequence message) {
		mMessageView.setText(message);
		mMessagePanel.setVisibility(View.VISIBLE);
//		mMessageView.setVisibility(View.VISIBLE);
	}

	public void setButton(final int which, CharSequence text, final OnClickListener listener) {
		Button button;
		switch(which){
		case BUTTON_NEGATIVE:
			button = button_negative;
			break;
		case BUTTON_POSITIVE:
			button = button_positive;
			break;
		case BUTTON_NEUTRAL:
			button = button_neutral;
			break;
		default:
			button = button_negative;
		}
		mBottomPanel.setVisibility(View.VISIBLE);
		button.setVisibility(View.VISIBLE);
		if(button_negative.getVisibility()==View.VISIBLE&&button_neutral.getVisibility()==View.VISIBLE)
			mBtnDivider1.setVisibility(View.VISIBLE);
		else
			mBtnDivider1.setVisibility(View.GONE);
		if(button_positive.getVisibility()==View.VISIBLE&&(button_negative.getVisibility()==View.VISIBLE||button_neutral.getVisibility()==View.VISIBLE))
			mBtnDivider2.setVisibility(View.VISIBLE);
		else
			mBtnDivider2.setVisibility(View.GONE);
		
		button.setText(text);

		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null)
					listener.onClick(AlertDialog.this, which);
				boolean dismiss = true;
				switch(which){
				case BUTTON_NEGATIVE:
					dismiss = btn_negative_dismiss_after_click;
					break;
				case BUTTON_POSITIVE:
					dismiss = btn_positive_dismiss_after_click;
					break;
				case BUTTON_NEUTRAL:
					dismiss = btn_neutral_dismiss_after_click;
					break;
				}
				if(dismiss)
					dismiss();
			}
		});
	}
	public void setTitle(CharSequence title) {
		mTitleView.setText(title);
		makesureShowTitle();
	}
	public void	 setTitle(int titleId){
		mTitleView.setText(titleId);
		makesureShowTitle();
	}
    /**
     * 隐藏中间层，只显示标题与按钮
     */
	public void hideContentPanel(){
		mBottomPanel.setPadding(0, 0, 0, 0);
		mContentPanel.setVisibility(View.GONE);
	}
	public void setView(View view){
		mCustomView.setVisibility(View.VISIBLE);
		mCustomView.removeAllViews();
		mCustomView.addView(view);
	}


	void makesureShowTitle() {
		mTitlePanel.setVisibility(View.VISIBLE);
		mDivider.setVisibility(View.VISIBLE);
	}
	
	
	public static class Builder {
		Context context;
		AlertDialog dialog;

		public Builder(Context context) {
			super();
			this.context = context;
			dialog = new AlertDialog(context);
		}
        
		public Builder(Context context, AlertDialog dialog) {
			super();
			this.context = context;
			this.dialog = dialog;
		}
		public AlertDialog create() {
			return dialog;
		}

		public Builder setAdapter(ListAdapter adapter,
				final OnClickListener listener) {
			ListView list = dialog.getListView();
			list.setAdapter(adapter);

			list.setOnItemClickListener(new ListView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> listView, View view,
						int position, long id) {
					if (listener != null)
						listener.onClick(dialog, position);
					dialog.dismiss();
				}
			});
			return this;
		}
		public Builder	 setCursor(Cursor cursor, OnClickListener listener, String labelColumn){
			ListAdapter adapter = new SimpleCursorAdapter(context, R.layout.simple_list_item_1,cursor, 
					new String[]{labelColumn}, new int []{android.R.id.text1},0);
			setAdapter(adapter, listener);
			return this;
		}
		
		public Builder	 setCustomTitle(View customTitleView){
			throw new UnsupportedOperationException("setCustomTitle not support now!" );
		}
		
		public Builder	 setIcon(Drawable icon){
			dialog.setIcon(icon);
			return this;
		}
		public Builder	 setIcon(int iconId){
			dialog.setIcon(iconId);
			return this;
		}
		
		public Builder setIconAttribute(int attrId) {
			throw new UnsupportedOperationException("Not support now!");
		}

		public Builder setInverseBackgroundForced(boolean useInverseBackground) {
			throw new UnsupportedOperationException("Not support now!");
		}
		public Builder	 setItems(int itemsId, OnClickListener listener){
			String items[] = context.getResources().getStringArray(itemsId);
			ListAdapter adapter = new ArrayAdapter<String>(context, R.layout.simple_list_item_1,android.R.id.text1,items);
			setAdapter(adapter, listener);
			return this;
		}
		public Builder	 setItems(CharSequence[] items, OnClickListener listener){
			ListAdapter adapter = new ArrayAdapter<CharSequence>(context, R.layout.simple_list_item_1,android.R.id.text1,items);
			setAdapter(adapter, listener);
			return this;
		}
		
		public Builder setCancelable(boolean cancelable) {
			dialog.setCancelable(cancelable);
			return this;
		}

		public Builder setTitle(CharSequence title) {
			dialog.setTitle(title);
			return this;
		}
		public Builder	 setTitle(int titleId){
			dialog.setTitle(titleId);
			return this;
		}
		
		public Builder	 setView(View view){
			dialog.setView(view);
			return this;
		}
		/**
	     * 隐藏中间层，只显示标题与按钮
	     */
        public Builder hideContentPanel(){
        	dialog.hideContentPanel();
			return this;
        }
		public Builder setMessage(CharSequence message ) {
			dialog.setMessage(message)	;
			return this;
		}
		public Builder	 setMessage(int messageId) {
			dialog.setMessage(context.getResources().getText(messageId))	;
			return this;
		}
		

		public Builder setMultiChoiceItems(CharSequence[] items,
				boolean[] checkedItems,
				OnMultiChoiceClickListener listener) {
			ListAdapter adapter = new ArrayAdapter<CharSequence>(context, R.layout.simple_list_item_multiple_choice,android.R.id.text1,items);
			setMultiChoiceItems(adapter, checkedItems, listener);
			return this;
		}

		public Builder setMultiChoiceItems(Cursor cursor,
				String isCheckedColumn, String labelColumn,
				OnMultiChoiceClickListener listener) {
			// FIXME 
			return this;
		}

		public Builder setMultiChoiceItems(int itemsId, boolean[] checkedItems,
				OnMultiChoiceClickListener listener) {
			String items[] = context.getResources().getStringArray(itemsId);
			ListAdapter adapter = new ArrayAdapter<CharSequence>(context, R.layout.simple_list_item_multiple_choice,android.R.id.text1,items);
			setMultiChoiceItems(adapter, checkedItems, listener);
			return this;
		}
		void setMultiChoiceItems(ListAdapter adapter, boolean[] checkedItems, final OnMultiChoiceClickListener listener){
			ListView list = dialog.getListView();
			list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			list.setAdapter(adapter);
			if(checkedItems!=null)
				for(int i =0;i<checkedItems.length;i++)
					list.setItemChecked(i, checkedItems[i]);
			if (listener != null)
				list.setOnItemClickListener(new ListView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> listView, View view,
							int position, long id) {
						listener.onClick(dialog, position,((ListView)listView).isItemChecked(position));
					}
				});
		}

		public Builder	 setNegativeButton(CharSequence text, OnClickListener listener){
			dialog.setButton(BUTTON_NEGATIVE, text, listener);
			return this;
		}
		public Builder	 setNegativeButton(int textId, OnClickListener listener){
			dialog.setButton(BUTTON_NEGATIVE, context.getResources().getString(textId), listener);
			return this;
		}

		public Builder setPositiveButton(String text, OnClickListener listener){
			dialog.setButton(BUTTON_POSITIVE, text, listener);
			return this;
		}

		public Builder	 setPositiveButton(int textId, OnClickListener listener){
			dialog.setButton(BUTTON_POSITIVE, context.getResources().getString(textId), listener);
			return this;
		}
		
		public Builder setNeutralButton(String text, OnClickListener listener){
			dialog.setButton(BUTTON_NEUTRAL, text, listener);
			return this;
		}

		public Builder	 setNeutralButton(int textId, OnClickListener listener){
			dialog.setButton(BUTTON_NEUTRAL, context.getResources().getString(textId), listener);
			return this;
		}

		public Builder	 setButtonDismiss(int btn, boolean dismiss_after_click_btn){
			dialog.setButtonDismiss(btn, dismiss_after_click_btn);
			return this;
		}
		
		public Builder	 setOnCancelListener(OnCancelListener listener){
			dialog.setOnCancelListener(listener);
			return this;
		}
		public Builder	 setOnDismissListener(OnDismissListener listener){
			dialog.setOnDismissListener(listener);
			return this;
		}
		
		public Builder	 setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener){
			dialog.getListView().setOnItemSelectedListener(listener);
			return this;
		}
		public Builder	 setOnKeyListener(OnKeyListener onKeyListener){
			dialog.setOnKeyListener(onKeyListener);
			return this;
		}
		
		public Builder setSingleChoiceItems(CharSequence[] items,
				int checkedItem, OnClickListener listener) {

			ListAdapter adapter = new ArrayAdapter<CharSequence>(context,
					R.layout.simple_list_item_single_choice,
					android.R.id.text1, items);
			setSingleChoiceItems(adapter, checkedItem, listener);
			return this;
		}
		public Builder setSingleChoiceItems(List<CharSequence> items,
				int checkedItem, OnClickListener listener) {

			ListAdapter adapter = new ArrayAdapter<CharSequence>(context,
					R.layout.simple_list_item_single_choice,
					android.R.id.text1, items);
			setSingleChoiceItems(adapter, checkedItem, listener);
			return this;
		}
		public Builder	 setSingleChoiceItems(ListAdapter adapter, int checkedItem, final OnClickListener listener){
			ListView list = dialog.getListView();
			list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			list.setAdapter(adapter);
			if(checkedItem>-1){
				list.setItemChecked(checkedItem, true);
				list.setSelection(checkedItem);
			}
			// FIXME
			if (listener != null)
				list.setOnItemClickListener(new ListView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> listView, View view,
							int position, long id) {
						listener.onClick(dialog, position);
					}
				});
			return this;
		}
		public Builder	 setSingleChoiceItems(int itemsId, int checkedItem, OnClickListener listener){
			String items[] = context.getResources().getStringArray(itemsId);
			ListAdapter adapter = new ArrayAdapter<CharSequence>(context, R.layout.simple_list_item_single_choice,android.R.id.text1,items);
			setSingleChoiceItems(adapter, checkedItem, listener);
			return this;
		}
		public Builder	 setSingleChoiceItems(Cursor cursor, int checkedItem, String labelColumn, OnClickListener listener){
			ListAdapter adapter = new SimpleCursorAdapter(context, R.layout.simple_list_item_single_choice,cursor, 
					new String[]{labelColumn}, new int []{android.R.id.text1},0);
			setSingleChoiceItems(adapter, checkedItem,listener);
			return this;
		} 
		public void show(){
			dialog.show();
		}
	}

	boolean btn_positive_dismiss_after_click = true;
	boolean btn_negative_dismiss_after_click = true;
	boolean btn_neutral_dismiss_after_click = true;

	public void setButtonDismiss(int btn, boolean dismiss_after_click_btn) {
		switch(btn){
		case BUTTON_POSITIVE:
			btn_positive_dismiss_after_click = dismiss_after_click_btn;
			break;
		case BUTTON_NEGATIVE:
			btn_negative_dismiss_after_click = dismiss_after_click_btn;
			break;
		case BUTTON_NEUTRAL:
			btn_neutral_dismiss_after_click = dismiss_after_click_btn;
			break;
			
		}
	}
}
