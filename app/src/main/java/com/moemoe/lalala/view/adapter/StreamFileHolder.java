package com.moemoe.lalala.view.adapter;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.StreamFileEntity;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.TagUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;

/**
 *
 * Created by yi on 2017/7/21.
 */

public class StreamFileHolder extends ClickableViewHolder {

    public StreamFileHolder(View itemView) {
        super(itemView);
    }

    public void createItem(final StreamFileEntity entity,int position, boolean isSelect){
        setVisible(R.id.iv_select,isSelect);
        $(R.id.iv_select).setSelected(entity.isSelect());

        int coverW = (DensityUtil.getScreenWidth(context) - (int)context.getResources().getDimension(R.dimen.x72)) / 2;
        int coverH = context.getResources().getDimensionPixelSize(R.dimen.y250);

        Glide.with(context)
                .load(StringUtils.getUrl(context,entity.getCover(),coverW,coverH,false,true))
                .error(R.drawable.bg_default_square)
                .placeholder(R.drawable.bg_default_square)
                .bitmapTransform(new CropTransformation(context,coverW,coverH))
                .into((ImageView) $(R.id.iv_cover));

        if("movie".equals(entity.getType())){
            if(entity.getState() == 0){
                setVisible(R.id.tv_state,true);
            }else {
                setVisible(R.id.tv_state,false);
            }
            setText(R.id.tv_mark,"视频");
            $(R.id.tv_mark).setBackgroundResource(R.drawable.shape_rect_shipin);
            TextView tvPlay = $(R.id.tv_play_num);
            tvPlay.setText(String.valueOf(entity.getPlayNum()));
            setVisible(R.id.tv_danmu_num,true);
            setText(R.id.tv_danmu_num,String.valueOf(entity.getBarrageNum()));
            tvPlay.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.ic_baglist_video_playtimes_white),null,null,null);
        }else {
            setText(R.id.tv_extra_content,StringUtils.timeFormat(entity.getUpdateTime()));
            setText(R.id.tv_extra,entity.getTimestamp());
            setImageResource(R.id.iv_play,R.drawable.ic_baglist_music_play);
            TextView tvPlay = $(R.id.tv_play_num);
            tvPlay.setText(String.valueOf(entity.getPlayNum()));
            setVisible(R.id.tv_danmu_num,false);
            tvPlay.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.ic_baglist_music_times),null,null,null);
        }

        int avatarSize = getResources().getDimensionPixelSize(R.dimen.y32);
        Glide.with(context)
                .load(StringUtils.getUrl(context,entity.getIcon(),avatarSize,avatarSize,false,true))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(context))
                .into((ImageView) $(R.id.iv_user_avatar));

        setText(R.id.tv_user_name,entity.getUserName());
        setText(R.id.tv_title,entity.getFileName());
        $(R.id.ll_user_root).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ViewUtils.toPersonal(context,entity.getUserId());
            }
        });

        //tag
        int[] tagIds = {R.id.tv_tag_1,R.id.tv_tag_2};
        if(entity.getTexts().size() > 1){
            setVisible(tagIds[0],true);
            setVisible(tagIds[1],true);
        }else if(entity.getTexts().size() > 0){
            setVisible(tagIds[0],true);
            setVisible(tagIds[1],false);
        }else {
            $(tagIds[0]).setVisibility(View.INVISIBLE);
            $(tagIds[1]).setVisibility(View.INVISIBLE);
        }
        int size1 = tagIds.length > entity.getTexts().size() ? entity.getTexts().size() : tagIds.length;
        for (int i = 0;i < size1;i++){
            TagUtils.setBackGround(entity.getTexts().get(i).getText(),$(tagIds[i]));
            $(tagIds[i]).setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    //TODO 跳转标签页
                }
            });
        }

        if(entity.getCoin() == 0){
            setText(R.id.tv_coin,"免费");
        }else {
            setText(R.id.tv_coin,entity.getCoin() + "节操");
        }
    }
}
