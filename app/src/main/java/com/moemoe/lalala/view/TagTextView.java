package com.moemoe.lalala.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.moemoe.lalala.R;

public class TagTextView extends TextView{

	public TagTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setTag(String tag){
		
		if(!TextUtils.isEmpty(tag)){
			if(tag.length() == 1){
				
				setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
			}else if(tag.length() == 2){
				tag = tag.substring(0, 1) + "\n" + tag.substring(1, 2);
				setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			}else if(tag.length() == 3){
				tag = tag.substring(0, 1) + "\n" + tag.substring(1, 3);
				setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			}else if(tag.length() == 4){
				tag = tag.substring(0, 2) + "\n" + tag.substring(2, 4);
				setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
			}
			setText(tag);
		}else{
			setText(tag);
		}
		
		this.setRotation(getRotationArgu());
	}
	
	private int getRotationArgu(){
		TypedValue typedValue = new TypedValue();
		this.getResources().getValue(R.dimen.tag_text_view_rotation, typedValue, true);
		return  typedValue.data;
	}
	
	
}
