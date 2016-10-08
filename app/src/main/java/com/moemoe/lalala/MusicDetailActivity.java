package com.moemoe.lalala;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.Utils;
import com.app.annotation.ContentView;
import com.app.annotation.FindView;
import com.app.common.util.DensityUtil;
import com.app.image.ImageOptions;
import com.squareup.picasso.Picasso;
import com.moemoe.lalala.data.MusicInfo;
import com.moemoe.lalala.music.MusicServiceManager;
import com.moemoe.lalala.utils.IConstants;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.view.NoDoubleClickListener;
;

/**
 * Created by Haru on 2016/5/6 0006.
 */
@ContentView(R.layout.ac_music_detail)
public class MusicDetailActivity extends BaseActivity implements IConstants,View.OnClickListener {


    @FindView(R.id.iv_back)
    private ImageView mIvBack;
    @FindView(R.id.iv_music_bg)
    private ImageView mIvBg;
    @FindView(R.id.iv_music_play)
    private ImageView mIvPlay;
    @FindView(R.id.tv_music_name)
    private TextView mTvName;
    private MusicInfo mMusicInfo;
    private MusicServiceManager mServiceManager;
    private MusicPlayBroadCast mReceiver;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
//        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // 透明状态栏
//            getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            // 透明导航栏
//            getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            tintManager.setStatusBarTintEnabled(true);
//            // 设置状态栏的颜色
//            tintManager.setStatusBarTintResource(R.color.black);
//            getWindow().getDecorView().setFitsSystemWindows(true);
//        }
//    }

    @Override
    protected void initView() {
        if (mIntent == null) {
            finish();
            return;
        }
        mServiceManager = MapActivity.sMusicServiceManager;
        mMusicInfo = mServiceManager.getCurMusic();
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
        mReceiver = new MusicPlayBroadCast();
        IntentFilter filter = new IntentFilter(BROADCAST_NAME);
        filter.addAction(BROADCAST_NAME);
        registerReceiver(mReceiver, filter);
        mTvName.setText(mMusicInfo.musicName);
//        Utils.image().bind(mIvBg, StringUtils.getUrl(MusicDetailActivity.this, mMusicInfo.img, DensityUtil.getScreenWidth(), DensityUtil.dip2px(273), false, false), new ImageOptions.Builder()
//                .setSize(DensityUtil.getScreenWidth(), DensityUtil.dip2px(273))
//                .setImageScaleType(ImageView.ScaleType.FIT_CENTER)
//                .setFailureDrawableId(R.drawable.ic_default_doc_l)
//                .setLoadingDrawableId(R.drawable.ic_default_doc_l)
//                .build());
        Picasso.with(MusicDetailActivity.this)
                .load(StringUtils.getUrl(MusicDetailActivity.this, mMusicInfo.img, DensityUtil.getScreenWidth(), DensityUtil.dip2px(273), false, false))
                .fit()
                //.resize(DensityUtil.getScreenWidth(), DensityUtil.dip2px(273))
                .placeholder(R.drawable.ic_default_doc_l)
                .error(R.drawable.ic_default_doc_l)
                .into(mIvBg);
        mTvName.setSelected(true);
        changePlayState();
        mIvPlay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mMusicInfo.playState == IConstants.MPS_PLAYING || mMusicInfo.playState == IConstants.MPS_PREPARE) {
            mServiceManager.pause();
            mIvPlay.setImageResource(R.drawable.icon_video_big_play);
        } else {
            mServiceManager.playByUrl(mMusicInfo.url);
            mIvPlay.setImageResource(R.drawable.icon_video_big_stop);
        }

    }

    private void changePlayState(){
        if(mMusicInfo.playState == MPS_PLAYING || mMusicInfo.playState == MPS_PREPARE){
            mIvPlay.setImageResource(R.drawable.icon_video_big_stop);
        }else {
            mIvPlay. setImageResource(R.drawable.icon_video_big_play);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    class MusicPlayBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BROADCAST_NAME)) {
                MusicInfo musicInfo = new MusicInfo();
                int curPlayIndex = intent.getIntExtra(PLAY_MUSIC_INDEX, -1);
                int playState = intent.getIntExtra(PLAY_STATE_NAME, MPS_NOFILE);
                int prePlayPosition = intent.getIntExtra(PLAY_PRE_MUSIC_POSITION,-1);
                Bundle bundle = intent.getBundleExtra(MusicInfo.KEY_MUSIC);
                if (bundle != null) {
                    musicInfo = bundle.getParcelable(MusicInfo.KEY_MUSIC);
                }

                switch (playState) {
                    case MPS_INVALID:
                        changePlayState();
                        finish();
                        break;
                    case MPS_RESET:
                        break;
                    case MPS_PAUSE:
                        changePlayState();
                        break;
                    case MPS_PLAYING:
                        changePlayState();
                        break;
                    case MPS_PREPARE:
                        changePlayState();
                        break;
                    case MPS_NOFILE:
                        changePlayState();
                        finish();
                        break;
                }
            }
        }
    }

}
