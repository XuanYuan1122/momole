package com.moemoe.lalala.utils;

import android.content.Context;

/**
 * 资源获取工具类
 * 
 * @author Haru
 * @version 创建时间：2015年10月13日 下午12:42:42
 */
public class ResourceUtils {

	/**
	 * 根据图片名获取图片资源ID
	 * 
	 * @param ctx
	 * @param imageName
	 * @return
	 * @author Haru
	 */
	public static int getResource(Context ctx, String imageName) {
		int resId = ctx.getResources().getIdentifier(imageName, "drawable", ctx.getPackageName());
		return resId;
	}

	public static int getColorResource(Context ctx, String colorName){
		int resId = ctx.getResources().getIdentifier(colorName, "color", ctx.getPackageName());
		return resId;
	}
}
