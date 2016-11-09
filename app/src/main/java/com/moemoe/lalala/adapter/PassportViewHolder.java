package com.moemoe.lalala.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.common.util.DensityUtil;
import com.moemoe.lalala.EditAccountActivity;
import com.moemoe.lalala.ImageBigSelectActivity;
import com.moemoe.lalala.PersonalLevelActivity;
import com.moemoe.lalala.R;
import com.moemoe.lalala.data.AuthorInfo;
import com.moemoe.lalala.data.Image;
import com.moemoe.lalala.data.PersonBean;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.StringUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

;


/**
 * 学生证 view holder
 * 
 * 包含学生view加载
 * @author Ben
 *
 */
public class PassportViewHolder implements OnClickListener{

	private static final String TAG = "PassportViewHolder";

	public static final int TYPE_SELF = 0;
	public static final int TYPE_FRIEND = 1;

	
	private Context context;
	
	public View rootView;
	public ImageView ivAvatar;
	public ImageView ivMale;
	
	public View ivLevelColor;
	public TextView tvLevel;
	public TextView tvScore;
	public TextView tvCoin;
	public ProgressBar pbScore;
	
	
	public TextView tvName;
	public TextView tvLevelName;
	public View ivLevelDetail;
	public TextView tvToSchoolTime;
	public TextView tvBirthday;
	
	public View tvBirthdayLabel;
	
	public View ivEdit;
	
	//------------------- fields ---------------------------
	public PersonBean data;


	
	
	public PassportViewHolder(Context context, View root,int type) {
		this.rootView = root;
		this.context = context;
		if (rootView != null) {
			ivAvatar = (ImageView)rootView.findViewById(R.id.iv_avatar);
			ivMale = (ImageView)rootView.findViewById(R.id.iv_passport_xingbie);
			
			ivLevelColor = rootView.findViewById(R.id.iv_level_bg);
			tvLevel = (TextView)rootView.findViewById(R.id.tv_level);
			tvScore = (TextView)rootView.findViewById(R.id.tv_curr_score);
			pbScore = (ProgressBar) rootView.findViewById(R.id.pb_curr_score);
			
			
			tvToSchoolTime = (TextView)rootView.findViewById(R.id.tv_content_in_school_time);
			tvName = (TextView)rootView.findViewById(R.id.tv_content_name);
			tvLevelName = (TextView) rootView.findViewById(R.id.tv_content_level_name);
			ivLevelDetail = rootView.findViewById(R.id.iv_level_name_details);
			tvBirthday = (TextView)rootView.findViewById(R.id.tv_content_birthday);
			tvBirthdayLabel = rootView.findViewById(R.id.tv_label_birthday);
			ivEdit = rootView.findViewById(R.id.iv_passport_edit);
			tvCoin = (TextView) rootView.findViewById(R.id.tv_content_jiecao);
			
			ivEdit.setOnClickListener(this);
			ivLevelDetail.setOnClickListener(this);
			if(type == TYPE_SELF){
				ivLevelDetail.setVisibility(View.VISIBLE);
			}else{
				ivLevelDetail.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	public void setPersonBean(PersonBean bean) {
		data = bean;
		loadView();
	}
	
	
	public void loadView() {
		if(data != null){
			AuthorInfo info = PreferenceManager.getInstance(context).getThirdPartyLoginMsg();
			boolean isMyself = TextUtils.equals(data.uuid, info.getmUUid());
			
			if (isMyself) {
				tvBirthday.setVisibility(View.VISIBLE);
				tvBirthdayLabel.setVisibility(View.VISIBLE);
				ivEdit.setVisibility(View.VISIBLE);
				
			} else {
				tvBirthday.setVisibility(View.GONE);
				tvBirthdayLabel.setVisibility(View.GONE);
				ivEdit.setVisibility(View.GONE);
				
			}
			
			if(data.icon != null){
				Picasso.with(context)
						.load(data.icon.path)
						.resize(DensityUtil.dip2px(80),DensityUtil.dip2px(80))
						.placeholder(R.drawable.ic_default_avatar_l)
						.error(R.drawable.ic_default_avatar_l)
						.config(Bitmap.Config.RGB_565)
						.into(ivAvatar);
				final ArrayList<Image> temp = new ArrayList<>();
				temp.add(data.icon);
				ivAvatar.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context, ImageBigSelectActivity.class);
						intent.putExtra(ImageBigSelectActivity.EXTRA_KEY_FILEBEAN, temp);
						intent.putExtra(ImageBigSelectActivity.EXTRAS_KEY_FIRST_PHTOT_INDEX,
								0);
						// 以后可选择 有返回数据
						context.startActivity(intent);
					}
				});
			}
			tvName.setText(data.name);
			tvBirthday.setText(StringUtils.getNormalUsDate(data.birthday));
			tvToSchoolTime.setText(StringUtils.getNormalUsDate(data.register_time));
			tvLevelName.setText(data.level_name);
			tvLevelName.setTextColor(data.level_color);
			tvCoin.setText(data.coin+"");
			
			ivLevelColor.setBackgroundColor(data.level_color);
			tvLevel.setText(data.level + "");
			tvLevel.setTextColor(data.level_color);
			tvScore.setText(data.score + "/" + data.level_score_end);
			
			pbScore.setMax(data.level_score_end - data.level_score_start);
			pbScore.setProgress(data.score - data.level_score_start);
			
			if(PersonBean.SEX_FEMALE.equals(data.sex_str)){
				ivMale.setImageResource(R.drawable.ic_boy);
				ivMale.setVisibility(View.VISIBLE);
			}else if(PersonBean.SEX_MALE.equals(data.sex_str)){
				ivMale.setImageResource(R.drawable.ic_girl);
				ivMale.setVisibility(View.VISIBLE);
			}else{
				ivMale.setVisibility(View.GONE);
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.iv_level_name_details) {
			PersonalLevelActivity.startActivity(context, Otaku.LEVEL_DETAILS_URL);
		} else if (id == R.id.iv_passport_edit) {
			Intent intent = new Intent(context, EditAccountActivity.class);
			context.startActivity(intent);
		}
		
		
	}
	
}
