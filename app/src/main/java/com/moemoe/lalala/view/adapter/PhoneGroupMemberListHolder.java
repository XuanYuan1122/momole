package com.moemoe.lalala.view.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.PhoneMenuEntity;
import com.moemoe.lalala.model.entity.UserTopEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 *
 * Created by yi on 2017/7/21.
 */

public class PhoneGroupMemberListHolder extends ClickableViewHolder {


    public PhoneGroupMemberListHolder(View itemView) {
        super(itemView);
    }

    public void createItem(final UserTopEntity entity,boolean isCheck,String id){
        Glide.with(itemView.getContext())
                .load(StringUtils.getUrl(itemView.getContext(),entity.getHeadPath(), (int)itemView.getContext().getResources().getDimension(R.dimen.x64),(int)itemView.getContext().getResources().getDimension(R.dimen.y64),false,true))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .bitmapTransform(new CropSquareTransformation(itemView.getContext()),new RoundedCornersTransformation(itemView.getContext(),(int)context.getResources().getDimension(R.dimen.y8),0))
                .into((ImageView) $(R.id.iv_avatar));

        setText(R.id.tv_name,entity.getUserName());

        if("N".equalsIgnoreCase(entity.getSex())){
            setVisible(R.id.iv_sex,false);
        }else {
            setVisible(R.id.iv_sex,true);
        }
        setImageResource(R.id.iv_sex,"M".equalsIgnoreCase(entity.getSex())?R.drawable.ic_user_girl:R.drawable.ic_user_boy);
        if(isCheck){
            if(id.equals(entity.getUserId())){
                setVisible(R.id.cb_select,false);
            }else {
                setVisible(R.id.cb_select,true);
                ((CheckBox)$(R.id.cb_select)).setChecked(entity.isCheck());
            }
        }else {
            setVisible(R.id.cb_select,false);
        }
        if(id.equals(entity.getUserId())){
            setVisible(R.id.tv_extra,true);
            setText(R.id.tv_extra,"管理员");
        }else {
            setVisible(R.id.tv_extra,true);
            setText(R.id.tv_extra,"");
        }
       // itemView.setTag(this);
    }

//    public void setCheck(boolean check){
//        ((CheckBox)$(R.id.cb_select)).setChecked(check);
//    }
}
