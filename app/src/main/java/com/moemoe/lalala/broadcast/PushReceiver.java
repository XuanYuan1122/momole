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
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.model.entity.ReceiverInfo;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.view.activity.SplashActivity;

/**
 * Created by yi on 2016/11/28.
 */

public class PushReceiver extends BroadcastReceiver {
    private static int notificationId = 100;

    @Override
    public void onReceive(Context context, Intent intent) {
        String result = intent.getStringExtra("data");
        ReceiverInfo info = new ReceiverInfo();
        info.readFromJsonContent(result);
        if(info.type.equals("SCHEMA")){//跳转
            if (info.showNotify){
                showNotification(context,info);
            }
        }else if(info.type.equals("COMMAND")){//命令
            if(info.schema.equals("CLEAR_DATA")){//清理数据
               // DataCleanManager.cleanApplicationData(context);
            }
        }
    }

    private void showNotification(Context context,ReceiverInfo info) {
        if(!TextUtils.isEmpty(info.messageType)) PreferenceUtils.setMessageDot(context,info.messageType,true);
        Bitmap btm = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_launcher);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.ic_launcher, 1000)
                .setContentTitle(info.title)
                .setContentText(info.content);
        mBuilder.setTicker(info.title + ":" + info.content);

        mBuilder.setAutoCancel(true);
        Intent resultIntent;
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
            Glide.with(context)
                    .load(info.logoUrl)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            Bitmap bitmap = drawableToBitamp(resource);
                            mBuilder.setLargeIcon(bitmap);
                            mNotificationManager.notify(notificationId++, mBuilder.build());
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
