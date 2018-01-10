package com.moemoe.lalala.view.adapter;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.HongBaoEntity;
import com.moemoe.lalala.model.entity.InviteUserEntity;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 *
 * Created by yi on 2017/7/21.
 */

public class HongbaoListHolder extends ClickableViewHolder {

    public HongbaoListHolder(View itemView) {
        super(itemView);
    }

    public void createItem(final HongBaoEntity entity, final int position){
        int size = (int) context.getResources().getDimension(R.dimen.y90);
        Glide.with(context)
                .load(StringUtils.getUrl(context,entity.getIcon(),size,size,false,true))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(context))
                .into((ImageView) $(R.id.iv_avatar));
        setText(R.id.tv_user_name,entity.getUserName());
        setText(R.id.tv_get_time,entity.getCreateTime());
        setText(R.id.tv_get_coin,String.format(context.getString(R.string.label_get_coin),entity.getCoin()));

    }
}
