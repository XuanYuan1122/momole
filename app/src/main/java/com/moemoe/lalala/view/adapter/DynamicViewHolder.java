package com.moemoe.lalala.view.adapter;

import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.DynamicEntity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by yi on 2017/7/21.
 */

public class DynamicViewHolder extends ClickableViewHolder {

    RelativeLayout topRoot;
    ImageView ivBg;
    TextView tvMark;
    TextView tvName;

    public DynamicViewHolder(View itemView) {
        super(itemView);
        ivBg = $(R.id.iv_bg);
        tvMark = $(R.id.tv_mark);
        tvName = $(R.id.tv_create_name);
        topRoot = $(R.id.rl_root);
    }

    public void createItem(final DynamicEntity entity){
        int width = (DensityUtil.getScreenWidth(itemView.getContext()) - (int)context.getResources().getDimension(R.dimen.x56))/2;
        topRoot.setLayoutParams(new LinearLayout.LayoutParams(width,width));
        ivBg.setLayoutParams(new RelativeLayout.LayoutParams(width,width));
        Glide.with(itemView.getContext())
                .load(StringUtils.getUrl(itemView.getContext(),entity.getCover(),width,width,false,true))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new CropSquareTransformation(itemView.getContext()),new RoundedCornersTransformation(itemView.getContext(),(int)context.getResources().getDimension(R.dimen.y8),0))
                .into(ivBg);
        tvMark.setVisibility(View.VISIBLE);

        if(entity.getFolderType().equals(FolderType.ZH.toString())){
            tvMark.setText("综合");
            tvMark.setBackgroundResource(R.drawable.shape_rect_zonghe);
        }else if(entity.getFolderType().equals(FolderType.TJ.toString())){
            tvMark.setText("图集");
            tvMark.setBackgroundResource(R.drawable.shape_rect_tuji);
        }else if(entity.getFolderType().equals(FolderType.MH.toString())){
            tvMark.setText("漫画");
            tvMark.setBackgroundResource(R.drawable.shape_rect_manhua);
        }else if(entity.getFolderType().equals(FolderType.XS.toString())){
            tvMark.setText("小说");
            tvMark.setBackgroundResource(R.drawable.shape_rect_xiaoshuo);
        }else {
            tvMark.setVisibility(View.GONE);
        }
        setText(R.id.tv_name,entity.getFolderName());
        String content = entity.getExtra();
        String colorStr = entity.getExtraColorContent();
        if(!TextUtils.isEmpty(colorStr)){
            ForegroundColorSpan span = new ForegroundColorSpan(ContextCompat.getColor(itemView.getContext(),R.color.main_red));
            SpannableStringBuilder style = new SpannableStringBuilder(content);
            style.setSpan(span, content.indexOf(colorStr), content.indexOf(colorStr) + colorStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(R.id.tv_extra, style);
            ((TextView)$(R.id.tv_extra)).setMovementMethod(LinkMovementMethod.getInstance());
        }else {
            setText(R.id.tv_extra, content);
            ((TextView)$(R.id.tv_extra)).setMovementMethod(null);
        }
        tvName.setText(entity.getCreateUserName());
        tvName.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ViewUtils.toPersonal(itemView.getContext(),entity.getCreateUser());
            }
        });

        setText(R.id.tv_time,StringUtils.timeFormat(entity.getCreateTime()));
    }
}
