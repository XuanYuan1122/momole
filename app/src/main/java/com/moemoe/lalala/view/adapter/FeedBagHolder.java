package com.moemoe.lalala.view.adapter;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
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

/**
 *
 * Created by yi on 2017/7/21.
 */

public class FeedBagHolder extends ClickableViewHolder {

    public FeedBagHolder(View itemView) {
        super(itemView);
    }

    public void createItem(final ShowFolderEntity entity, final int position){

        int coverW = (DensityUtil.getScreenWidth(context) - context.getResources().getDimensionPixelSize(R.dimen.x72)) / 2;
        int coverH = getResources().getDimensionPixelSize(R.dimen.y250);
        Glide.with(context)
                .load(StringUtils.getUrl(context,entity.getCover(),coverW,coverH,false,true))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .bitmapTransform(new CropTransformation(context,coverW,coverH)
                        ,new RoundedCornersTransformation(context,getResources().getDimensionPixelSize(R.dimen.y8),0))
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
        }else if(entity.getType().equals(FolderType.WZ.toString())){
            setText(R.id.tv_mark,"文章");
            $(R.id.tv_mark).setBackgroundResource(R.drawable.shape_rect_zonghe);
        }else if(entity.getType().equals(FolderType.SP.toString())){
            setText(R.id.tv_mark,"视频");
            $(R.id.tv_mark).setBackgroundResource(R.drawable.shape_rect_shipin);
        }else if(entity.getType().equals(FolderType.YY.toString())){
            setText(R.id.tv_mark,"音乐");
            $(R.id.tv_mark).setBackgroundResource(R.drawable.shape_rect_yinyue);
        }else if("MOVIE".equals(entity.getType())){
            setText(R.id.tv_mark,"视频");
            $(R.id.tv_mark).setBackgroundResource(R.drawable.shape_black_time);
        }else if("MUSIC".equals(entity.getType())){
            setText(R.id.tv_mark,"音乐");
            $(R.id.tv_mark).setBackgroundResource(R.drawable.shape_black_time);
        }

        int avatarSize = getResources().getDimensionPixelSize(R.dimen.y32);
        Glide.with(context)
                .load(StringUtils.getUrl(context,entity.getUserIcon(),avatarSize,avatarSize,false,true))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(context))
                .into((ImageView) $(R.id.iv_user_avatar));

        setText(R.id.tv_user_name,entity.getCreateUserName());
        setText(R.id.tv_title,entity.getFolderName());
        $(R.id.ll_user_root).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ViewUtils.toPersonal(context,entity.getCreateUser());
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
            ((TextView)$(tagIds[i])).setText(entity.getTextsV2().get(i).getText());
            $(tagIds[i]).setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    //TODO 跳转标签页
                }
            });
        }

        TextView tvPlay = $(R.id.tv_play_num);
        if(entity.getType().equals(FolderType.SP.toString()) || entity.getType().equals(FolderType.YY.toString())){
            if(entity.getCoin() == 0){
                setText(R.id.tv_coin,"免费");
            }else {
                setText(R.id.tv_coin,entity.getCoin() + "节操");
            }
            tvPlay.setText(String.valueOf(entity.getPlayNum()));
            if(entity.getType().equals(FolderType.SP.toString())){
                setVisible(R.id.tv_danmu_num,true);
                setText(R.id.tv_danmu_num,String.valueOf(entity.getBarrageNum()));
                tvPlay.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.ic_baglist_video_playtimes_white),null,null,null);
            }else {
                tvPlay.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.ic_baglist_music_times_white),null,null,null);
                setVisible(R.id.tv_danmu_num,false);
            }
        }else {
            setVisible(R.id.tv_danmu_num,false);
            setText(R.id.tv_coin,StringUtils.timeFormat(entity.getTime()));
            tvPlay.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
            tvPlay.setCompoundDrawablePadding(0);

            String str = "";
            if(entity.getCoin() > 0){
                str = entity.getCoin() + "节操 · ";
            }else {
                str = "免费 · ";
            }
            str += entity.getItems() + "项";
            tvPlay.setText(str);
        }
    }
}
