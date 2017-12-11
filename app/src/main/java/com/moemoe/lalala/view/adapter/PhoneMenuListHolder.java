package com.moemoe.lalala.view.adapter;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.PersonFollowEntity;
import com.moemoe.lalala.model.entity.PhoneMenuEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
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

public class PhoneMenuListHolder extends ClickableViewHolder {


    public PhoneMenuListHolder(View itemView) {
        super(itemView);
    }

    public void createItem(final PhoneMenuEntity entity){

        Glide.with(itemView.getContext())
                .load(StringUtils.getUrl(itemView.getContext(),entity.getUserIcon(), (int)itemView.getContext().getResources().getDimension(R.dimen.x64),(int)itemView.getContext().getResources().getDimension(R.dimen.y64),false,true))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .bitmapTransform(new CropSquareTransformation(itemView.getContext()),new RoundedCornersTransformation(itemView.getContext(),(int)context.getResources().getDimension(R.dimen.y8),0))
                .into((ImageView) $(R.id.iv_avatar));

        setText(R.id.tv_name,entity.getUserName());

        if(entity.getUserSex().equalsIgnoreCase("N")){
            setVisible(R.id.iv_sex,false);
        }else {
            setVisible(R.id.iv_sex,true);
        }
        setImageResource(R.id.iv_sex,entity.getUserSex().equalsIgnoreCase("M")?R.drawable.ic_user_girl:R.drawable.ic_user_boy);
    }
}
