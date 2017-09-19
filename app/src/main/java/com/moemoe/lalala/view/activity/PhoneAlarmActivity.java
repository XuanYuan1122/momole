package com.moemoe.lalala.view.activity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.AlarmClockEntity;
import com.moemoe.lalala.utils.AudioPlayer;
import com.moemoe.lalala.utils.NoDoubleClickListener;

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
        Glide.with(this)
                .load(R.drawable.alarm_bg)
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
            AudioPlayer.getInstance(this).playRaw(mAlarmClock.getRingUrl(),true,false);
        }
    }
}
