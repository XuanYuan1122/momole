package com.moemoe.lalala.view.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.FileXiaoShuoEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;

/**
 * Created by yi on 2017/8/20.
 */

public class FileXiaoShuoViewHolder extends ClickableViewHolder {

    ImageView cover;
    TextView title;
    TextView name;
    TextView num;
    TextView content;
    ImageView select;

    public FileXiaoShuoViewHolder(View itemView) {
        super(itemView);
        cover = $(R.id.iv_cover);
        title = $(R.id.tv_title);
        name = $(R.id.tv_user_name);
        num = $(R.id.tv_num);
        content = $(R.id.tv_content);
        select = $(R.id.iv_select);
    }

    public void createItem(FileXiaoShuoEntity entity, boolean isSelect,boolean isBuy){
        select.setVisibility(isSelect?View.VISIBLE:View.GONE);
        select.setSelected(entity.isSelect());
        if(!isBuy){
            Glide.with(itemView.getContext())
                    .load(StringUtils.getUrl(itemView.getContext(),entity.getCover(),DensityUtil.dip2px(itemView.getContext(),56),DensityUtil.dip2px(itemView.getContext(),74),false,true))
                    .error(R.drawable.bg_default_square)
                    .placeholder(R.drawable.bg_default_square)
                    .bitmapTransform(new CropTransformation(itemView.getContext(),DensityUtil.dip2px(itemView.getContext(),56),DensityUtil.dip2px(itemView.getContext(),74)))
                    .into(cover);
        }else {
            Glide.with(itemView.getContext())
                    .load(StringUtils.getUrl(itemView.getContext(),entity.getCover(),DensityUtil.dip2px(itemView.getContext(),56),DensityUtil.dip2px(itemView.getContext(),74),false,true))
                    .error(R.drawable.bg_default_square)
                    .placeholder(R.drawable.bg_default_square)
                    .bitmapTransform(new CropTransformation(itemView.getContext(),DensityUtil.dip2px(itemView.getContext(),56),DensityUtil.dip2px(itemView.getContext(),74)),new BlurTransformation(context,10,4))
                    .into(cover);
        }
        title.setText(entity.getTitle());
        name.setText("UP " + entity.getUserName());
        num.setText("字数 " + entity.getNum());
        content.setText(entity.getContent());
    }
}
