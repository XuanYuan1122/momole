package com.moemoe.lalala;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.util.DensityUtil;
import com.moemoe.lalala.netamusic.data.model.Song;
import com.moemoe.lalala.netamusic.player.IPlayBack;
import com.moemoe.lalala.netamusic.player.Player;
import com.moemoe.lalala.utils.IConstants;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.NoDoubleClickListener;
import com.squareup.picasso.Picasso;

;

/**
 * Created by Haru on 2016/5/6 0006.
 */
@ContentView(R.layout.ac_music_detail)
public class MusicDetailActivity extends BaseActivity implements IConstants,View.OnClickListener,IPlayBack.Callback{


    @FindView(R.id.iv_back)
    private ImageView mIvBack;
    @FindView(R.id.iv_music_bg)
    private ImageView mIvBg;
    @FindView(R.id.iv_music_play)
    private ImageView mIvPlay;
    @FindView(R.id.tv_music_name)
    private TextView mTvName;
    private Song mMusicInfo;
    //private MusicServiceManager mServiceManager;
    private Player mPlayer;
   // private MusicPlayBroadCast mReceiver;

    @Override
    protected void initView() {
        if (mIntent == null) {
            finish();
            return;
        }
        //mServiceManager = MapActivity.sMusicServiceManager;
        mPlayer = Player.getInstance();
        mPlayer.registerCallback(this);
       // mMusicInfo = mServiceManager.getCurMusic();
        mMusicInfo = mPlayer.getPlayingSong();
        if (mMusicInfo == null) {
            finish();
            return;
        }
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
       // mReceiver = new MusicPlayBroadCast();
        //IntentFilter filter = new IntentFilter(BROADCAST_NAME);
       // filter.addAction(BROADCAST_NAME);
      //  registerReceiver(mReceiver, filter);
        mTvName.setText(mMusicInfo.getDisplayName());
        Picasso.with(MusicDetailActivity.this)
                .load(StringUtils.getUrl(MusicDetailActivity.this, mMusicInfo.getCoverPath(), DensityUtil.getScreenWidth(), DensityUtil.dip2px(273), false, false))
                .fit()
                .placeholder(R.drawable.ic_default_doc_l)
                .error(R.drawable.ic_default_doc_l)
                .config(Bitmap.Config.RGB_565)
                .into(mIvBg);
        mTvName.setSelected(true);
        changePlayState();
        mIvPlay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//        if (mMusicInfo.playState == IConstants.MPS_PLAYING || mMusicInfo.playState == IConstants.MPS_PREPARE) {
//            mServiceManager.pause();
//            mIvPlay.setImageResource(R.drawable.icon_video_big_play);
//        } else {
//            mServiceManager.playByUrl(mMusicInfo.url);
//            mIvPlay.setImageResource(R.drawable.icon_video_big_stop);
//        }
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
       // unregisterReceiver(mReceiver);
    }

    @Override
    public void onSwitchLast(@Nullable Song last) {

    }

    @Override
    public void onSwitchNext(@Nullable Song next) {

    }

    @Override
    public void onComplete(@Nullable Song next) {
        changePlayState();
    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        changePlayState();
    }

//    class MusicPlayBroadCast extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(intent.getAction().equals(BROADCAST_NAME)) {
//                MusicInfo musicInfo = new MusicInfo();
//                int curPlayIndex = intent.getIntExtra(PLAY_MUSIC_INDEX, -1);
//                int playState = intent.getIntExtra(PLAY_STATE_NAME, MPS_NOFILE);
//                int prePlayPosition = intent.getIntExtra(PLAY_PRE_MUSIC_POSITION,-1);
//                Bundle bundle = intent.getBundleExtra(MusicInfo.KEY_MUSIC);
//                if (bundle != null) {
//                    musicInfo = bundle.getParcelable(MusicInfo.KEY_MUSIC);
//                }
//
//                switch (playState) {
//                    case MPS_INVALID:
//                        changePlayState();
//                        finish();
//                        break;
//                    case MPS_RESET:
//                        break;
//                    case MPS_PAUSE:
//                        changePlayState();
//                        break;
//                    case MPS_PLAYING:
//                        changePlayState();
//                        break;
//                    case MPS_PREPARE:
//                        changePlayState();
//                        break;
//                    case MPS_NOFILE:
//                        changePlayState();
//                        finish();
//                        break;
//                }
//            }
//        }
//    }

}
