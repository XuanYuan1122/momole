package com.moemoe.lalala.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.moemoe.lalala.R;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 除你之外的其他人，json数据类
 * @author Ben
 *
 */
public class PersonBean extends BasicBean {
	
	private static final String TAG = "PersonBean";

	public static final String SEX_MALE = "M";
	public static final String SEX_FEMALE = "F";
	public static final String SEX_NONE = "N";
	
	/**
	 * 数据库本地id
	 */
	public long id;
	
	public String uuid;
	
	public String mobile;
	
	public String name;
	
	public String sex_str;
	
	public int nice_num;
	
//	public String remark_name;
	
	public String slogan;
	
	public String icon_uuid;
	
	public int relation;
	
	public long register_time;
	
	public long birthday;
	
	/**
	 * 当前等级名称
	 */
	public String level_name;
	
	/**
	 * 当前积分
	 */
	public int score;
	/**
	 * 等级
	 */
	public int level;
	/**
	 * 当前等级积分上限
	 */
	public int level_score_end;
	/**
	 * 当前等级积分开始；
	 */
	public int level_score_start;
	
	public int level_color;
	
	public int version;
	
	public String award_status;
	
	public Image icon;

	public int coin;
	
	public static ArrayList<PersonBean> readFromJsonList(Context context, String jsonContent){
		ArrayList<PersonBean> res = new ArrayList<PersonBean>();
		try {
			JSONArray array = new JSONArray(jsonContent);
			for(int i = 0; i < array.length(); i++){
				JSONObject one = (JSONObject)array.get(i);
				PersonBean bean = new PersonBean();
				bean.readFromJsonContent(context, one.toString());
				res.add(bean);
			}
			
		} catch (Exception e) {
		}
		return res;
	}
	
	public void readFromJsonContent(Context context, String jsonContent){
		String res = null;
		try {
			JSONObject json = new JSONObject(jsonContent);
			if(TextUtils.isEmpty(uuid)){
				uuid = json.optString("id");
			}
			award_status = json.optString("award_status");
			name = json.optString("nickname");
			mobile = json.optString("user_id");
			birthday = StringUtils.getServerDate(json.optString("birthday"));
			sex_str = json.optString("sex");
			register_time = StringUtils.getServerTime(json.optString("register_time"));
			nice_num = json.optInt("nice_sum");
			icon_uuid =  Otaku.URL_QINIU  + json.optString("icon");
			icon = new Image();
			icon.real_path = icon_uuid;
			icon.w = json.optInt("icon_width");
			icon.h = json.optInt("icon_height");
			icon.path = StringUtils.getUrl(context,json.optString("icon"),icon.w,icon.h);
			// 积分相关
			level_name = json.optString("level_name");
			score = json.optInt("score");
			level = json.optInt("level");
			level_score_start = json.optInt("level_score_beg");
			level_score_end = json.optInt("level_score_end");
			level_color = readColorStr(json.optString("level_color"), context.getResources().getColor(R.color.main_title_cyan));
			coin = json.optInt("coin");
			version = json.optInt("version");

		} catch (Exception e) {
		}
	}

	public static PersonBean createPersonBeanFromAuth(AuthorInfo info){
		PersonBean personBean = new PersonBean();
		personBean.uuid = info.getmUid();
		personBean.name = info.getmUserName();
		personBean.sex_str = info.getmGender();
		personBean.nice_num = info.getNice_num();
		personBean.slogan = info.getSlogan();
		personBean.icon_uuid = info.getmHeadPath();
		personBean.icon = new Image();
		personBean.icon.path = info.getmHeadPath();
		personBean.register_time = info.getRegister_time();
		personBean.birthday = info.getBirthday();
		personBean.level_name = info.getLevel_name();
		personBean.score = info.getScore();
		personBean.level = info.getLevel();
		personBean.level_score_end = info.getLevel_score_end();
		personBean.level_score_start = info.getLevel_score_start();
		personBean.level_color = info.getLevel_color();
		return personBean;
	}
}
