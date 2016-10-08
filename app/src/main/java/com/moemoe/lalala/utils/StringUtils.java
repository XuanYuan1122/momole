package com.moemoe.lalala.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.data.ClubBean;
import com.moemoe.lalala.network.Otaku;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	private static final String TAG = "StringUtils";
	
	public static final long TIME_ONE_DAY = 24 * 3600 * 1000;
	public static final long TIME_ONE_HOUR = 3600 * 1000;
	public static final long TIME_ONE_MIN = 60 * 1000;
	
	private static SimpleDateFormat sServerDate = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sServerTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static SimpleDateFormat sYearMthDay = new SimpleDateFormat("yyyy年M月d日");
	
	private static SimpleDateFormat sMthDay = new SimpleDateFormat("M月d日");
	
	private static SimpleDateFormat sMthDayUS = new SimpleDateFormat("M.d");
	
	private static SimpleDateFormat sYearMthDayUS = new SimpleDateFormat("yyyy.M.d");
	
	private static SimpleDateFormat sNormal = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
	
	private static Pattern sEmailPattern = Pattern.compile("[\\w\\.\\-\\+]+@([\\w\\-\\+]+\\.)+[\\w\\-\\+]+", Pattern.CASE_INSENSITIVE);
	
	private static Pattern sNickNamePattern;
	
	private static Pattern sPasswordPattern;
	
	private static Pattern sWebUrlPattern;
	
	public static long getServerDate(String server){
		long time = 0;
		if(!TextUtils.isEmpty(server) && !"0001-01-01 00:00:00".equals(server) && !"0001-01-01".equals(server)){
			try {
				time = sServerDate.parse(server).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return time;
	}
	
	public static long getServerTime(String server){
		long time = 0;
		if(!TextUtils.isEmpty(server) && !"0001-01-01 00:00:00".equals(server) && !"0001-01-01".equals(server)){
			try {
				time = sServerTime.parse(server).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return time;
	}
	
	public static String toServerTimeString(long time){
		return sServerTime.format(new Date(time));
	}
	
	public static String toServerDateString(long time){
		return sServerDate.format(new Date(time));
	}
	
	public static String getMthDayUs(long time) {
		return sMthDayUS.format(new Date(time));
	}
	
	/**
	 * 我的课表- 日期tab项
	 * @param
	 * @return
	 */
	public static String getMyScheduleDateLabel(long time){
		String res = null;
		if(time > 0){
			Date date = new Date(time);
			if(date.getYear() == new Date().getYear()){
				// 同年
				res = sMthDay.format(date.getTime());
			}else{
				res = sYearMthDay.format(date.getTime());
			}
		}
		return res;
	}
	
	public static String getDateTime(long time){
		if(time > 0){
			return sYearMthDayUS.format(time);
		}else{
			return null;
		}
	}
	
	/**
	 * yyyy.M.d
	 * @param time
	 * @return
	 * @author Ben
	 */
	public static String getNormalUsDate(long time){
		if(time > 0){
			return sYearMthDayUS.format(time);
		}else{
			return null;
		}
	}
	
	/**
	 * yyyy年M月d日
	 * @param time
	 * @return
	 * @author Ben
	 */
	public static String getNormalDate(long time){
		if(time > 0){
			return sYearMthDay.format(time);
		}else{
			return null;
		}
	}
	
	public static String getNormalTime(long time){
		if(time > 0){
			return sNormal.format(time);
		}else{
			return null;
		}
	}
	
	public static String getEventDuration(long start, long end){
		String res = null;
		if(end > start){
			res = sYearMthDayUS.format(new Date(start)) + " - " + sYearMthDayUS.format(new Date(end));	
		}else{
			res = sYearMthDayUS.format(new Date(start));
		}
		return res;
	}

	/**
	 * 
	 * @return
	 */
	public static String getUpdateTime(long serverTime){
		String res = null;
		if(serverTime > 0){
		}
		long dura = System.currentTimeMillis() - serverTime;
		if(dura < TIME_ONE_DAY && dura >= 0){
			int h = (int)(dura / TIME_ONE_HOUR);
			int m = (int)((dura % TIME_ONE_HOUR) / TIME_ONE_MIN);
			int s = (int)(dura % TIME_ONE_MIN / 1000);
			if(h > 0){
				res = h + "小时前";
			}else if(m > 0){
				res = m + "分钟前";
			}else{
				res = s + "秒前";
			}
		}else{
			if(new Date().getYear() == new Date(serverTime).getYear()){
				res = sMthDay.format(new Date(serverTime));
			}else{
				res = sYearMthDay.format(new Date(serverTime));
			}
		}
		
		return res;
	}
	
	/**
	 * 带位数限制的数字: 1000, 3 -> 999+
	 * @param num
	 * @param limit
	 * @return
	 */
	public static String getNumberInLengthLimit(int num, int limit){
		String res = num + "";
		int rl = (int)Math.pow(10, limit);
		if(num > rl){
			res = (rl - 1) + "+";
		}
		return res;
	}
	
	
	/**
	 * 判断是否是邮箱，支持“+”,"-"号
	 * @param email
	 * @return
	 */
	public static boolean isEmailFormated(String email) {
		Matcher matcher = sEmailPattern.matcher(email);
		boolean res =  matcher.matches();
		return res;
	}
	
	/**
	 * 账户敏感信息隐藏后的字符串： abh@gmail.com -> abh ; 15618818212 -> 156****212
	 * @param account
	 * @return
	 */
	public static String getPrivateHiddenAccountAddress(String account){
		//邮箱检查及手机号码隐藏中间4位
		String res = account;
		if(isEmailFormated(account)){	// email
			res = account.substring(0, account.indexOf("@") - 1);
		}else if(PhoneUtil.isPossiablePhoneFormated(account)){
			//FIXME 电话号码，保留前3位，后3位, 不太合理
			if(account.length() > 6){
				StringBuffer sb = new StringBuffer();
				for(int i = 0; i < account.length() - 6; i++){
					sb.append("*");
				}
				res = account.substring(0, 3) + sb.toString() + account.substring(account.length() - 3, account.length());
			}
		}
		return res;
	}
	
	/**
	 * 昵称中不能包含特殊字符：`~!@#$%^&*()+=|{}':;',[].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？
	 * @param nickName
	 * @return
	 */
	public static boolean isLeagleNickName(String nickName){
		if(sNickNamePattern == null){
			//sNickNamePattern = Pattern.compile("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\n\\s]");
			sNickNamePattern = Pattern.compile("^[A-Za-z0-9\u4e00-\u9fa5]+$");
		}
		if(sNickNamePattern.matcher(nickName).find()){	// email
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isLeagleClubTitle(String clubTitle){
		boolean res = false;
		if(!TextUtils.isEmpty(clubTitle) && isLeagleNickName(clubTitle) && clubTitle.length() <= ClubBean.TITLE_LIMIT){
			res = true;
		}
		return res;
	}
	
	public static boolean isLeagleVCode(String vCode){
		boolean res = false;
		if(vCode.length() == 6){
			res = true;
			for(int i = 0; i < vCode.length(); i++){
				if(vCode.charAt(i) < '0' && vCode.charAt(i) > '9'){
					res = false;
					break;
				}
			}
		}
		return res;
	}
	
	/**
	 * 是否是合法的密码,规则是英文数字，常规特殊符号，不限制长度
	 * @param password
	 * @return
	 */
	public static boolean isLegalPassword(String password){
		boolean isLegal = false;
		if(sPasswordPattern == null){
			sPasswordPattern = Pattern.compile("\\w{6,15}$");
		}
		if(!TextUtils.isEmpty(password)){
			isLegal = sPasswordPattern.matcher(password).matches();
		}
		return isLegal;
		
	}

	/**
	 * 转换成DB tag
	 * @return
	 */
	public static String toDbStr(String[] tag) {
		StringBuilder sb = new StringBuilder();
		if(tag != null){
			for (int i = 0; i < tag.length; i++) {
				sb.append(tag[i] + ",");
			}
		}
		if (sb.length() > 0) {
			return sb.substring(0, sb.length() - 1);
		} else {
			return "";
		}
	}

	/**
	 * 读取db中的tag string
	 * @param tag
	 * @return
	 */
	public static String[] readDbConcatStr(String tag){
		if(!TextUtils.isEmpty(tag)){
			return tag.split(",");
		}else{
			return null;
		}
	}
	
	
	public static String getSentenceTimeStr(long time){
		String ret = "";
		if(time > 0){
			ret = (time / 3600) + ":" + (time % 3600 / 60);
		}
		return ret;
	}
	
	public static long parseSentenceTime(String timeStr){
		long time = 0;
		if(!TextUtils.isEmpty(timeStr)){
			String[] part = timeStr.split(":");
			int h = Integer.valueOf(part[0]);
			int m = Integer.valueOf(part[1]);
			time = h * 3600 + m * 60;
		}
//		LogUtils.LOGD(TAG, "timeStr = " + timeStr + " to " + time);
		return time;
	}

	public static boolean matchCurrentTime(long start, long end){
		boolean res = false;
		Calendar time = Calendar.getInstance();
		int t = time.get(Calendar.HOUR_OF_DAY) * 3600 + time.get(Calendar.MINUTE) * 60;
		if(t < end && t >= start){
			res = true;
		}
		return res;
	}

	public static boolean isyoru(){
		boolean res = false;
		boolean a = matchCurrentTime(parseSentenceTime("18:00"), parseSentenceTime("24:00"));
		boolean b = matchCurrentTime(parseSentenceTime("00:00"),parseSentenceTime("06:00"));
		if(a || b){
			res = true;
		}
		return res;
	}

	public static boolean isKillEvent(){
		return matchCurrentTime(parseSentenceTime("00:00"),parseSentenceTime("01:00"));
	}

	public static boolean matchCurrentTime(String startTime,String endTime){
		long start = parseSentenceTime(startTime);
		long end = parseSentenceTime(endTime);
		boolean res = false;
		Calendar time = Calendar.getInstance();
		int t = time.get(Calendar.HOUR_OF_DAY) * 3600 + time.get(Calendar.MINUTE) * 60;
		if(t < end && t >= start){
			res = true;
		}
		return res;
	}
	
	/**
	 * 获取今天0点的时间
	 * @return
	 */
	public static Date getTodayDate(){
		Calendar calendar = Calendar.getInstance();
		Date date = new Date(calendar.get(Calendar.YEAR) - 1900, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		return date;
	}
	
	public static Date getYesterDayDate(){
		Calendar calendar = Calendar.getInstance();
		Date date = new Date(calendar.get(Calendar.YEAR) - 1900, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) - 1);
		return date;
	}
	
	public static Date getDayStartDate(long start){
		Date date = new Date(start);
		return date;
//		Calendar calendar = ne;
//		return new Date(date.getYear() - 1900, date.getMonth(), date.getDay());
	}
	
	/**
	 * JSON字符串特殊字符处理，比如：“\A1;1300”
	 * 
	 * @param s
	 * @return String
	 */
	public static String string2Json(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '\"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '/':
				sb.append("\\/");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * 文本增加网址监听
	 * @param context
	 * @param text
	 * @return
	 */
	public static SpannableString getUrlClickableText(Context context, String text){
		if(sWebUrlPattern == null){
			//sWebUrlPattern = Pattern.compile("((https://|http://)([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?)|www.(([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?)");
			sWebUrlPattern = Pattern.compile("(http|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?");
		}
		if(!TextUtils.isEmpty(text)){
			SpannableString ss = new SpannableString(text);
			Matcher m = sWebUrlPattern.matcher(text);
			while(m.find()){
				int start = m.start();
				int end = m.end();
				CustomUrlSpan span = new CustomUrlSpan(context, null, text.substring(start, end));
				ss.setSpan(span, start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			return ss;
		} else {
			return null;
		}
	}
	
	/**
	 * 获取表示文件打下的字符串：
	 * 10240 = 10kb
	 * 1024 * 1024 * 3 = 3mb
	 * @param fileSize
	 * @return
	 */
	public static String getFileSizeString(long fileSize) {
		int level = 0;
		float nfs = fileSize;
		while (nfs / 1024 >= 1 && level < 4) {
			nfs = nfs / 1024;
			level ++;
		}
		String ret = String.format("%.2f", nfs);
		if (level == 0) {
			ret = ret + "b";
		} else if (level == 1) {
			ret = ret + "kb";
		} else if (level == 2) {
			ret = ret + "mb";
		} else if (level == 3) {
			ret = ret + "gb";
		}
		return ret;
	}
	
	/**
	 * 高亮关键字
	 * @param value
	 * @param keyWord
	 * @return
	 * @author Haru
	 */
	@SuppressLint("DefaultLocale")
	public static SpannableString highLightKeyWord(Context ctx,String value,String keyWord){
		if(!TextUtils.isEmpty(keyWord) && !TextUtils.isEmpty(value)){
			//String temp1 = value.toLowerCase();
			//String temp2 = keyWord.toLowerCase();
//			if(temp1.contains(temp2)){
//				int index = temp1.indexOf(temp2);
//				int live2d.len = temp2.length();
//				Spanned temp = Html.fromHtml(value.substring(0, index)
//						+ "<font color=#78D7C8>"
//						+ value.substring(index, index + live2d.len)
//						+ "</font>"
//						+ value.substring(index + live2d.len,value.length()));
//				return temp;
//			}
			SpannableString s = new SpannableString(value);
			Pattern p = Pattern.compile(keyWord,Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(s);
			int color = ctx.getResources().getColor(R.color.main_title_cyan);
			while (m.find()) {
	            int start = m.start();
	            int end = m.end();
	            s.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	        }
			return s;
		}
		return null;
	}
	
	
	public static int getHashOfString(String text, int limit) {
		int ret = 0;
		if (!TextUtils.isEmpty(text)) {
			ret = text.charAt(0) % limit;
		}
		return ret;
	}


	/**
			* AutoCompleteTextView 邮箱自动补全的{@link TextWatcher#afterTextChanged(android.text.Editable)} 方法响应事件
	* 在其中设置邮箱列表，如果不符合邮箱列表，则加载预置补全项
	*
			* @param context
	* @param actv
	*            AutoCompleteTextView
	* @param newText
	*            当前text in AutoCompleteTextView
	* @param needSetDefault
	*            当newText不符合邮箱格式时，是否需要设置默认补全adapter
	* @param defaultAdapter
	*            默认补全adapter
	* @return true if 成功设置了email adpater， false if设置了defaut adpater；如果没有设置adpater，返回值与needSetDefault相同
	*/
	public static boolean onEmailAutoCompleteTvTextChanged(Context context, AutoCompleteTextView actv, String newText,
														   boolean needSetDefault, ArrayAdapter<String> defaultAdapter) {
		boolean enable = needSetDefault;
		if (newText != null) {
			int len = newText.length();
			if (len > 0) {
				if (newText.contains("@")) {
					// 邮箱后缀补全
					if ("@".equals(newText.subSequence(len - 1, len))) {
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
								R.layout.simple_dropdown_item_1line, EmailSufix.getFormatedEmails(newText));
						actv.setAdapter(adapter);
					}
					enable = true;
				} else if (needSetDefault && defaultAdapter != null) {
					actv.setAdapter(defaultAdapter);
					enable = false;
				}
				ListAdapter apdapter = actv.getAdapter();
				if (apdapter != null && apdapter.getCount() == 1 && newText.equals(apdapter.getItem(0))) {
					actv.dismissDropDown();
				}
			}
		}
		return enable;
	}

	/**
	 * Copy Text to Android ClipBoard
	 *
	 * @param context
	 * @param text
	 *            text to be copied
	 * @return true if success
	 */
	@SuppressWarnings("deprecation")
	public static boolean copyStringToClipBoard(Context context, String text) {
		boolean res = false;
		android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		if (clipboardManager != null) {
			try {
				clipboardManager.setText(text);
				Toast.makeText(context, R.string.util_a_msg_copy_url_success, Toast.LENGTH_SHORT).show();
				res = true;
			} catch (Exception e) {
				res = false;
			}
		}
		return res;
	}

	private static String getFormatDate(String year, String month, String day){
		StringBuffer sBuffer = new StringBuffer();
		if(month.length() < 2){
			month = "0" + month;
		}
		if(day.length() < 2){
			day = "0" + day;
		}
		sBuffer.append(year).append(month).append(day);
		return sBuffer.toString();
	}

	public static String timeFormate(String str){
		if(!TextUtils.isEmpty(str)){
			try {
				Calendar calendar = Calendar.getInstance();
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day1 = calendar.get(Calendar.DAY_OF_MONTH);
				String nowDay = getFormatDate(String.valueOf(year), String.valueOf(month + 1), String.valueOf(day1));
				String res = "";
				String[] temp = str.split(" ");
				String day = temp[0];
				String time = temp[1];
				String reDay = day.replace("-","");
				if(reDay.equals(nowDay)){
					Calendar c = Calendar.getInstance();
					try {
						c.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					long temp1 = System.currentTimeMillis() - c.getTimeInMillis();
					StringBuffer sb = new StringBuffer();
					long mill = (long) Math.ceil(temp1 /1000);//秒前
					long minute = (long) Math.ceil(temp1/60/1000.0f);// 分钟前
					long hour = (long) Math.ceil(temp1/60/60/1000.0f);// 小时
					if (hour - 1 > 0) {
						if (hour >= 24) {
							sb.append("1天");
						} else {
							sb.append(hour + "小时");
						}
					}else if (minute - 1 > 0) {
						if (minute == 60) {
							sb.append("1小时");
						} else {
							sb.append(minute + "分钟");
						}
					} else if (mill - 1 > 0) {
						if (mill == 60) {
							sb.append("1分钟");
						} else {
							sb.append(mill + "秒");
						}
					} else {
						sb.append("刚刚");
					}
					if (!sb.toString().equals("刚刚")) {
						sb.append("前");
					}
					res = sb.toString();
				}else {
					res = day;
				}
				return res;
			}catch (Exception e){
				return "";
			}
		}
		return "";
	}

	public static String getUrl(Context context,String path,int width,int height){
		String res = Otaku.URL_QINIU + path;
		//boolean isLow = !NetworkUtils.isWifi(context) && AppSetting.IS_DOWNLOAD_LOW_IN_3G;
//		if(width > 0){
//			res = res + "?imageView2/0/w/" + width + "/h/" + height;
//		}else{
//			res = res + "?imageView2/0/w/" + width + "/h/" + height;
//		}
		return res;
	}

	public static String getUrl(Context context,String path,int width,int height,boolean isDocDetail,boolean isDoc){
		String res = path;
		boolean isLow = !NetworkUtils.isWifi(context) && !AppSetting.IS_DOWNLOAD_LOW_IN_3G;
		if(width > 0){
			if(isLow){
				width = width / 2;
				if(width > 600) width = 600;
			}
			if(isDoc && !isDocDetail){
				res = res + "?imageView2/1/w/" + width + "/h/" + height + "/format/jpg";
			}else if(isDocDetail && isDoc){
				res = res + "?imageView2/2/w/" + width + (isLow ? "/q/60" : "");
			}else {
				res = res + "?imageView2/0/w/" + width + "/h/" + height;
			}
		}else{
			res = res + "?imageView2/2/w/" + width + (isLow ? "/q/60" : "");
		}
		return res;
	}
}
