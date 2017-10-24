package com.moemoe.lalala.view.activity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.AlarmClockEntity;
import com.moemoe.lalala.utils.AudioPlayer;
import com.moemoe.lalala.utils.NoDoubleClickListener;

import java.util.Random;

import butterknife.BindView;

/**
 * 闹钟响起画面
 * Created by yi on 2017/9/13.
 */

public class PhoneAlarmActivity extends BaseAppCompatActivity {

    @BindView(R.id.tv_mark)
    TextView mTvMark;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.ll_accept_root)
    View mAcceptRoot;
    @BindView(R.id.ll_reject_root)
    View mRejectRoot;
    @BindView(R.id.tv_reject)
    TextView mTvReject;
    @BindView(R.id.iv_bg)
    ImageView mIvBg;

    private AlarmClockEntity mAlarmClock;
    /**
     * 线程运行flag
     */
    private boolean mIsRun = true;

    /**
     * 线程标记
     */
    private static final int UPDATE_TIME = 1;

    /**
     * 通知消息管理
     */
    private NotificationManagerCompat mNotificationManager;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_alarm;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mAlarmClock = getIntent()
                .getParcelableExtra("alarm");
        if(mAlarmClock != null){
            mTvMark.setText(mAlarmClock.getTag());
            mTvName.setText(mAlarmClock.getRoleName());
        }
        mNotificationManager = NotificationManagerCompat.from(this);
        if (mAlarmClock != null) {
            // 取消下拉列表通知消息
            mNotificationManager.cancel((int) mAlarmClock.getId());
        }
        AudioPlayer.getInstance(this).playRaw(R.raw.bgm_alarm,true,true);
        mAcceptRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                mAcceptRoot.setVisibility(View.GONE);
                mTvReject.setVisibility(View.INVISIBLE);
                playRing();
            }
        });
        mRejectRoot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
         System.out.println("alarm:" + mAlarmClock.getRoleId());
        int bg = R.drawable.bg_alarm_len;
        if(mAlarmClock.getRoleId().equals("len")){
            bg = R.drawable.bg_alarm_len;
        }else if(mAlarmClock.getRoleId().equals("mei")){
            bg = R.drawable.bg_alarm_mei;
        }else if(mAlarmClock.getRoleId().equals("sari")){
            bg = R.drawable.bg_alarm_sari;
        }
        Glide.with(this)
                .load(bg)
                .into(mIvBg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioPlayer.getInstance(this).stop();
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onBackPressed() {

    }

    /**
     * 播放铃声
     */
    private void playRing() {
        if(mAlarmClock != null){
            AudioPlayer.getInstance(this).stop();
            AudioPlayer.getInstance(this).playRaw(getAlarmUrl(),true,false);
        }
    }

    private int getAlarmUrl(){
        String role = mAlarmClock.getRoleId();

        if(role.equals("mei")){
            if(mAlarmClock.getRingName().equals("按时休息")){
                int[] urls = {R.raw.vc_alerm_mei_sleep_1,R.raw.vc_alerm_mei_sleep_2};
                mAlarmClock.setRingUrl(randomUrl(urls));
            }else if(mAlarmClock.getRingName().equals("起床提醒")){
                int[] urls = {R.raw.vc_alerm_mei_wakeup_1,R.raw.vc_alerm_mei_wakeup_2};
                mAlarmClock.setRingUrl(randomUrl(urls));
            }else {
                int[] urls = {R.raw.vc_alerm_mei_remind_1,R.raw.vc_alerm_mei_remind_2};
                mAlarmClock.setRingUrl(randomUrl(urls));
            }
        }else if(role.equals("sari")){
            if(mAlarmClock.getRingName().equals("按时休息")){
                int[] urls = {R.raw.vc_alerm_sari_sleep_1,R.raw.vc_alerm_sari_sleep_2};
                mAlarmClock.setRingUrl(randomUrl(urls));
            }else if(mAlarmClock.getRingName().equals("起床提醒")){
                int[] urls = {R.raw.vc_alerm_sari_wakeup_1,R.raw.vc_alerm_sari_wakeup_2};
                mAlarmClock.setRingUrl(randomUrl(urls));
            }else {
                int[] urls = {R.raw.vc_alerm_sari_remind_1,R.raw.vc_alerm_sari_remind_2};
                mAlarmClock.setRingUrl(randomUrl(urls));
            }
        }else {
            if(mAlarmClock.getRingName().equals("按时休息")){
                int[] urls = {R.raw.vc_alerm_len_sleep_1,R.raw.vc_alerm_len_sleep_2};
                mAlarmClock.setRingUrl(randomUrl(urls));
            }else if(mAlarmClock.getRingName().equals("起床提醒")){
                int[] urls = {R.raw.vc_alerm_len_wakeup_1,R.raw.vc_alerm_len_wakeup_2};
                mAlarmClock.setRingUrl(randomUrl(urls));
            }else {
                int[] urls = {R.raw.vc_alerm_len_remind_1,R.raw.vc_alerm_len_remind_2};
                mAlarmClock.setRingUrl(randomUrl(urls));
            }
        }
        return mAlarmClock.getRingUrl();
    }

    private int randomUrl(int[] urls){
        Random random = new Random();
        int i = random.nextInt(urls.length);
        return urls[i];
    }
}
