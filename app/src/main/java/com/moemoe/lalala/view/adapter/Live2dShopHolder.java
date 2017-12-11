package com.moemoe.lalala.view.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.Live2dShopEntity;
import com.moemoe.lalala.model.entity.MapUserImageEntity;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;
import com.moemoe.lalala.view.widget.view.KiraRatingBar;

import java.util.Locale;

/**
 * Created by yi on 2017/7/21.
 */

public class Live2dShopHolder extends ClickableViewHolder {

    public Live2dShopHolder(View itemView) {
        super(itemView);
    }

    public void createItem(final Live2dShopEntity entity){
        int size = (int) context.getResources().getDimension(R.dimen.y148);
        Glide.with(context)
                .load(StringUtils.getUrl(context,entity.getIcon(),size,size,false,true))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .into((ImageView) $(R.id.iv_cover));
        setText(R.id.tv_title,entity.getName());
        setText(R.id.tv_content,entity.getDesc());
        setText(R.id.tv_from,"by: " + entity.getDesign());
        if(entity.isVip() && !TextUtils.isEmpty(PreferenceUtils.getAuthorInfo().getVipTime())){
            $(R.id.tv_state).setSelected(true);
            setText(R.id.tv_state,"vip免费");
        }else {
            if(entity.isHave()){
                $(R.id.tv_state).setSelected(true);
                setText(R.id.tv_state,"已拥有");
            }else {
                $(R.id.tv_state).setSelected(false);
                setText(R.id.tv_state,entity.getCoin() + "节操");
            }
        }
        if(entity.getUses() == 0){
            setText(R.id.tv_score,"0分");
            ((KiraRatingBar)$(R.id.kira_bar)).setRating(0);
        }else {
            float score = (float) entity.getScore() / entity.getUses();
            ((KiraRatingBar)$(R.id.kira_bar)).setRating(score);
            setText(R.id.tv_score,String.format(Locale.getDefault(),"%.1f分",score));
        }
    }
}
