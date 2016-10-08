package com.moemoe.lalala.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * NoScrollGridView
 * 
 * <p> This view extends {@link GridView} and which override onMeasure to set its height as large
 * 	as possible. So this view can be put into a {@link android.widget.ScrollView}
 * @author Ben
 *
 */
public class NoScrollGridView extends GridView {
	public NoScrollGridView(Context context) {
		super(context);
	}

	public NoScrollGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		/*
		 * MeasureSpec 前两位表示模式，AT_MOST模式为10，后面几位代表当前view需要的空间；
		 * 设为Integer.MAX_VALUE >> 2，即前两位为空，后面取最大值
		 */
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}