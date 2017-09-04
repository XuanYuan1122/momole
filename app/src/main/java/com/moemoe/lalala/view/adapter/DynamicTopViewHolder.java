package com.moemoe.lalala.view.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.BagMyEntity;
import com.moemoe.lalala.model.entity.BagMyShowEntity;
import com.moemoe.lalala.model.entity.DynamicTopEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.FolderUrlSpan;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by yi on 2017/7/21.
 */

public class DynamicTopViewHolder extends ClickableViewHolder {

    ImageView ivAvater;
    TextView tvName;

    public DynamicTopViewHolder(View itemView) {
        super(itemView);
        ivAvater = $(R.id.iv_avatar);
        tvName = $(R.id.tv_name);
    }

    public void createItem(final DynamicTopEntity entity){
        Glide.with(itemView.getContext())
                .load(StringUtils.getUrl(itemView.getContext(),entity.getUserIcon().getPath(),DensityUtil.dip2px(itemView.getContext(),18),DensityUtil.dip2px(itemView.getContext(),18),false,true))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(itemView.getContext()))
                .into(ivAvater);
        tvName.setText(entity.getUserName());
        ivAvater.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ViewUtils.toPersonal(itemView.getContext(),entity.getUserId());
            }
        });
        tvName.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ViewUtils.toPersonal(itemView.getContext(),entity.getUserId());
            }
        });
        String content = entity.getExtra();
        String colorStr = entity.getExtraColorContent();
        String colorStr2 = "";
        if(entity.getCoin() > 0){
            content += "，花费了" + entity.getCoin() + "节操";
            colorStr2 = entity.getCoin() + "节操";
        }
        if(!TextUtils.isEmpty(colorStr)){
            FolderUrlSpan span = new FolderUrlSpan(itemView.getContext(),entity.getUserId(),entity.getExtraTargetId(),entity.getFolderType());
            SpannableStringBuilder style = new SpannableStringBuilder(content);
            style.setSpan(span, content.indexOf(colorStr), content.indexOf(colorStr) + colorStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if(!TextUtils.isEmpty(colorStr2)){
                ForegroundColorSpan span2 = new ForegroundColorSpan(ContextCompat.getColor(itemView.getContext(),R.color.pink_fb7ba2));
                style.setSpan(span2, content.indexOf(colorStr2), content.indexOf(colorStr2) + colorStr2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            setText(R.id.tv_extra, style);
            ((TextView)$(R.id.tv_extra)).setMovementMethod(LinkMovementMethod.getInstance());
        }else {
            setText(R.id.tv_extra, content);
            ((TextView)$(R.id.tv_extra)).setMovementMethod(null);
        }
    }
}
