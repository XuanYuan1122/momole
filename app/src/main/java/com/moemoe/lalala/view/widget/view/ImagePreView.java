package com.moemoe.lalala.view.widget.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.moemoe.lalala.R;
import com.moemoe.lalala.view.widget.scaleimage.ScaleView;

/**
 * 图片可缩放展示VIEW
 * @author Yitianhao
 *
 */
public class ImagePreView extends LinearLayout {

	private ScaleView mIvCurrent = null;
	
	public ImagePreView(Context context){
		super(context);
		LayoutInflater.from(context).inflate(R.layout.frag_image, this, true);
		mIvCurrent = (ScaleView)findViewById(R.id.scale_pic_item);
	}
	
//	public void loadImage(String paht){
//		BitmapLoaderUtil.loadImgPickerThumb(mIvCurrent, paht, 0, 0, 0);
//	}
	
	public ScaleView getImageView(){
		return mIvCurrent;
	}
}
