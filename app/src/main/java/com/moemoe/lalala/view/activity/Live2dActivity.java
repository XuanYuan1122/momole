package com.moemoe.lalala.view.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moemoe.lalala.BuildConfig;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerLive2dComponent;
import com.moemoe.lalala.di.modules.CoinShopModule;
import com.moemoe.lalala.di.modules.Live2dModule;
import com.moemoe.lalala.galgame.FileManager;
import com.moemoe.lalala.galgame.Live2DDefine;
import com.moemoe.lalala.galgame.Live2DManager;
import com.moemoe.lalala.galgame.Live2DView;
import com.moemoe.lalala.galgame.SoundManager;
import com.moemoe.lalala.greendao.gen.AlarmClockEntityDao;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.AlarmClockEntity;
import com.moemoe.lalala.model.entity.DeskMateEntity;
import com.moemoe.lalala.model.entity.Live2dMusicEntity;
import com.moemoe.lalala.model.entity.ShareLive2dEntity;
import com.moemoe.lalala.netamusic.data.model.PlayList;
import com.moemoe.lalala.netamusic.data.model.Song;
import com.moemoe.lalala.netamusic.player.IPlayBack;
import com.moemoe.lalala.netamusic.player.PlayMode;
import com.moemoe.lalala.netamusic.player.Player;
import com.moemoe.lalala.presenter.Live2dContract;
import com.moemoe.lalala.presenter.Live2dPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.GreenDaoManager;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ToastUtils;
import com.moemoe.lalala.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import io.reactivex.schedulers.Schedulers;

/**
 * 官方陪睡Live2d
 * Created by yi on 2017/11/2.
 */

public class Live2dActivity extends BaseAppCompatActivity implements Live2dContract.View,IPlayBack.Callback{

    @BindView(R.id.fl_container)
    FrameLayout mLive2DLayout;
    @BindView(R.id.iv_sound_load)
    ImageView mIvSoundLoad;
    @BindView(R.id.tv_text_1)
    View mTv1;
    @BindView(R.id.tv_text_2)
    View mTv2;
    @BindView(R.id.tv_text_3)
    View mTv3;
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.iv_live_len)
    ImageView mIvLen;
    @BindView(R.id.iv_live_sari)
    ImageView mIvSari;
    @BindView(R.id.iv_live_mei)
    ImageView mIvMei;
    @BindView(R.id.iv_play)
    ImageView mIvPlay;
    @BindView(R.id.iv_next)
    ImageView mIvNext;
    @BindView(R.id.tv_music_name)
    TextView mTvMusicName;

    @Inject
    Live2dPresenter mPresenter;

    private Live2DManager live2DMgr;
    private Live2DView mLive2dView;
    private ObjectAnimator mSoundLoadAnim;
    private String mCurRole;
    private Player mPlayer;
    private PlayList playList;
    private ArrayList<ShareLive2dEntity> mEntites;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_live2d;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerLive2dComponent.builder()
                .live2dModule(new Live2dModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        MoeMoeApplication.getInstance().getNetComponent().getApiService().clickDepartment("peiban")
                .subscribeOn(Schedulers.io())
                .subscribe();
        SoundManager.init(this);
        FileManager.init(this);
        mPresenter.loadMusicList();
        mPresenter.loadShareLive2dList();
        mPlayer = Player.getInstance(this);
        mPlayer.registerCallback(this);
        String model = Live2DDefine.MODEL_LEN;
        mTvMusicName.setSelected(true);
        mCurRole = "len";
        mIvLen.setAlpha(1.0f);
        mIvSari.setAlpha(0.3f);
        mIvMei.setAlpha(0.3f);
        live2DMgr = new Live2DManager(model,false);
        live2DMgr.setOnSoundLoadListener(new Live2DManager.OnSoundLoadListener() {
            @Override
            public void OnStart() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mIvSoundLoad.setVisibility(View.VISIBLE);
                        soundLoading();
                    }
                });
            }

            @Override
            public void OnLoad(int count, int position) {

            }

            @Override
            public void OnFinish() {
                if(!isFinishing())
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mSoundLoadAnim != null) {
                                mSoundLoadAnim.end();
                                mSoundLoadAnim = null;
                            }
                            if(mIvSoundLoad != null) mIvSoundLoad.setVisibility(View.GONE);
                        }
                    });
            }
        });
        mLive2dView = live2DMgr.createView(this) ;
        mLive2DLayout.addView(mLive2dView, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        AlarmClockEntity entity = GreenDaoManager.getInstance().getSession().getAlarmClockEntityDao().load(-1L);
        if(entity != null){
            mTvTime.setText(StringUtils.formatTime(entity
                    .getHour(),entity.getMinute()));
        }
    }

    private void soundLoading(){
        mSoundLoadAnim = ObjectAnimator.ofFloat(mIvSoundLoad,"alpha",0.2f,1f).setDuration(300);
        mSoundLoadAnim.setInterpolator(new LinearInterpolator());
        mSoundLoadAnim.setRepeatMode(ValueAnimator.REVERSE);
        mSoundLoadAnim.setRepeatCount(ValueAnimator.INFINITE);
        mSoundLoadAnim.start();
    }

    private int mStayTime;

    private Handler mHandler = new Handler();
    private Runnable timeRunnabel = new Runnable() {
        @Override
        public void run() {
            mStayTime++;
            mHandler.postDelayed(this,1000);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(timeRunnabel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.post(timeRunnabel);
    }

    @Override
    protected void onDestroy() {
        if(mPresenter != null) mPresenter.release();
        if(mPlayer != null){
            mPlayer.pause();
            mPlayer.setPlayList(null);
            mPlayer.unregisterCallback(this);
        }
        mHandler.removeCallbacks(timeRunnabel);
        MoeMoeApplication.getInstance().getNetComponent().getApiService().stayDepartment("peiban",mStayTime)
                .subscribeOn(Schedulers.io())
                .subscribe();
        super.onDestroy();
        SoundManager.release();
        FileManager.release();
    }

    @OnClick({R.id.rl_root_1,R.id.rl_alarm_root,R.id.iv_live_len,R.id.iv_live_mei,R.id.iv_live_sari,R.id.tv_text_1,R.id.tv_text_2,R.id.tv_text_3})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.rl_root_1:
                if(mTv1.getVisibility() == View.GONE){
                    mTv1.setVisibility(View.VISIBLE);
                    mTv2.setVisibility(View.VISIBLE);
                    mTv3.setVisibility(View.VISIBLE);
                }else {
                    mTv1.setVisibility(View.GONE);
                    mTv2.setVisibility(View.GONE);
                    mTv3.setVisibility(View.GONE);
                }
                break;
            case R.id.rl_alarm_root:
                final AlarmClockEntity mAlarmClock = new AlarmClockEntity();
                mAlarmClock.setId(-1);
                mAlarmClock.setOnOff(true); // 闹钟默认开启
                mAlarmClock.setRepeat("只响一次");
                mAlarmClock.setWeeks(null);
                if("len".equals(mCurRole)){
                    mAlarmClock.setRoleName("小莲");
                }else if("mei".equals(mCurRole)){
                    mAlarmClock.setRoleName("美藤双树");
                }else if("sari".equals(mCurRole)){
                    mAlarmClock.setRoleName("沙利尔");
                }
                mAlarmClock.setRoleId(mCurRole);
                mAlarmClock.setRingName("按时休息");
                mAlarmClock.setRingUrl(R.raw.vc_alerm_len_sleep_1);
                final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
                alertDialogUtil.createTimepickerDialog(Live2dActivity.this,mAlarmClock.getHour(),mAlarmClock.getMinute());
                alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                    @Override
                    public void CancelOnClick() {
                        alertDialogUtil.dismissDialog();
                    }

                    @Override
                    public void ConfirmOnClick() {
                        mAlarmClock.setHour(alertDialogUtil.getHour());
                        // 保存闹钟实例的分钟
                        mAlarmClock.setMinute(alertDialogUtil.getMinute());
                        mTvTime.setText(StringUtils.formatTime(alertDialogUtil
                                .getHour(),alertDialogUtil.getMinute()));
                        AlarmClockEntityDao dao = GreenDaoManager.getInstance().getSession().getAlarmClockEntityDao();
                        dao.insertOrReplace(mAlarmClock);
                        Utils.startAlarmClock(Live2dActivity.this,mAlarmClock);
                        alertDialogUtil.dismissDialog();
                    }
                });
                alertDialogUtil.showDialog();
                break;
            case R.id.iv_live_len:
                boolean have =  false;
                for(DeskMateEntity temp : PreferenceUtils.getAuthorInfo().getDeskMateEntities()){
                    if("len".equals(temp.getRoleOf())){
                        have = true;
                        break;
                    }
                }
                if(!have){
                    showToast("还未拥有角色");
                    return;
                }
                if(!"len".equals(mCurRole)){
                    mCurRole = "len";
                    live2DMgr.changeModel(Live2DDefine.MODEL_LEN);
                    mIvLen.setAlpha(1.0f);
                    mIvSari.setAlpha(0.3f);
                    mIvMei.setAlpha(0.3f);
                }
                break;
            case R.id.iv_live_mei:
                boolean have1 =  false;
                for(DeskMateEntity temp : PreferenceUtils.getAuthorInfo().getDeskMateEntities()){
                    if("mei".equals(temp.getRoleOf())){
                        have1 = true;
                        break;
                    }
                }
                if(!have1){
                    showToast("还未拥有角色");
                    return;
                }
                if(!PreferenceUtils.isLogin() || TextUtils.isEmpty(PreferenceUtils.getAuthorInfo().getVipTime())){
                    if(mEntites != null){
                        ShareLive2dEntity entity = null;
                        for(ShareLive2dEntity entity1 : mEntites){
                            if("mei".equals(entity1.getRoleOf())){
                                entity = entity1;
                            }
                        }
                        if(entity != null){
                            if(!entity.isHave()){
                                final AlertDialogUtil alertDialogUtil1 = AlertDialogUtil.getInstance();
                                alertDialogUtil1.createShareLive2dDialog(Live2dActivity.this,entity);
                                alertDialogUtil1.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                                    @Override
                                    public void CancelOnClick() {
                                        alertDialogUtil1.dismissDialog();
                                    }

                                    @Override
                                    public void ConfirmOnClick() {
                                        showShareToBuy("mei");
                                        alertDialogUtil1.dismissDialog();
                                    }
                                });
                                alertDialogUtil1.showDialog();
                                break;
                            }
                        }else {
                            break;
                        }
                    }else {
                        break;
                    }
                }
                if(!"mei".equals(mCurRole)){
                    mCurRole = "mei";
                    live2DMgr.changeModel(Live2DDefine.MODEL_MEI);
                    mIvLen.setAlpha(0.3f);
                    mIvSari.setAlpha(0.3f);
                    mIvMei.setAlpha(1.0f);
                }
                break;
            case R.id.iv_live_sari:
                boolean have2 =  false;
                for(DeskMateEntity temp : PreferenceUtils.getAuthorInfo().getDeskMateEntities()){
                    if("sari".equals(temp.getRoleOf())){
                        have2 = true;
                        break;
                    }
                }
                if(!have2){
                    showToast("还未拥有角色");
                    return;
                }
                if(!PreferenceUtils.isLogin() || TextUtils.isEmpty(PreferenceUtils.getAuthorInfo().getVipTime())){
                    if(mEntites != null){
                        ShareLive2dEntity entity = null;
                        for(ShareLive2dEntity entity1 : mEntites){
                            if("sari".equals(entity1.getRoleOf())){
                                entity = entity1;
                            }
                        }
                        if(entity != null){
                            if(!entity.isHave()){
                                final AlertDialogUtil alertDialogUtil1 = AlertDialogUtil.getInstance();
                                alertDialogUtil1.createShareLive2dDialog(Live2dActivity.this,entity);
                                alertDialogUtil1.setOnClickListener(new AlertDialogUtil.OnClickListener() {
                                    @Override
                                    public void CancelOnClick() {
                                        alertDialogUtil1.dismissDialog();
                                    }

                                    @Override
                                    public void ConfirmOnClick() {
                                        showShareToBuy("sari");
                                        alertDialogUtil1.dismissDialog();
                                    }
                                });
                                alertDialogUtil1.showDialog();
                                break;
                            }
                        }else {
                            break;
                        }
                    }else {
                        break;
                    }
                }
                if(!"sari".equals(mCurRole)){
                    mCurRole = "sari";
                    live2DMgr.changeModel(Live2DDefine.MODEL_SARI);
                    mIvLen.setAlpha(0.3f);
                    mIvSari.setAlpha(1.0f);
                    mIvMei.setAlpha(0.3f);
                }
                break;
            case R.id.tv_text_1:
            case R.id.tv_text_2:
            case R.id.tv_text_3:
                showToast("功能暂未开放");
                break;
        }
    }

    private void showShareToBuy(String role) {
        final OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setTitle("你被邀请帮助好友解锁妹子的新“姿势”");
        String url = "http://2333.moemoe.la/share/role/" + role + "/" + PreferenceUtils.getUUid();
        oks.setTitleUrl(url);
        oks.setText("青春少女孤枕难眠，点击解锁陪睡姿势" + url);
        oks.setImageUrl("http://s.moemoe.la/ic_shareout.jpg");
        oks.setUrl(url);
        oks.setSite(getString(R.string.app_name));
        oks.setSiteUrl(url);
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
        MoeMoeApplication.getInstance().getNetComponent().getApiService().shareKpi("role")
                .subscribeOn(Schedulers.io())
                .subscribe();
        oks.show(this);
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mIvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playList != null && playList.getSongs().size() == 0){
                    showToast("还没有广播歌曲呢");
                }else {
                   if(mPlayer.isPlaying()){
                       mPlayer.pause();
                       mIvPlay.setImageResource(R.drawable.btn_phone_music_play);
                   }else {
                       mPlayer.play();
                       mIvPlay.setImageResource(R.drawable.btn_phone_music_playing);
                   }
                }
            }
        });
        mIvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playList != null && playList.getSongs().size() == 0){
                    showToast("还没有广播歌曲呢");
                }else {
                    mPlayer.playNext();
                }
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onFailure(int code, String msg) {

    }

    @Override
    public void onLoadMusicListSuccess(ArrayList<Live2dMusicEntity> entities) {
        if(entities.size() > 0) mTvMusicName.setText(entities.get(0).getName());
        playList = new PlayList();
        playList.setPlayMode(PlayMode.LOOP);
        for(Live2dMusicEntity entity : entities){
            Song mMusicInfo = new Song();
            mMusicInfo.setPath(ApiService.URL_QINIU + entity.getUrl());
            mMusicInfo.setDisplayName(entity.getName());
            mMusicInfo.setDuration(entity.getTime());
            playList.addSong(mMusicInfo);
        }
        mPlayer.setPlayList(playList);
    }

    @Override
    public void onLoadShareListSuccess(ArrayList<ShareLive2dEntity> entities) {
        mEntites = entities;
    }

    @Override
    public void onSwitchLast(@Nullable Song last) {

    }

    @Override
    public void onSwitchNext(@Nullable Song next) {
        if(next != null) {
            mTvMusicName.setText(next.getDisplayName());
        }else {
            mTvMusicName.setText("");
        }
    }

    @Override
    public void onComplete(@Nullable Song next) {

    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        if(isPlaying){
            mIvPlay.setImageResource(R.drawable.btn_phone_music_playing);
        }else {
            mIvPlay.setImageResource(R.drawable.btn_phone_music_play);
        }
    }
}
