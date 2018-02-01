package com.moemoe.lalala.view.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.FeedFollowType1Entity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.model.entity.UserFollowTagEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.TagUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import org.json.JSONObject;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 *
 * Created by yi on 2017/7/21.
 */

public class FeedShowAllHolder extends ClickableViewHolder {

    public FeedShowAllHolder(View itemView) {
        super(itemView);
    }

    public void createItem(final Object entity, final int position,boolean showSelect){

        setVisible(R.id.iv_select,showSelect);


        final String coverPath,avatarPath,userName,userId,title;
        int coverW,coverH,avatarSize;
        ArrayList<UserFollowTagEntity> tags;

        if(entity instanceof ShowFolderEntity){
            ShowFolderEntity item = (ShowFolderEntity) entity;
            $(R.id.iv_select).setSelected(item.isSelect());
            TextView tvMark = $(R.id.tv_mark);
            if("MOVIE".equals(item.getType())){
                coverW = (DensityUtil.getScreenWidth(context) - context.getResources().getDimensionPixelSize(R.dimen.x72)) / 2;
                coverH = context.getResources().getDimensionPixelSize(R.dimen.y250);
                tvMark.setText("视频");
                tvMark.setBackgroundResource(R.drawable.shape_black_time);

                setText(R.id.tv_play_num,String.valueOf(item.getPlayNum()));
                setText(R.id.tv_danmu_num,String.valueOf(item.getBarrageNum()));
            }else {
                coverW = (DensityUtil.getScreenWidth(context) - getResources().getDimensionPixelSize(R.dimen.x84)) / 3;
                coverH = getResources().getDimensionPixelSize(R.dimen.y290);
                if(item.getType().equals("ZH")){
                    tvMark.setText("综合");
                    tvMark.setBackgroundResource(R.drawable.shape_rect_zonghe);
                }else if(item.getType().equals("TJ")){
                    tvMark.setText("图集");
                    tvMark.setBackgroundResource(R.drawable.shape_rect_tuji);
                }else if(item.getType().equals("MH")){
                    tvMark.setText("漫画");
                    tvMark.setBackgroundResource(R.drawable.shape_rect_manhua);
                }else if(item.getType().equals("XS")){
                    tvMark.setText("小说");
                    tvMark.setBackgroundResource(R.drawable.shape_rect_xiaoshuo);
                }else if(item.getType().equals("WZ")){
                    tvMark.setText("文章");
                    tvMark.setBackgroundResource(R.drawable.shape_rect_zonghe);
                }else {
                    tvMark.setVisibility(View.GONE);
                }

                setText(R.id.tv_bag_num,item.getItems() + "项");
            }
            coverPath = item.getCover();
            avatarSize = getResources().getDimensionPixelSize(R.dimen.y32);
            avatarPath = item.getUserIcon();
            userName = item.getCreateUserName();
            userId = item.getCreateUser();
            title = item.getFolderName();
            tags = item.getTextsV2();

            if(item.getCoin() == 0){
                setText(R.id.tv_coin,"免费");
            }else {
                setText(R.id.tv_coin,item.getCoin() + "节操");
            }
        }else{ //FeedFollowType1Entity
            FeedFollowType1Entity item = (FeedFollowType1Entity) entity;
            $(R.id.iv_select).setSelected(item.isSelect());
            setVisible(R.id.view_step,false);
            itemView.setPadding(0,0,0,0);

            if("DOC".equals(item.getType())){
                coverW = coverH = getResources().getDimensionPixelSize(R.dimen.y180);
            }else {
                coverW = getResources().getDimensionPixelSize(R.dimen.x222);
                coverH = getResources().getDimensionPixelSize(R.dimen.y190);
            }
            coverPath = item.getCover();
            avatarSize = getResources().getDimensionPixelSize(R.dimen.y32);
            avatarPath = item.getUserAvatar();
            userName = item.getUserName();
            userId = item.getUserId();
            title = item.getTitle();
            tags = item.getTags();

            try {
                //extra content
                JSONObject json = new JSONObject(item.getExtra());
                if("DOC".equals(item.getType())){
                    int readNum = json.getInt("readNum");
                    setText(R.id.tv_extra_content,"阅读 " + readNum + " · " + StringUtils.timeFormat(item.getCreateTime()));
                }else {
                    setText(R.id.tv_extra_content,StringUtils.timeFormat(item.getCreateTime()));
                }

                //special
                if("MUSIC".equals(item.getType())){
                    int playNum = json.getInt("playNum");
                    int stampTime = json.getInt("stampTime");
                    int coin = json.getInt("coin");
                    TextView tvPlay = $(R.id.tv_play_num);
                    tvPlay.setText(String.valueOf(playNum));
                    tvPlay.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.ic_baglist_music_times),null,null,null);

                    setVisible(R.id.tv_danmu_num,false);

                    TextView tvCoin = $(R.id.tv_coin);
                    if(coin == 0){
                        tvCoin.setText("免费");
                    }else {
                        tvCoin.setText(coin + "节操");
                    }

                    setText(R.id.tv_extra,StringUtils.getMinute(stampTime));

                    ImageView ivPlay = $(R.id.iv_play);
                    ivPlay.setVisibility(View.VISIBLE);
                    ivPlay.setImageResource(R.drawable.ic_baglist_music_play);
                    setVisible(R.id.tv_mark,false);

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(coverH == coverW){
            Glide.with(context)
                    .load(StringUtils.getUrl(context,coverPath,coverW,coverH,false,true))
                    .error(R.drawable.bg_default_square)
                    .placeholder(R.drawable.bg_default_square)
                    .bitmapTransform(new CropSquareTransformation(context))
                    .into((ImageView) $(R.id.iv_cover));
        }else {
            Glide.with(context)
                    .load(StringUtils.getUrl(context,coverPath,coverW,coverH,false,true))
                    .error(R.drawable.bg_default_square)
                    .placeholder(R.drawable.bg_default_square)
                    .bitmapTransform(new CropTransformation(context,coverW,coverH),
                            new RoundedCornersTransformation(context,getResources().getDimensionPixelSize(R.dimen.y8),0))
                    .into((ImageView) $(R.id.iv_cover));
        }

        Glide.with(context)
                .load(StringUtils.getUrl(context,avatarPath,avatarSize,avatarSize,false,true))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(context))
                .into((ImageView) $(R.id.iv_user_avatar));

        setText(R.id.tv_user_name,userName);
        setText(R.id.tv_title,title);
        $(R.id.ll_user_root).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ViewUtils.toPersonal(context,userId);
            }
        });

        //tag
        int[] tagIds = {R.id.tv_tag_1,R.id.tv_tag_2};
        if(tags.size() > 1){
            setVisible(tagIds[0],true);
            setVisible(tagIds[1],true);
        }else if(tags.size() > 0){
            setVisible(tagIds[0],true);
            setVisible(tagIds[1],false);
        }else {
            $(tagIds[0]).setVisibility(View.INVISIBLE);
            $(tagIds[1]).setVisibility(View.INVISIBLE);
        }
        int size1 = tagIds.length > tags.size() ? tags.size() : tagIds.length;
        for (int i = 0;i < size1;i++){
            TagUtils.setBackGround(tags.get(i).getText(),$(tagIds[i]));
            $(tagIds[i]).setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    //TODO 跳转标签页
                }
            });
        }
    }
}
