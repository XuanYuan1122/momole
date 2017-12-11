package com.moemoe.lalala.view.adapter;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.StickEntity;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

/**
 *
 * Created by yi on 2017/7/21.
 */

public class StickHolder extends ClickableViewHolder {

    public StickHolder(View itemView) {
        super(itemView);
    }

    public void createItem(final StickEntity.Stick entity, boolean isSelect){
        if(isSelect){
            $(R.id.rl_root).setBackgroundResource(R.drawable.shape_rect_border_main_no_background_y8);
        }else {
            $(R.id.rl_root).setBackgroundResource(R.color.transparent);
        }
        int w = (int) context.getResources().getDimension(R.dimen.x120);
        int h = (int) context.getResources().getDimension(R.dimen.y160);
        Glide.with(context)
                .load(StringUtils.getUrl(context,entity.getSmallPath(),w,h,false,true))
                .into((ImageView) $(R.id.iv_stick_cover));
        if(entity.isBelong()){
            $(R.id.iv_stick_cover).setAlpha(1.0f);
        }else {
            $(R.id.iv_stick_cover).setAlpha(0.5f);
            if("VIP".equals(entity.getType())){
                setText(R.id.tv_extra,"VIP限定");
            }else if("JC".equals(entity.getType())){
                setText(R.id.tv_extra,entity.getCoin() + "节操");
            }else if("CY".equals(entity.getType())){
                setText(R.id.tv_extra,entity.getCoin() + "次元币");
            }else {
                setText(R.id.tv_extra,"");
            }
        }
    }
}
