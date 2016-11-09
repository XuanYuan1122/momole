package com.moemoe.lalala.broadcast;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.app.Utils;
import com.app.common.Callback;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.moemoe.lalala.R;
import com.moemoe.lalala.SplashActivity;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.data.ReceiverInfo;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.DataCleanManager;

/**
 * Created by Haru on 2016/5/30 0030.
 */
public class PushReceiver extends BroadcastReceiver {
    private static int notificationId = 100;
    //推送类型
    private static String TYPE_SCHEMA = "SCHEMA";//跳转
    private static String TYPE_COMMAND = "COMMAND";//命令

    //command 命令
    private static String COMMAND_CLEAR_DATA = "CLEAR_DATA";//清理数据

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                String result = new String(bundle.getByteArray("payload"));
                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");
                ReceiverInfo info = new ReceiverInfo();
                info.readFromJsonContent(result);
                if(info.type.equals(TYPE_SCHEMA)){
                    if (info.showNotify){
                        showNotification(context,info);
                    }
                }else if(info.type.equals(TYPE_COMMAND)){
                    if(info.schema.equals(COMMAND_CLEAR_DATA)){//清理数据
                        DataCleanManager.cleanApplicationData(context);
                    }
                }
                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
                break;
            case PushConsts.GET_CLIENTID:
                break;
            case PushConsts.THIRDPART_FEEDBACK:
                break;
        }
    }

    private void showNotification(Context context,ReceiverInfo info) {
        Bitmap btm = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_launcher);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.ic_launcher, 1000)
                .setContentTitle(info.title)
                .setContentText(info.content);
        mBuilder.setTicker(info.title + ":" + info.content);

        mBuilder.setAutoCancel(true);
        Intent resultIntent = null;
        if (AppSetting.isRunning) {
            resultIntent = IntentUtils.getIntentFromUri(context, Uri.parse(info.schema));
        } else {
            resultIntent = new Intent(context,
                    SplashActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            resultIntent.putExtra("schema", info.schema);
        }
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                context, notificationId, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (TextUtils.isEmpty(info.logoUrl)) {
            mBuilder.setLargeIcon(btm);
            mNotificationManager.notify(notificationId++, mBuilder.build());
        } else {
            Utils.image().loadDrawable(info.logoUrl, null, new Callback.CommonCallback<Drawable>() {
                @Override
                public void onSuccess(Drawable result) {
                    Bitmap bitmap = drawableToBitamp(result);
                    mBuilder.setLargeIcon(bitmap);
                    mNotificationManager.notify(notificationId++, mBuilder.build());
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {

                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        }
    }

    private Bitmap drawableToBitamp(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config =
                 drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                         : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w,h,config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
     }
}
