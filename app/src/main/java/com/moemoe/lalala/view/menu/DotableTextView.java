package com.moemoe.lalala.view.menu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 
 * @author Ben
 * @deprecated 当前版本没有用到该功能
 */
public class DotableTextView extends TextView{

	/**
	 * FIXME 根据dpi 做改变
	 */
	private static final int DOT_RADIUS = 5;
	
	private boolean mShowDot = false;
	private Paint mDotPaint;
	
	public DotableTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public DotableTextView(Context context){
		super(context);
		init();
	}
	
	public DotableTextView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		mDotPaint = new Paint();
		mDotPaint.setColor(Color.RED);
	}

	public void setShowDot(boolean isShowDot){
		mShowDot = isShowDot;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mShowDot) {
			canvas.drawCircle(getWidth() - DOT_RADIUS * 2, getHeight() / 2, DOT_RADIUS, mDotPaint);
		}
	}
	
	
}
