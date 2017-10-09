package com.moemoe.lalala.view.adapter;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.JuQingEntity;
import com.moemoe.lalala.model.entity.PhoneMenuEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 *
 * Created by yi on 2017/7/21.
 */

public class PhoneJuQingListHolder extends ClickableViewHolder {


    public PhoneJuQingListHolder(View itemView) {
        super(itemView);
    }

    public void createItem(final JuQingEntity entity){
        int w = (int) (DensityUtil.getScreenWidth(context) - context.getResources().getDimension(R.dimen.x48));
        int h = (int) context.getResources().getDimension(R.dimen.y210);
        Glide.with(context)
                .load(StringUtils.getUrl(context,entity.getBg(),w,h,false,true))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .bitmapTransform(new CropTransformation(context,w,h),new RoundedCornersTransformation(context, (int) context.getResources().getDimension(R.dimen.y8),0))
                .into((ImageView) $(R.id.iv_bg));
        setText(R.id.tv_title,entity.getGroupName());
        setText(R.id.tv_tag,"by "+entity.getBy());
        if(entity.getItems() > 0){
            setText(R.id.tv_juqing_progress,"进度: " + entity.getProgress()/entity.getItems() + "%");
        }else {
            setText(R.id.tv_juqing_progress,"进度: " + "0%");
        }
        setText(R.id.tv_user_progress,"参与人次: " + entity.getJoinNum());
        setText(R.id.tv_content,entity.getContent());
        if(entity.getStatus() == 1){
            setText(R.id.tv_state,"攻略中");
            ((TextView)$(R.id.tv_state)).setTextColor(ContextCompat.getColor(context,R.color.green_6fc93a));
            ((TextView)$(R.id.tv_juqing_progress)).setTextColor(ContextCompat.getColor(context,R.color.green_6fc93a));
            setVisible(R.id.iv_front,false);
        }
        if(entity.getStatus() == 2){
            setText(R.id.tv_state,"完成");
            ((TextView)$(R.id.tv_state)).setTextColor(ContextCompat.getColor(context,R.color.white));
            ((TextView)$(R.id.tv_juqing_progress)).setTextColor(ContextCompat.getColor(context,R.color.white));
            setVisible(R.id.iv_front,true);
        }
        if(entity.getStatus() == 3){
            setText(R.id.tv_state,"未解锁");
            ((TextView)$(R.id.tv_state)).setTextColor(ContextCompat.getColor(context,R.color.white));
            ((TextView)$(R.id.tv_juqing_progress)).setTextColor(ContextCompat.getColor(context,R.color.white));
            setVisible(R.id.iv_front,false);
        }
    }
}
