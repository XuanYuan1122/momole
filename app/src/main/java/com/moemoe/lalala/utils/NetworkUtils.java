package com.moemoe.lalala.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.moemoe.lalala.R;

public class NetworkUtils {

	private static final String TAG = "NetworkUtils";
	
	
	public static boolean isNetworkAvailable(Context context){
		boolean isAvailable = false;
		if (context == null){
			return isAvailable;
		}
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null){
			return isAvailable;
		}
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null){
			return isAvailable;
		}
		isAvailable = ni.isAvailable();
		//LogUtils.LOGI(TAG, "NetState = " + isAvailable);
		return isAvailable;
	}

	public static boolean checkNetworkAndShowError(final Context context){
		if(isNetworkAvailable(context)){
			return true;
		}else{
			((Activity)context).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ToastUtil.showCenterToast(context,R.string.msg_server_connection);
				}
			});
			return false;
		}
	}
	
	/**
	 * 用戶是否授權可以使用网络
	 * @return
	 */
	//public static boolean isNetworkAuthed(){
//		return AppSwitch.NETWORK_PERMISSION;
	//}
	
	/**
	 * 当前是否wifi网络下
	 * @return
	 */
	public static boolean isWifi(Context context){
		boolean res = false;
		if (context != null) {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (info != null) {
				if (info.isConnected()) {
					if (info.getType() == ConnectivityManager.TYPE_WIFI) {
						res = true;
					} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
						res = false;
					}
				}
			}
		}
		return res;
	}
	
	/**
	 * 用户是否选择只能在wifi下访问
	 * @return
	 */
	public static boolean isOnlyInWifi(){
		//FIXME 是否开放该功能
		return false;
	}
	
}
