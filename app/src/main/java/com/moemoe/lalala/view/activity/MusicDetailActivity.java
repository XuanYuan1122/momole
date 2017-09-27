package com.moemoe.lalala.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.netamusic.data.model.Song;
import com.moemoe.lalala.netamusic.player.IPlayBack;
import com.moemoe.lalala.netamusic.player.Player;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;

import butterknife.BindView;

/**
 * Created by yi on 2016/12/1.
 */

public class MusicDetailActivity extends BaseAppCompatActivity implements IPlayBack.Callback,View.OnClickListener{


    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.iv_music_bg)
    ImageView mIvBg;
    @BindView(R.id.iv_music_play)
    ImageView mIvPlay;
    @BindView(R.id.tv_music_name)
    TextView mTvName;
    private Player mPlayer;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_music_detail;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        if (getIntent() == null) {
            finish();
            return;
        }
        mPlayer = Player.getInstance(this);
        mPlayer.registerCallback(this);
        Song mMusicInfo = mPlayer.getPlayingSong();
        if (mMusicInfo == null) {
            finish();
            return;
        }
        mTvName.setText(mMusicInfo.getDisplayName());
        Glide.with(MusicDetailActivity.this)
                .load(StringUtils.getUrl(MusicDetailActivity.this, ApiService.URL_QINIU + mMusicInfo.getCoverPath(), DensityUtil.getScreenWidth(this), DensityUtil.dip2px(this,273), false, false))
                .override(DensityUtil.getScreenWidth(this), DensityUtil.dip2px(this,273))
                .placeholder(R.drawable.bg_default_square)
                .error(R.drawable.bg_default_square)
                .into(mIvBg);
        mTvName.setSelected(true);
        changePlayState();

    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mIvPlay.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onSwitchLast(@Nullable Song last) {

    }

    @Override
    public void onSwitchNext(@Nullable Song next) {

    }

    @Override
    public void onComplete(@Nullable Song next) {

    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {

    }

    @Override
    public void onClick(View view) {
        if (mPlayer == null) return;

        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.play();
        }
        changePlayState();
    }

    private void changePlayState(){
        if(mPlayer.isPlaying()){
            mIvPlay.setImageResource(R.drawable.icon_video_big_stop);
        }else {
            mIvPlay. setImageResource(R.drawable.icon_video_big_play);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.unregisterCallback(this);
    }
}
