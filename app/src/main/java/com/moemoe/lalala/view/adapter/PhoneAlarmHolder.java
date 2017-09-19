package com.moemoe.lalala.view.adapter;

import android.app.Activity;
import android.app.NotificationManager;
import android.view.View;
import android.widget.ImageView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.event.AlarmEvent;
import com.moemoe.lalala.greendao.gen.AlarmClockEntityDao;
import com.moemoe.lalala.model.entity.AlarmClockEntity;
import com.moemoe.lalala.utils.AudioPlayer;
import com.moemoe.lalala.utils.GreenDaoManager;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.Utils;
import com.moemoe.lalala.view.widget.adapter.ClickableViewHolder;

/**
 * Created by yi on 2017/7/21.
 */

public class PhoneAlarmHolder extends ClickableViewHolder {

    ImageView onOff;

    public PhoneAlarmHolder(View itemView) {
        super(itemView);
        onOff = $(R.id.iv_on);
    }

    public void createItem(final AlarmClockEntity entity, final int position){

        String timeTmp = StringUtils.formatTime(entity.getHour(),
                entity.getMinute());
        // 设定闹钟时间的显示
        setText(R.id.tv_time,timeTmp);
        setText(R.id.tv_week,entity.getRepeat());
        setText(R.id.tv_type,entity.getRingName());
        onOff.setSelected(entity.isOnOff());
        onOff.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(onOff.isSelected()){
                    if(!entity.isOnOff()){
                        return;
                    }
                    AlarmClockEntityDao dao = GreenDaoManager.getInstance().getSession().getAlarmClockEntityDao();
                    entity.setOnOff(false);
                    dao.insertOrReplace(entity);
                    Utils.cancelAlarmClock(itemView.getContext(), (int) entity.getId());
                    NotificationManager notificationManager = (NotificationManager) itemView.getContext().
                            getSystemService(
                                    Activity.NOTIFICATION_SERVICE);
                    // 取消下拉列表通知消息
                    notificationManager.cancel((int) entity.getId());
                    // 停止播放
                    AudioPlayer.getInstance(itemView.getContext()).stop();
                    RxBus.getInstance().post(new AlarmEvent(entity,3));
                }else {
                    if(!entity.isOnOff()){
                        AlarmClockEntityDao dao = GreenDaoManager.getInstance().getSession().getAlarmClockEntityDao();
                        entity.setOnOff(true);
                        dao.insertOrReplace(entity);
                        RxBus.getInstance().post(new AlarmEvent(entity,3));
                    }
                }
            }
        });
    }
}
