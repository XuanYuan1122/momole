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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.app.RxBus;
import com.moemoe.lalala.event.ChatEvent;
import com.moemoe.lalala.event.PrivateMessageEvent;
import com.moemoe.lalala.event.SystemMessageEvent;
import com.moemoe.lalala.greendao.gen.ChatContentDbEntityDao;
import com.moemoe.lalala.greendao.gen.ChatUserEntityDao;
import com.moemoe.lalala.greendao.gen.GroupUserEntityDao;
import com.moemoe.lalala.greendao.gen.PrivateMessageItemEntityDao;
import com.moemoe.lalala.model.entity.ChatContentDbEntity;
import com.moemoe.lalala.model.entity.ChatContentEntity;
import com.moemoe.lalala.model.entity.ChatUserEntity;
import com.moemoe.lalala.model.entity.GroupUserEntity;
import com.moemoe.lalala.model.entity.PrivateMessageItemEntity;
import com.moemoe.lalala.model.entity.ReceiverInfo;
import com.moemoe.lalala.utils.GreenDaoManager;
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
        }else if(info.type.equals("TALK")){
            //存入数据库
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            ChatContentEntity entityTemp = null;
            try {
                 entityTemp = gson.fromJson(info.data,ChatContentEntity.class);
            }catch (Exception e){
                e.printStackTrace();
            }
            if(entityTemp != null){
                //私信列表
                PrivateMessageItemEntityDao messageItemEntityDao = GreenDaoManager.getInstance().getSession().getPrivateMessageItemEntityDao();
                PrivateMessageItemEntity entity = messageItemEntityDao.queryBuilder()
                        .where(PrivateMessageItemEntityDao.Properties.TalkId.eq(entityTemp.getTalkId()))
                        .limit(1)
                        .unique();
                if(entity != null){
                    entity.setDot(entity.getDot() + 1);
                    entity.setUpdateTime(entityTemp.getCreateTime());
                    entity.setName(entityTemp.getUserName());
                    entity.setIcon(entityTemp.getUserIcon());
                    entity.setContent(entityTemp.getContent());
                    entity.setState(false);
                }else {
                    entity = new PrivateMessageItemEntity();
                    entity.setDot(1);
                    entity.setUpdateTime(entityTemp.getCreateTime());
                    entity.setName(entityTemp.getUserName());
                    entity.setIcon(entityTemp.getUserIcon());
                    entity.setContent(entityTemp.getContent());
                    entity.setTalkId(entityTemp.getTalkId());
                    entity.setNew(true);
                    entity.setState(false);
                }
                messageItemEntityDao.insertOrReplace(entity);
                //对话详情
                ChatContentDbEntityDao chatContentEntityDao = GreenDaoManager.getInstance().getSession().getChatContentDbEntityDao();
                ChatContentDbEntity chatContentDbEntity = new ChatContentDbEntity(entityTemp);
                chatContentEntityDao.insertOrReplace(chatContentDbEntity);
                //列表与用户中间表
                GroupUserEntityDao groupUserEntityDao = GreenDaoManager.getInstance().getSession().getGroupUserEntityDao();
                GroupUserEntity groupUserEntity = groupUserEntityDao.queryBuilder()
                        .where(GroupUserEntityDao.Properties.TalkId.eq(entityTemp.getTalkId()), GroupUserEntityDao.Properties.UserId.eq(entityTemp.getUserId()))
                        .limit(1)
                        .unique();
                if(groupUserEntity == null){
                    groupUserEntity = new GroupUserEntity();
                    groupUserEntity.setId(null);
                    groupUserEntity.setTalkId(entityTemp.getTalkId());
                    groupUserEntity.setUserId(entityTemp.getUserId());
                    groupUserEntityDao.insertOrReplace(groupUserEntity);
                }
                //自己
                GroupUserEntity groupUserEntity1 = groupUserEntityDao.queryBuilder()
                        .where(GroupUserEntityDao.Properties.TalkId.eq(entityTemp.getTalkId()), GroupUserEntityDao.Properties.UserId.eq(PreferenceUtils.getUUid()))
                        .limit(1)
                        .unique();
                if(groupUserEntity1 == null){
                    groupUserEntity1 = new GroupUserEntity();
                    groupUserEntity1.setId(null);
                    groupUserEntity1.setTalkId(entityTemp.getTalkId());
                    groupUserEntity1.setUserId(PreferenceUtils.getUUid());
                    groupUserEntityDao.insertOrReplace(groupUserEntity1);
                }
                //用户表
                ChatUserEntityDao chatUserEntityDao = GreenDaoManager.getInstance().getSession().getChatUserEntityDao();
                ChatUserEntity userEntity = chatUserEntityDao.queryBuilder()
                        .where(ChatUserEntityDao.Properties.UserId.eq(entityTemp.getUserId()))
                        .limit(1)
                        .unique();
                if(userEntity != null ){
                    userEntity.setUserIcon(entityTemp.getUserIcon());
                    userEntity.setUserName(entityTemp.getUserName());
                }else {
                    userEntity = new ChatUserEntity();
                    userEntity.setUserIcon(entityTemp.getUserIcon());
                    userEntity.setUserName(entityTemp.getUserName());
                    userEntity.setUserId(entityTemp.getUserId());
                }
                chatUserEntityDao.insertOrReplace(userEntity);
                //自己
                ChatUserEntity userEntity1 = chatUserEntityDao.queryBuilder()
                        .where(ChatUserEntityDao.Properties.UserId.eq(PreferenceUtils.getUUid()))
                        .limit(1)
                        .unique();
                if(userEntity1 != null ){
                    userEntity1.setUserIcon(PreferenceUtils.getAuthorInfo().getHeadPath());
                    userEntity1.setUserName(PreferenceUtils.getAuthorInfo().getUserName());
                }else {
                    userEntity1 = new ChatUserEntity();
                    userEntity1.setUserIcon(PreferenceUtils.getAuthorInfo().getHeadPath());
                    userEntity1.setUserName(PreferenceUtils.getAuthorInfo().getUserName());
                    userEntity1.setUserId(PreferenceUtils.getUUid());
                }
                chatUserEntityDao.insertOrReplace(userEntity1);
                RxBus.getInstance().post(new PrivateMessageEvent(true,entityTemp.getTalkId(),false));
                RxBus.getInstance().post(new ChatEvent(chatContentDbEntity));
            }
            if (info.showNotify){
                showNotification(context,info,entityTemp.getTalkId());
            }
        }
    }

    private void showNotification(Context context, final ReceiverInfo info,String talkId){
        if(!TextUtils.isEmpty(info.messageType)) {
            PreferenceUtils.setMessageDot(context,info.messageType,true);
            RxBus.getInstance().post(new SystemMessageEvent(info.messageType));
        }
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
            if(!AppSetting.sCurChatId.equals(talkId) || TextUtils.isEmpty(talkId)) {
                resultIntent = IntentUtils.getIntentFromUri(context, Uri.parse(info.schema));
            }
        } else {
            resultIntent = new Intent(context,
                    SplashActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            resultIntent.putExtra("schema", info.schema);
        }
        if(resultIntent == null) resultIntent = new Intent();
        final int id;
        if(info.type.equals("TALK")){
            id = info.id;
        }else {
            id = notificationId++;
        }
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                context, id, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (TextUtils.isEmpty(info.logoUrl)) {
            mBuilder.setLargeIcon(btm);
            mNotificationManager.notify(id, mBuilder.build());
        } else {
            Glide.with(context)
                    .load(info.logoUrl)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            Bitmap bitmap = drawableToBitamp(resource);
                            mBuilder.setLargeIcon(bitmap);
                            mNotificationManager.notify(id, mBuilder.build());
                        }
                    });
        }
    }

    private void showNotification(Context context, final ReceiverInfo info) {
        showNotification(context, info,"");
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
