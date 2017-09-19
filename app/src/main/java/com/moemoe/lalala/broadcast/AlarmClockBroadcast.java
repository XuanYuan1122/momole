package com.moemoe.lalala.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.moemoe.lalala.greendao.gen.AlarmClockEntityDao;
import com.moemoe.lalala.model.entity.AlarmClockEntity;
import com.moemoe.lalala.utils.GreenDaoManager;
import com.moemoe.lalala.utils.Utils;
import com.moemoe.lalala.view.activity.PhoneAlarmActivity;

/**
 * Created by yi on 2017/9/12.
 */

public class AlarmClockBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmClockEntity alarmClock = intent.getParcelableExtra("alarm");
        if (alarmClock != null) {
            // 单次响铃
            if (alarmClock.getWeeks() == null) {
                AlarmClockEntityDao dao = GreenDaoManager.getInstance().getSession().getAlarmClockEntityDao();
                AlarmClockEntity entity = dao.queryBuilder()
                        .where(AlarmClockEntityDao.Properties.Id.eq(alarmClock.getId()))
                        .unique();
                entity.setOnOff(false);
                dao.insertOrReplace(entity);
                Intent i = new Intent("com.moemoe.lalala.AlarmClockOff");
                context.sendBroadcast(i);
            }else {
                // 重复周期闹钟
                Utils.startAlarmClock(context,alarmClock);
            }
        }

        Intent i = new Intent(context, PhoneAlarmActivity.class);
        i.putExtra("alarm",alarmClock);
        // 清除栈顶的Activity
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
    }
}
