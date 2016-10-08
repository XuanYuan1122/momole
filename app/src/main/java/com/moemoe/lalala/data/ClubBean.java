package com.moemoe.lalala.data;

import android.content.Context;
import android.text.TextUtils;

import com.app.annotation.Column;
import com.app.annotation.Table;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 
 * @author Ben
 * 
 */
public class ClubBean{

	private static final String TAG = "ClubBean";

	public enum Relation {
		NONE, FOLLOWER, CREATOR
	}

	public static final String TYPE_OFFICAL = "OFFICE";
	public static final String TYPE_NORMAL = "NORMAL";
	
	public static final String TYPE_ROLE_PUBLIC = "PUBLIC";
	public static final String TYPE_ROLE_PRIVATE = "PRIVATE";

	public static final int TITLE_LIMIT = 10;
	public static final int CONTENT_LIMIT = 140;

	/**
	 * DB ID
	 */
	public long id;
	public String uuid;

	public String title;

	public String description;

	public String type;
	
	public String type_role;

	public String recommand;

	/**
	 * 第一版社团没有url界面
	 */
	public String url;

	public String icon_uuid;

	public Image icon;
	
	/**
	 * 1.5.0 加入，官方社团的背景
	 */
	public String background_uuid;
	/**
	 * 1.5.0 加入，官方社团的背景
	 */
	public Image background;
	/**
	 * 1.5.0加入，官方社团名称图
	 */
	public String list_bg_uuid;
	public Image list_bg;
	

	/**
	 * 关系： 0：无关 ， 1：成员； 2：创建者 <br>
	 * values in {@linkplain Relation}
	 */
	public int relation;
	/**
	 * 是否过审核
	 */
	public String active_status;

	/**
	 * ？？
	 */
	public long active_time;

	/**
	 * 冻结状态
	 */
	public String frozen_status;

	/**
	 * 冻结时间
	 */
	public long frozen_time;

	public String creator_id;

	public long create_time;

	public String update_user;

	/**
	 * 社团的最后更新时间
	 */
	public long update_time;

	/**
	 * 最新帖子发布时间
	 */
	public long last_doc_time;

	/**
	 * 
	 */
	public int gift_num;

	public int follower_num;

	/**
	 * 当前社团的贴子数
	 */
	public int doc_num;
	
	
	/**
	 * 热度
	 */
	public int hot_sum;

	/**
	 * 置顶
	 */
	public String top_status;

	public long top_start_time;

	public long top_end_time;

	public int top_num;
	/**
	 * 标签
	 */
	public String[] tag;

	public int version;

	/**
	 * 官方社团，并且用户不能发帖，显示标题，不显示用户属性
	 * @return
	 * @author Ben
	 */
	public boolean isOfficePrivateClub() {
		return isOfficalClub() && !isCanPublishDoc();
	}
	
	public boolean isOfficalClub() {
		//TODO 测试
//		return true;
		return TYPE_OFFICAL.equals(type);
	}

	/**
	 * 是否有发帖权限
	 * @return
	 */
	public boolean isCanPublishDoc() {
		return !TYPE_ROLE_PRIVATE.equals(type_role);
	}
	
	public String getCreateJsonString() {
		String res = null;
		try {
			JSONObject json = new JSONObject();
			json.put("name", title);
			json.put("desc", description);
			json.put("icon", icon_uuid);
			JSONArray array = new JSONArray();
			for (String otag : this.tag) {
				array.put(otag);
			}
			json.put("tags", array);
			res = json.toString();
		} catch (Exception e) {
		}
		return res;
	}

	public static ArrayList<ClubBean> readFromJsonList(Context context, String jsonContent) {
		ArrayList<ClubBean> res = new ArrayList<ClubBean>();
		try {
			JSONArray array = new JSONArray(jsonContent);
			for (int i = 0; i < array.length(); i++) {
				JSONObject one = (JSONObject) array.get(i);
				ClubBean bean = new ClubBean();
				bean.readFromJsonContent(context, one.toString(), true);
				res.add(bean);
			}

		} catch (Exception e) {
		}
		return res;
	}
	
	public void readFromJsonContent(Context context,String jsonContent,String preFix,boolean isBatch){
		try {
			JSONObject json = new JSONObject(jsonContent);
			if (TextUtils.isEmpty(uuid)) {
				uuid = json.optString("id");
			}
			int num = json.optInt("nice_sum");
			if (num > 0) {
				gift_num = num;
			}
			num = json.optInt("doc_sum");
			if (num > 0) {
				doc_num = num;
			}
			active_status = json.optString("active_status");
			active_time = StringUtils.getServerTime(json.optString("active_time"));
			frozen_status = json.optString("frozen_status");
			frozen_time = StringUtils.getServerTime(json.optString("forzen_time"));

			creator_id = json.optString("create_user");
			// 不需要获取团长的身份

			create_time = StringUtils.getServerTime(json.optString("create_time"));
			update_user = json.optString("update_user");
			update_time = StringUtils.getServerTime(json.optString("update_time"));
			last_doc_time = StringUtils.getServerTime(json.optString("last_doc_time"));

			top_status = json.optString("top_staus");
			top_start_time = StringUtils.getServerTime(json.optString("top_start_time"));
			top_end_time = StringUtils.getServerTime(json.optString("top_end_time"));

			version = json.optInt("version");
			if(!TextUtils.isEmpty(preFix)){
				uuid = json.optString(preFix + "id");
				title = json.optString(preFix + "name");
				description = json.optString(preFix + "desc");
				type = json.optString(preFix + "type");
				recommand = json.optString(preFix + "recommend_status");
				icon_uuid = json.optString(preFix + "icon_name");
				icon = new Image();
				icon.real_path = Otaku.URL_QINIU +  icon_uuid;
				icon.w = json.optInt(preFix + "icon_width");
				icon.h = json.optInt(preFix + "icon_height");
				icon.path = StringUtils.getUrl(context, icon_uuid, icon.w, icon.h);
				if (TextUtils.equals(creator_id, PreferenceManager.getInstance(context).getUUid())) {
					relation = Relation.CREATOR.ordinal();
				} else {
					relation = Otaku.SERVER_Y.equals(json.optString(preFix + "mark_status")) ? Relation.FOLLOWER.ordinal()
							: Relation.NONE.ordinal();
				}
				num = json.optInt(preFix + "mark_sum");
				follower_num = num;
				JSONArray tagArray = json.optJSONArray(preFix + "tags");
				if (tagArray != null) {
					tag = new String[tagArray.length()];
					for (int i = 0; i < tag.length; i++) {
						tag[i] = tagArray.getJSONObject(i).optString("name");
					}
				}
			}else{
				title = json.optString("name");
				description = json.optString("desc");
				type = json.optString("type");
				recommand = json.optString("recommend_status");
				icon_uuid = json.optString("icon_name");
				icon = new Image();
				icon.real_path = Otaku.URL_QINIU +  icon_uuid;
				icon.w = json.optInt("icon_width");
				icon.h = json.optInt("icon_height");
				icon.path = StringUtils.getUrl(context, icon_uuid, icon.w, icon.h);
				if (TextUtils.equals(creator_id, PreferenceManager.getInstance(context).getUUid())) {
					relation = Relation.CREATOR.ordinal();
				} else {
					relation = Otaku.SERVER_Y.equals(json.optString("mark_status")) ? Relation.FOLLOWER.ordinal()
							: Relation.NONE.ordinal();
				}
				num = json.optInt("mark_sum");
				follower_num = num;
				JSONArray tagArray = json.optJSONArray("tags");
				if (tagArray != null) {
					tag = new String[tagArray.length()];
					for (int i = 0; i < tag.length; i++) {
						tag[i] = tagArray.getJSONObject(i).optString("name");
					}
				}
			}
			if (!Otaku.SERVER_Y.equals(recommand)) {
				recommand = Otaku.SERVER_N;
			}
			type_role = json.optString("type_role");
			background_uuid = json.optString("background_name");
			if (!TextUtils.isEmpty(background_uuid)) {
				background = new Image();
				background.real_path = Otaku.URL_QINIU + background_uuid;
				background.w =  json.optInt("background_width");
				background.h = json.optInt("background_height");
				background.path = StringUtils.getUrl(context,background_uuid,background.w,background.h);
			}
			list_bg_uuid = json.optString("background_x1_name");
			if(!TextUtils.isEmpty(list_bg_uuid)){
				list_bg = new Image();
				list_bg.real_path = Otaku.URL_QINIU + list_bg_uuid;
				list_bg.w = json.optInt("background_x1_width");
				list_bg.h = json.optInt("background_x1_height");
				list_bg.path = StringUtils.getUrl(context, list_bg_uuid, list_bg.w, list_bg.h);
			}
			// active_status string
			// active_time string
			// frozen_status string
			// forzen_time string
			// create_user string
			// create_time string
			// update_user string
			// update_time string
			// version string
			// mark_status string
			// tags []标签

		} catch (Exception e) {
		}
	}

	public void readFromJsonContent(Context context, String jsonContent, boolean isBatch) {
		readFromJsonContent(context,jsonContent,null,isBatch);
	}

	@Override
	public boolean equals(Object o) {
		boolean res = false;
		if (o != null && o instanceof ClubBean) {
			ClubBean other = (ClubBean) o;
			if (!TextUtils.isEmpty(other.uuid) && other.uuid.equals(uuid)) {
				res = true;
			}
		}

		return res;
	}

}
