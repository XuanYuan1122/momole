package com.moemoe.lalala.data;

import android.content.Context;
import android.text.TextUtils;

import com.app.annotation.Column;
import com.app.annotation.Table;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

@Deprecated
@Table(name = "top_banner")
public class TopAdBean {
	
	private static final String TAG = "TopAdBean";
	
//	id	string	
//	target_id	string	对象编号
//	recommend	string	推荐
//	target_type	string	类型
//	target_name	string	对象名称
//	target_icon	string	图片名称
//	tagret_icon_width	int	图片宽度
//	target_icon_height	int	图片高度
//	top_no	int	置顶数
//	begin_time	string	开始日期
//	end_time	string	结束日期
//	target_user	string	对象创建人
//	target_time	string	对象创建日期
//	create_user	string	
//	create_time	string	
//	update_user	string	
//	update_time	string	
//	delete_user	string	
//	delete_time	string	
//	version	int	

	public static final String TYPE_DOC = "DOC";
	public static final String TYPE_CLUB = "CLUB";
	public static final String TYPE_EVENT = "EVENT";
	
	@Column(name = "uuid" ,isId = true,autoGen = false)
	public String uuid;
	@Column(name = "json")
	public String json;
	
	public String target_id;
	
	/**
	 * Y/N
	 */
	public String recommand;
	
	/**
	 * DOC/CLUB/EVENT
	 */
	public String target_type;
	
	public String target_name;
	
	public String target_icon;
	public int target_icon_width;
	public int target_icon_height;
	
	public int top_no;
	
	public long begin_time;
	public long end_time;
	public long target_time;
//	public String target_user;
//	public String target_time;
//	public String create_user;
//	public String create_time;
//	public String update_user;
//	public String update_time;
//	public String delete_user;
//	public String delete_time;
//	public int version;
	
	public Image icon;
	
	
	public static ArrayList<TopAdBean> readFromJsonList(Context context, String jsonContent){
		ArrayList<TopAdBean> res = new ArrayList<TopAdBean>();
		try {
			JSONArray array = new JSONArray(jsonContent);
			for(int i = 0; i < array.length(); i++){
				JSONObject one = (JSONObject)array.get(i);
				TopAdBean bean = new TopAdBean();
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
//			id	string	
//			doc_id	string	
//			user_id	string	评论人编号
//			club_id	string	
//			content	string	内容
//			comment_to	string	被评论者编号
//			delete_time	string	
//			delete_status	string	
//			create_time	string	
			JSONObject json = new JSONObject(jsonContent);
			if(TextUtils.isEmpty(uuid)){
				uuid = json.optString("id");
			}
			target_id = json.optString("target_id");
			recommand = json.optString("recommand");
			target_type = json.optString("target_type");
			target_name = json.optString("target_name");
			target_icon = Otaku.URL_QINIU + json.optString("target_icon");
			target_time = StringUtils.getServerTime(json.optString("target_time"));
			icon = new Image();
			if(!TextUtils.isEmpty(target_icon)){
				icon.real_path = target_icon;
				icon.w = json.optInt("target_icon_width");
				icon.h =  json.optInt("target_icon_height");
				icon.path = StringUtils.getUrl(context,json.optString("target_icon"),icon.w,icon.h);
			}
			
			
			top_no = json.optInt("top_no");
			begin_time = StringUtils.getServerTime(json.optString("begin_time"));
			end_time = StringUtils.getServerTime(json.optString("end_time"));
		} catch (Exception e) {
		}
	}
}
