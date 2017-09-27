package com.moemoe.lalala.view.adapter;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.event.PhonePlayMusicEvent;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.CommentV2Entity;
import com.moemoe.lalala.model.entity.CommentV2SecEntity;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.LuYinEntity;
import com.moemoe.lalala.model.entity.tag.UserUrlSpan;
import com.moemoe.lalala.netamusic.data.model.Song;
import com.moemoe.lalala.netamusic.player.Player;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.LevelSpan;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.tag.TagControl;
import com.moemoe.lalala.view.activity.CommentListActivity;
import com.moemoe.lalala.view.activity.CommentSecListActivity;
import com.moemoe.lalala.view.activity.DynamicActivity;
import com.moemoe.lalala.view.activity.ImageBigSelectActivity;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 *
 * Created by yi on 2017/7/21.
 */

public class PhoneLuYinListHolder extends ClickableViewHolder implements SeekBar.OnSeekBarChangeListener{

    public PhoneLuYinListHolder(View itemView) {
        super(itemView);
    }

    public void createItem(final LuYinEntity entity, final int position,int playingPosition){
        ((SeekBar)$(R.id.seek_music)).setOnSeekBarChangeListener(null);
        if(entity.isFlag()){
            setImageResource(R.id.iv_cover,R.drawable.ic_phone_tape);
            if(playingPosition == position){
                $(R.id.ll_root).setBackgroundResource(R.drawable.shape_rect_border_main_white_background_y12);
                setImageResource(R.id.iv_play,R.drawable.btn_phone_music_playing);
                setText(R.id.tv_state,"播放中");
                ((TextView)$(R.id.tv_state)).setTextColor(ContextCompat.getColor(context,R.color.main_cyan));
                setVisible(R.id.seek_music,true);
                setVisible(R.id.iv_share,false);
                ((SeekBar)$(R.id.seek_music)).setMax(entity.getTimestamp());
                ((SeekBar)$(R.id.seek_music)).setOnSeekBarChangeListener(this);
                $(R.id.iv_play).setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        RxBus.getInstance().post(new PhonePlayMusicEvent(entity.getSound(),position,false,entity.getTimestamp()));
                    }
                });
            }else {
                $(R.id.ll_root).setBackgroundResource(R.drawable.shape_rect_white_background_y12);
                setText(R.id.tv_state,"已拥有");
                setImageResource(R.id.iv_play,R.drawable.btn_phone_music_play);
                ((TextView)$(R.id.tv_state)).setTextColor(ContextCompat.getColor(context,R.color.pink_fb7ba2));
                setVisible(R.id.seek_music,false);
                setVisible(R.id.iv_share,false);
                $(R.id.iv_play).setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        RxBus.getInstance().post(new PhonePlayMusicEvent(entity.getSound(),position,true,entity.getTimestamp()));
                    }
                });
            }

        }else {
            $(R.id.ll_root).setBackgroundResource(R.drawable.shape_rect_d7d7d7_background_y12);
            setImageResource(R.id.iv_cover,R.drawable.ic_phone_tape_lock);
            setImageResource(R.id.iv_play,R.drawable.btn_phone_tape_play_lock);
            setText(R.id.tv_state,"需要消耗一张录音券");
            ((TextView)$(R.id.tv_state)).setTextColor(ContextCompat.getColor(context,R.color.gray_929292));
            setVisible(R.id.seek_music,false);
            setVisible(R.id.iv_share,false);
            $(R.id.iv_play).setOnClickListener(null);
        }
        setText(R.id.tv_title,entity.getSoundName());
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            seekBar.setProgress(progress,true);
        }else {
            seekBar.setProgress(progress);
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
