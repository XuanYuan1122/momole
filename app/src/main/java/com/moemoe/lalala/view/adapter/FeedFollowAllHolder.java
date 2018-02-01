package com.moemoe.lalala.view.adapter;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.event.RefreshListEvent;
import com.moemoe.lalala.model.entity.FeedFollowType1Entity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.TagUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;

/**
 *
 * Created by yi on 2017/7/21.
 */

public class FeedFollowAllHolder extends ClickableViewHolder {

    public FeedFollowAllHolder(View itemView) {
        super(itemView);
    }

    public void createItem(final FeedFollowType1Entity entity, final int position){
        //cover
        int w,h;
        if("DOC".equals(entity.getType())){
            w = h = context.getResources().getDimensionPixelSize(R.dimen.y180);
            Glide.with(context)
                    .load(StringUtils.getUrl(context,entity.getCover(),w,h,false,true))
                    .error(R.drawable.bg_default_square)
                    .placeholder(R.drawable.bg_default_square)
                    .bitmapTransform(new CropSquareTransformation(context))
                    .into((ImageView) $(R.id.iv_cover));
        }else {
            w = context.getResources().getDimensionPixelSize(R.dimen.x222);
            h = context.getResources().getDimensionPixelSize(R.dimen.y190);
            Glide.with(context)
                    .load(StringUtils.getUrl(context,entity.getCover(),w,h,false,true))
                    .error(R.drawable.bg_default_square)
                    .placeholder(R.drawable.bg_default_square)
                    .bitmapTransform(new CropTransformation(context,w,h))
                    .into((ImageView) $(R.id.iv_cover));
        }

        //title
        setText(R.id.tv_title,entity.getTitle());

        //tag
        int[] tagsId = {R.id.tv_tag_1,R.id.tv_tag_2};
        $(tagsId[0]).setOnClickListener(null);
        $(tagsId[1]).setOnClickListener(null);
        if(entity.getTags().size() > 1){
            $(tagsId[0]).setVisibility(View.VISIBLE);
            $(tagsId[1]).setVisibility(View.VISIBLE);
        }else if(entity.getTags().size() > 0){
            $(tagsId[0]).setVisibility(View.VISIBLE);
            $(tagsId[1]).setVisibility(View.INVISIBLE);
        }else {
            $(tagsId[0]).setVisibility(View.INVISIBLE);
            $(tagsId[1]).setVisibility(View.INVISIBLE);
        }
        int size = tagsId.length > entity.getTags().size() ? entity.getTags().size() : tagsId.length;
        for (int i = 0;i < size;i++){
            TagUtils.setBackGround(entity.getTags().get(i).getText(),$(tagsId[i]));
            $(tagsId[i]).setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    //TODO 跳转标签页
                }
            });
        }

        //user
        int avatarSize = context.getResources().getDimensionPixelSize(R.dimen.y32);
        Glide.with(context)
                .load(StringUtils.getUrl(context,entity.getUserAvatar(),avatarSize,avatarSize,false,true))
                .error(R.drawable.bg_default_circle)
                .placeholder(R.drawable.bg_default_circle)
                .bitmapTransform(new CropCircleTransformation(context))
                .into((ImageView) $(R.id.iv_user_avatar));
        setText(R.id.tv_user_name,entity.getUserName());
        $(R.id.iv_user_avatar).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ViewUtils.toPersonal(context,entity.getUserId());
            }
        });
        $(R.id.tv_user_name).setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                ViewUtils.toPersonal(context,entity.getUserId());
            }
        });
        try {
            JSONObject json = new JSONObject(entity.getExtra());
            //extra content
            if("DOC".equals(entity.getType())){
                int readNum = json.getInt("readNum");
                setText(R.id.tv_extra_content,"阅读 " + readNum + " · " + StringUtils.timeFormat(entity.getCreateTime()));
            }else {
                setText(R.id.tv_extra_content,StringUtils.timeFormat(entity.getCreateTime()));
            }

            //special
            if("MOVIE".equals(entity.getType())){
                int playNum = json.getInt("playNum");
                int danmuNum = json.getInt("danmuNum");
                int stampTime = json.getInt("stampTime");
                int coin = json.getInt("coin");
                TextView tv_play = $(R.id.tv_play_num);
                tv_play.setText(String.valueOf(playNum));
                tv_play.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.ic_baglist_video_playtimes_gray),null,null,null);

                TextView tv_danMu = $(R.id.tv_danmu_num);
                tv_danMu.setVisibility(View.VISIBLE);
                tv_danMu.setText(String.valueOf(danmuNum));

                setText(R.id.tv_coin,coin + "节操");
                setText(R.id.tv_extra,StringUtils.getMinute(stampTime));
                setVisible(R.id.iv_play,true);
                setVisible(R.id.tv_mark,false);
                setImageResource(R.id.iv_play,R.drawable.ic_baglist_video_play);
            }else if("MUSIC".equals(entity.getType())){
                int playNum = json.getInt("playNum");
                int stampTime = json.getInt("stampTime");
                int coin = json.getInt("coin");
                TextView tv_play = $(R.id.tv_play_num);
                tv_play.setText(String.valueOf(playNum));
                tv_play.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.ic_baglist_music_times),null,null,null);

                TextView tv_danMu = $(R.id.tv_danmu_num);
                tv_danMu.setVisibility(View.GONE);

                setText(R.id.tv_coin,coin + "节操");
                setText(R.id.tv_extra,StringUtils.getMinute(stampTime));
                setVisible(R.id.iv_play,true);
                setVisible(R.id.tv_mark,false);
                setImageResource(R.id.iv_play,R.drawable.ic_baglist_music_play);
            }else if("FOLDER".equals(entity.getType())){
                int fileNum = json.getInt("fileNum");
                int coin = json.getInt("coin");
                String type = json.getString("type");

                setText(R.id.tv_coin,coin + "节操");
                setText(R.id.tv_extra,fileNum + "项");

                setVisible(R.id.iv_play,false);

                setVisible(R.id.tv_mark,true);
                if(type.equals(FolderType.ZH.toString())){
                    setText(R.id.tv_mark,"综合");
                    $(R.id.tv_mark).setBackgroundResource(R.drawable.shape_rect_zonghe);
                }else if(type.equals(FolderType.TJ.toString())){
                    setText(R.id.tv_mark,"图集");
                    $(R.id.tv_mark).setBackgroundResource(R.drawable.shape_rect_tuji);
                }else if(type.equals(FolderType.MH.toString())){
                    setText(R.id.tv_mark,"漫画");
                    $(R.id.tv_mark).setBackgroundResource(R.drawable.shape_rect_manhua);
                }else if(type.equals(FolderType.XS.toString())){
                    setText(R.id.tv_mark,"小说");
                    $(R.id.tv_mark).setBackgroundResource(R.drawable.shape_rect_xiaoshuo);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //history
        if(PreferenceUtils.getFeedLastItem(context,"follow_all").equals(entity.getId())){
            setVisible(R.id.fl_history_root,true);
            EventBus.getDefault().post(new RefreshListEvent("follow_all"));
        }else {
            setVisible(R.id.fl_history_root,false);
        }

    }
}
