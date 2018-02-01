package com.moemoe.lalala.view.adapter;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.os.Handler;
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

import com.moemoe.lalala.R;

import com.moemoe.lalala.event.PhonePlayMusicEvent;
import com.moemoe.lalala.model.entity.LuYinEntity;
import com.moemoe.lalala.utils.AudioPlayer;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

import org.greenrobot.eventbus.EventBus;

/**
 *
 * Created by yi on 2017/7/21.
 */

public class PhoneLuYinListHolder extends ClickableViewHolder{

    private PhoneLuYinListAdapter mAdapter;

    private Handler mHandler;
    private Runnable mProgressCallback;
    private SeekBar seekBar;

    public PhoneLuYinListHolder(View itemView,PhoneLuYinListAdapter mAdapter) {
        super(itemView);
        this.mAdapter = mAdapter;
        seekBar = $(R.id.seek_music);
    }

    public void createItem(final LuYinEntity entity, final int position, int playingPosition, final String type){
        if(entity.isFlag()){
            setImageResource(R.id.iv_cover,R.drawable.ic_phone_tape);
            if(playingPosition == position){
                $(R.id.ll_root).setBackgroundResource(R.drawable.shape_rect_border_main_white_background_y12);
                setImageResource(R.id.iv_play,R.drawable.btn_phone_music_playing);
                setText(R.id.tv_state,"播放中");
                ((TextView)$(R.id.tv_state)).setTextColor(ContextCompat.getColor(context,R.color.main_cyan));
                seekBar.setVisibility(View.VISIBLE);
                setVisible(R.id.iv_share,false);
                seekBar.setMax(entity.getTimestamp());
                seekBar.setProgress(0);
                AudioPlayer.getInstance(context).play(StorageUtils.getMusicRootPath() + entity.getSound().substring(entity.getSound().lastIndexOf("/") + 1));
                mHandler = new Handler();
                mProgressCallback = new Runnable() {
                    @Override
                    public void run() {
                        if (AudioPlayer.getInstance(context).isPlaying()) {
                            if(mAdapter.getPlayingPosition() != -1){
                                seekBar.setProgress(seekBar.getProgress() + 1);
                                mHandler.postDelayed(this, 1000);
                            }
                        }else {
                            int position = mAdapter.getPlayingPosition();
                            mAdapter.setPlayingPosition(-1);
                            if(position >= 0){
                                mAdapter.notifyItemChanged(position);
                            }
                            mHandler.removeCallbacks(this);
                        }
                    }
                };
                mHandler.postDelayed(mProgressCallback,1000);
                $(R.id.iv_play).setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        if(mHandler != null) mHandler.removeCallbacks(mProgressCallback);
                        AudioPlayer.getInstance(context).stop();
                        EventBus.getDefault().post(new PhonePlayMusicEvent(entity.getSound(),position,false,entity.getTimestamp(),type,entity.getSoundName()));
                    }
                });
            }else {
                $(R.id.ll_root).setBackgroundResource(R.drawable.shape_rect_white_background_y12);
                setText(R.id.tv_state,"已拥有");
                setImageResource(R.id.iv_play,R.drawable.btn_phone_music_play);
                ((TextView)$(R.id.tv_state)).setTextColor(ContextCompat.getColor(context,R.color.pink_fb7ba2));
                seekBar.setVisibility(View.GONE);
                setVisible(R.id.iv_share,false);
                $(R.id.iv_play).setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        EventBus.getDefault().post(new PhonePlayMusicEvent(entity.getSound(),position,true,entity.getTimestamp(),type,entity.getSoundName()));
                    }
                });
            }

        }else {
            $(R.id.ll_root).setBackgroundResource(R.drawable.shape_rect_d7d7d7_background_y12);
            setImageResource(R.id.iv_cover,R.drawable.ic_phone_tape_lock);
            setImageResource(R.id.iv_play,R.drawable.btn_phone_tape_play_lock);
            setText(R.id.tv_state,"需要消耗一张录音券");
            ((TextView)$(R.id.tv_state)).setTextColor(ContextCompat.getColor(context,R.color.gray_929292));
            seekBar.setVisibility(View.GONE);
            setVisible(R.id.iv_share,false);
            $(R.id.iv_play).setOnClickListener(null);
        }
        setText(R.id.tv_title,entity.getSoundName());
    }

}
