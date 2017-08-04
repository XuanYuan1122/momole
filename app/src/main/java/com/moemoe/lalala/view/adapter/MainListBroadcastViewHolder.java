package com.moemoe.lalala.view.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.NewDocListEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by yi on 2017/7/28.
 */

public class MainListBroadcastViewHolder extends ClickableViewHolder {

    private Context context;

    public MainListBroadcastViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
    }

    public void createDoc(NewDocListEntity item){
        final NewDocListEntity.FollowDepartment doc = (NewDocListEntity.FollowDepartment) item.getDetail().getTrueData();
        if(!TextUtils.isEmpty(doc.getDocFrom())){
            setVisible(R.id.rl_from_top, true);
            setText(R.id.tv_from_name,doc.getDocFrom());
            if(!TextUtils.isEmpty(doc.getFromSchema())){
                $(R.id.tv_from_name).setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        //TODO 跳转schema
                    }
                });
            }else {
                $(R.id.tv_from_name).setOnClickListener(null);
            }
        }else {
            setVisible(R.id.rl_from_top, false);
        }

        Glide.with(context)
                .load( StringUtils.getUrl(context,doc.getDocIcon().getPath(), DensityUtil.getScreenWidth(context), DensityUtil.dip2px(context,250), false, true))
                .override(DensityUtil.getScreenWidth(context), DensityUtil.dip2px(context,250))
                .centerCrop()
                .placeholder(R.drawable.bg_default_square)
                .error(R.drawable.bg_default_square)
                .into((ImageView) $(R.id.iv_icon));
        setText(R.id.tv_title,doc.getTitle());
        Glide.with(context)
                .load( StringUtils.getUrl(context, doc.getUserIcon().getPath(), DensityUtil.dip2px(context,20), DensityUtil.dip2px(context,20), false, true))
                .override(DensityUtil.dip2px(context,20), DensityUtil.dip2px(context,20))
                .placeholder(R.drawable.bg_default_circle)
                .error(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(context))
                .into((ImageView) $(R.id.iv_avatar));
        setText(R.id.tv_user_name,doc.getUserName());
        setText(R.id.tv_update_time,StringUtils.timeFormate(item.getCreateTime()));

        setText(R.id.tv_tag_num, StringUtils.getNumberInLengthLimit(doc.getLikes(), 3));
        setText(R.id.tv_comment_num, StringUtils.getNumberInLengthLimit(doc.getComments(), 3));
        $(R.id.fl_add_tag_root).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                //TODO 打标签
            }
        });
        $(R.id.fl_show_comment_root).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                //TODO 进入帖子详情回复界面
            }
        });
        $(R.id.fl_menu_root).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                //TODO 显示菜单
            }
        });
    }
}
