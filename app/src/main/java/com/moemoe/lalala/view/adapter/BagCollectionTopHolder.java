package com.moemoe.lalala.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.TagUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.mob.MobSDK.getContext;

/**
 *
 * Created by yi on 2017/7/21.
 */

public class BagCollectionTopHolder extends ClickableViewHolder {

    ImageView select;
    View root;

    public BagCollectionTopHolder(View itemView) {
        super(itemView);
        select = $(R.id.iv_select);
        root = $(R.id.rl_root);
    }

    public void createItem(final ShowFolderEntity entity, final int position,boolean isSelect){
        select.setVisibility(isSelect?View.VISIBLE:View.GONE);
        select.setSelected(entity.isSelect());

        int w = (DensityUtil.getScreenWidth(context) - (int)getResources().getDimension(R.dimen.x84)) / 3;
        int h = (int)getResources().getDimension(R.dimen.y290);
        Glide.with(context)
                .load(StringUtils.getUrl(context,entity.getCover(),w,h, false, true))
                .placeholder(R.drawable.bg_default_square)
                .error(R.drawable.bg_default_square)
                .bitmapTransform(new CropTransformation(context,w,h),new RoundedCornersTransformation(context,(int)getResources().getDimension(R.dimen.y8),0))
                .into((ImageView) $(R.id.iv_cover));

        if(entity.getType().equals(FolderType.ZH.toString())){
            setText(R.id.tv_mark,"综合");
            $(R.id.tv_mark).setBackgroundResource(R.drawable.shape_rect_zonghe);
        }else if(entity.getType().equals(FolderType.TJ.toString())){
            setText(R.id.tv_mark,"图集");
            $(R.id.tv_mark).setBackgroundResource(R.drawable.shape_rect_tuji);
        }else if(entity.getType().equals(FolderType.MH.toString())){
            setText(R.id.tv_mark,"漫画");
            $(R.id.tv_mark).setBackgroundResource(R.drawable.shape_rect_manhua);
        }else if(entity.getType().equals(FolderType.XS.toString())){
            setText(R.id.tv_mark,"小说");
            $(R.id.tv_mark).setBackgroundResource(R.drawable.shape_rect_xiaoshuo);
        }else if(entity.getType().equals(FolderType.SP.toString())){
            setText(R.id.tv_mark,"视频集");
            $(R.id.tv_mark).setBackgroundResource(R.drawable.shape_rect_shipin);
        }else if(entity.getType().equals(FolderType.YY.toString())){
            setText(R.id.tv_mark,"音乐集");
            $(R.id.tv_mark).setBackgroundResource(R.drawable.shape_rect_yinyue);
        }

        if(entity.getCoin() == 0){
            setText(R.id.tv_coin,"免费");
        }else {
            setText(R.id.tv_coin,entity.getCoin() + "节操");
        }
        setText(R.id.tv_bag_num,entity.getItems() + "项");
        setText(R.id.tv_title,entity.getFolderName());

        int size = getResources().getDimensionPixelSize(R.dimen.y32);
        setVisible(R.id.iv_user_avatar,true);
        Glide.with(context)
                .load(StringUtils.getUrl(context,entity.getUserIcon(),size,size,false,true))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(context))
                .into((ImageView) $(R.id.iv_user_avatar));
        setVisible(R.id.tv_user_name,true);
        setText(R.id.tv_user_name,entity.getCreateUserName());
        $(R.id.ll_user_root).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ViewUtils.toPersonal(getContext(),entity.getCreateUser());
            }
        });

        //tag
        int[] tagIds = {R.id.tv_tag_1,R.id.tv_tag_2};
        if(entity.getTextsV2().size() > 1){
            setVisible(tagIds[0],true);
            setVisible(tagIds[1],true);
        }else if(entity.getTextsV2().size() > 0){
            setVisible(tagIds[0],true);
            setVisible(tagIds[1],false);
        }else {
            $(tagIds[0]).setVisibility(View.INVISIBLE);
            $(tagIds[1]).setVisibility(View.INVISIBLE);
        }
        int size1 = tagIds.length > entity.getTextsV2().size() ? entity.getTextsV2().size() : tagIds.length;
        for (int i = 0;i < size1;i++){
            TagUtils.setBackGround(entity.getTextsV2().get(i).getText(),$(tagIds[i]));
            $(tagIds[i]).setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    //TODO 跳转标签页
                }
            });
        }
    }
}
