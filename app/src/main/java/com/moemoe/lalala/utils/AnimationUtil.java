package com.moemoe.lalala.utils;


import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
/**
 * 动画工具类
 * 
 * @author Yitianhao
 *
 */
public class AnimationUtil implements AnimationListener {

	private Animation animation;
	private OnAnimationEndListener animationEndListener;
	private OnAnimationStartListener animationStartListener;
	private OnAnimationRepeatListener animationRepeatListener;

	public AnimationUtil(Context context, int resId) {
		this.animation = AnimationUtils.loadAnimation(context, resId);
		this.animation.setAnimationListener(this);
	}
	
	public AnimationUtil(){
	}
	
	/**
	 * 自定义Translate类型的Animation
	 * 
	 * @param fromXDelta
	 * @param toXDelta
	 * @param fromYDelta
	 * @param toYDelta
	 */
	public AnimationUtil tanslateAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta) {
		animation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
		return this;
	}
	
	/**
	 * 自定义Alph类型的Animation
	 * 
	 * @param fromAlpha
	 * @param toAlpha
	 */
	public AnimationUtil alphaAnimation(float fromAlpha,float toAlpha){
		animation = new AlphaAnimation(fromAlpha, toAlpha);
		return this;
	}
	
	/**
	 * 自定义rotate类型的Animation
	 * @param fromDegrees
	 * @param toDegrees
	 * @return
	 * @author Haru
	 */
	public AnimationUtil rotateAnimation(float fromDegrees,float toDegrees){
		animation = new RotateAnimation(fromDegrees, toDegrees);
		return this;
	}
	
	/**
	 * 自定义rotate类型的Animation2
	 * @param fromDegrees
	 * @param toDefrees
	 * @param pivotX
	 * @param pivotY
	 * @return
	 * @author Haru
	 */
	public AnimationUtil rotateAnimation(float fromDegrees,float toDefrees,float pivotX,float pivotY){
		animation = new RotateAnimation(fromDegrees, toDefrees, pivotX, pivotY);
		return this;
	}

	/**
	 * 自定义rotate类型的Animation3
	 * @param fromDegrees
	 * @param toDefrees
	 * @param pivotX
	 * @param pivotY
	 * @return
	 * @author Haru
	 */
	public AnimationUtil rotateAnimation(float fromDegrees,float toDefrees,int pivotXtype,float pivotX,int pivotType,float pivotY){
		animation = new RotateAnimation(fromDegrees,toDefrees,pivotXtype,pivotX,pivotType,pivotY);
		return this;
	}
	
	/**
	 * 自定义scale类型的Animation
	 * @param fromX
	 * @param toX
	 * @param fromY
	 * @param toY
	 * @return
	 * @author Haru
	 */
	public AnimationUtil scaleAnimation(float fromX,float toX,float fromY,float toY,int pivotXtype,float pivotXValue,int pivotType,float pivotYValue){
		animation = new ScaleAnimation(fromX,toX,fromY,toY,pivotXtype,pivotXValue,pivotType,pivotYValue);
		return this;
	}

	/**
	 * 动画之间的间隔
	 * 
	 * @param startOffset
	 * @return
	 */
	public AnimationUtil setStartOffSet(long startOffset) {
		animation.setStartOffset(startOffset);
		return this;
	}

	/**
	 * 设置动画插入器
	 * 
	 * @param i
	 * @return
	 */
	public AnimationUtil setInterpolator(Interpolator i) {
		animation.setInterpolator(i);
		return this;
	}
	
	public AnimationUtil setLinearInterpolator(){
		animation.setInterpolator(new LinearInterpolator());
		return this;
	}

	public void startAnimation(View view) {
		view.startAnimation(animation);
	}

	public static void startAnimation(int resId, View view) {
		view.setBackgroundResource(resId);
		((AnimationDrawable) view.getBackground()).start();
	}

	public AnimationUtil setDuration(long durationMillis) {
		animation.setDuration(durationMillis);
		return this;
	}

	public AnimationUtil setFillAfter(boolean fillAfter) {
		animation.setFillAfter(fillAfter);
		return this;
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (this.animationEndListener != null)
			this.animationEndListener.onAnimationEnd(animation);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		if (this.animationRepeatListener != null)
			this.animationRepeatListener.onAnimationRepeat(animation);
	}

	@Override
	public void onAnimationStart(Animation animation) {
		if (this.animationStartListener != null)
			this.animationStartListener.onAnimationStart(animation);
	}

	public AnimationUtil setOnAnimationEndLinstener(OnAnimationEndListener listener) {
		animation.setAnimationListener(this);
		this.animationEndListener = listener;
		return this;
	}

	public AnimationUtil setOnAnimationStartLinstener(OnAnimationStartListener listener) {
		animation.setAnimationListener(this);
		this.animationStartListener = listener;
		return this;
	}

	public AnimationUtil setOnAnimationRepeatLinstener(OnAnimationRepeatListener listener) {
		animation.setAnimationListener(this);
		this.animationRepeatListener = listener;
		return this;
	}

	public interface OnAnimationEndListener {
		void onAnimationEnd(Animation animation);
	}

	public interface OnAnimationStartListener {
		void onAnimationStart(Animation animation);
	}

	public interface OnAnimationRepeatListener {
		void onAnimationRepeat(Animation animation);
	}
}
