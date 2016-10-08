package com.moemoe.lalala.data;

import android.graphics.Color;
import android.text.TextUtils;

/**
 * bean 类不做混淆
 * @author Ben
 *
 */
public abstract class BasicBean {

	private static final String TAG = "BasicBean";
	
	public String uuid;

	
	/**
	 * 解析16进制颜色字符串
	 * @param str
	 * @param defaultColor
	 * @return
	 * @author Ben
	 */
	protected int readColorStr(String str, int defaultColor) {
		int color = defaultColor;
		if (!TextUtils.isEmpty(str)) {
			try {
				if (!str.startsWith("#")) {
					str = "#" + str;
				}
				color = Color.parseColor(str);
			} catch (Exception e) {
			}
		}
		return color;
	}
	
	@Override
	public boolean equals(Object o) {
		boolean res = false;
		if(o != null){
			BasicBean other = (BasicBean) o;
			if (!TextUtils.isEmpty(other.uuid) && other.uuid.equals(uuid)) {
				res = true;
			}
		}
		return res;
	}
}
