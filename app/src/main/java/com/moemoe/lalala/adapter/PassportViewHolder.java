package com.moemoe.lalala.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moemoe.lalala.EditAccountActivity;
import com.moemoe.lalala.ImageBigSelectActivity;
import com.moemoe.lalala.PersonalLevelActivity;
import com.moemoe.lalala.R;
import com.moemoe.lalala.data.AuthorInfo;
import com.moemoe.lalala.data.Image;
import com.moemoe.lalala.network.Otaku;
import com.moemoe.lalala.utils.PreferenceManager;
import com.moemoe.lalala.utils.StringUtils;
import com.squareup.picasso.Picasso;

import org.xutils.common.util.DensityUtil;

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

	public static final int TYPE_SELF = 0;
	public static final int TYPE_FRIEND = 1;
	public static final int RES_EDIT = 4000;

	private Context context;
	private ImageView ivAvatar;
	private ImageView ivMale;
	private View ivLevelColor;
	private TextView tvLevel;
	private TextView tvScore;
	private TextView tvCoin;
	private ProgressBar pbScore;
	private TextView tvName;
	private TextView tvLevelName;
	private TextView tvToSchoolTime;
	private TextView tvBirthday;
	private View tvBirthdayLabel;
	private View ivEdit;
	//------------------- fields ---------------------------
	public AuthorInfo data;


	
	
	public PassportViewHolder(Context context, View root,int type) {
		this.context = context;
		if (root != null) {
			ivAvatar = (ImageView)root.findViewById(R.id.iv_avatar);
			ivMale = (ImageView)root.findViewById(R.id.iv_passport_xingbie);
			
			ivLevelColor = root.findViewById(R.id.iv_level_bg);
			tvLevel = (TextView)root.findViewById(R.id.tv_level);
			tvScore = (TextView)root.findViewById(R.id.tv_curr_score);
			pbScore = (ProgressBar) root.findViewById(R.id.pb_curr_score);
			
			
			tvToSchoolTime = (TextView)root.findViewById(R.id.tv_content_in_school_time);
			tvName = (TextView)root.findViewById(R.id.tv_content_name);
			tvLevelName = (TextView) root.findViewById(R.id.tv_content_level_name);
			View ivLevelDetail = root.findViewById(R.id.iv_level_name_details);
			tvBirthday = (TextView)root.findViewById(R.id.tv_content_birthday);
			tvBirthdayLabel = root.findViewById(R.id.tv_label_birthday);
			ivEdit = root.findViewById(R.id.iv_passport_edit);
			tvCoin = (TextView) root.findViewById(R.id.tv_content_jiecao);
			
			ivEdit.setOnClickListener(this);
			ivLevelDetail.setOnClickListener(this);
			if(type == TYPE_SELF){
				ivLevelDetail.setVisibility(View.VISIBLE);
			}else{
				ivLevelDetail.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	public void setPersonBean(AuthorInfo bean) {
		data = bean;
		loadView();
	}
	
	
	private void loadView() {
		if(data != null){
			AuthorInfo info = PreferenceManager.getInstance(context).getAuthorInfo();
			boolean isMyself = TextUtils.equals(data.getUserId(), info.getUserId());
			
			if (isMyself) {
				tvBirthday.setVisibility(View.VISIBLE);
				tvBirthdayLabel.setVisibility(View.VISIBLE);
				ivEdit.setVisibility(View.VISIBLE);
				
			} else {
				tvBirthday.setVisibility(View.GONE);
				tvBirthdayLabel.setVisibility(View.GONE);
				ivEdit.setVisibility(View.GONE);
				
			}
			
			if(data.getHeadPath() != null){
				Picasso.with(context)
						.load(data.getHeadPath())
						.resize(DensityUtil.dip2px(80),DensityUtil.dip2px(80))
						.placeholder(R.drawable.ic_default_avatar_l)
						.error(R.drawable.ic_default_avatar_l)
						.config(Bitmap.Config.RGB_565)
						.into(ivAvatar);
				final ArrayList<Image> temp = new ArrayList<>();
				Image image = new Image();
				String str = data.getHeadPath().replace(Otaku.URL_QINIU,"");
				image.setPath(str);
				temp.add(image);
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
			tvName.setText(data.getUserName());
			tvBirthday.setText(data.getBirthday());
			tvToSchoolTime.setText(data.getRegisterTime());
			tvLevelName.setText(data.getLevelName());
			tvLevelName.setTextColor(StringUtils.readColorStr(data.getLevelColor(), ContextCompat.getColor(context,R.color.main_title_cyan)));
			tvCoin.setText(String.valueOf(data.getCoin()));
			
			ivLevelColor.setBackgroundColor(StringUtils.readColorStr(data.getLevelColor(),ContextCompat.getColor(context,R.color.main_title_cyan)));
			tvLevel.setText(String.valueOf(data.getLevel()));
			tvLevel.setTextColor(StringUtils.readColorStr(data.getLevelColor(),ContextCompat.getColor(context,R.color.main_title_cyan)));
			tvScore.setText(data.getScore() + "/" + data.getLevelScoreEnd());
			
			pbScore.setMax(data.getLevelScoreEnd() - data.getLevelScoreStart());
			pbScore.setProgress(data.getScore() - data.getLevelScoreStart());
			
			if(AuthorInfo.SEX_FEMALE.equals(data.getSex())){
				ivMale.setImageResource(R.drawable.ic_boy);
				ivMale.setVisibility(View.VISIBLE);
			}else if(AuthorInfo.SEX_MALE.equals(data.getSex())){
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
			((Activity)context).startActivityForResult(intent,RES_EDIT);
		}
		
		
	}
	
}
