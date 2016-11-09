package com.moemoe.lalala.netamusic.player;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.app.common.util.DensityUtil;
import com.moemoe.lalala.MapActivity;
import com.moemoe.lalala.R;
import com.moemoe.lalala.netamusic.data.model.PlayList;
import com.moemoe.lalala.netamusic.data.model.Song;
import com.moemoe.lalala.utils.StringUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by yi on 2016/10/31.
 */

public class PlaybackService extends Service implements IPlayBack ,IPlayBack.Callback{

    private static final String ACTION_PLAY_TOGGLE = "com.moemoe.lalala.ACTION_PLAY_TOGGLE";
    private static final String ACTION_PLAY_LAST = "com.moemoe.lalala.ACTION_PLAY_LAST";
    private static final String ACTION_PLAY_NEXT = "com.moemoe.lalala.ACTION_PLAY_NEXT";
    private static final String ACTION_STOP_SERVICE = "com.moemoe.lalala.ACTION_PLAY_SERVICE";

    private static final int NOTIFICATION_ID = 1;

    private RemoteViews mContentViewBig,mContentViewSmall;

    private Player mPlayer;

    private final Binder mBinder = new LocalBinder();

    public class LocalBinder extends Binder{
        public PlaybackService getService(){
            return PlaybackService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = Player.getInstance();
        mPlayer.registerCallback(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if(ACTION_PLAY_TOGGLE.equals(action)){
            if(isPlaying()){
                pause();
            }else {
                play();
            }
        }else if(ACTION_PLAY_NEXT.equals(action)){
            playNext();
        }else if(ACTION_PLAY_LAST.equals(action)){
            playLast();
        }else if(ACTION_STOP_SERVICE.equals(action)){
            if(isPlaying()){
                pause();
            }
            stopForeground(true);
            unregisterCallback(this);
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean stopService(Intent name) {
        stopForeground(true);
        unregisterCallback(this);
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }

    @Override
    public void setPlayList(PlayList list) {
        mPlayer.setPlayList(list);
    }

    @Override
    public boolean play() {
        return mPlayer.play();
    }

    @Override
    public boolean play(PlayList list) {
        return mPlayer.play(list);
    }

    @Override
    public boolean play(PlayList list, int startIndex) {
        return mPlayer.play(list,startIndex);
    }

    @Override
    public boolean play(Song song) {
        return mPlayer.play(song);
    }

    @Override
    public boolean playLast() {
        return mPlayer.playLast();
    }

    @Override
    public boolean playNext() {
        return mPlayer.playNext();
    }

    @Override
    public boolean pause() {
        return mPlayer.pause();
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    @Override
    public int getProgress() {
        return mPlayer.getProgress();
    }

    @Override
    public Song getPlayingSong() {
        return mPlayer.getPlayingSong();
    }

    @Override
    public boolean seekTo(int progress) {
        return mPlayer.seekTo(progress);
    }

    @Override
    public void setPlayMode(PlayMode playMode) {
        mPlayer.setPlayMode(playMode);
    }

    @Override
    public void registerCallback(Callback callback) {
        mPlayer.registerCallback(callback);
    }

    @Override
    public void unregisterCallback(Callback callback) {
        mPlayer.unregisterCallback(callback);
    }

    @Override
    public void removeCallbacks() {
        mPlayer.removeCallbacks();
    }

    @Override
    public void releasePlayer() {
        mPlayer.releasePlayer();
        super.onDestroy();
    }

    @Override
    public void onSwitchLast(@Nullable Song last) {
        showNotification();
    }

    @Override
    public void onSwitchNext(@Nullable Song next) {
        showNotification();
    }

    @Override
    public void onComplete(@Nullable Song next) {
        showNotification();
    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        showNotification();
    }

    private void showNotification(){
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MapActivity.class), 0);
        // Set the info for the views that show in the notification panel.
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)  // the status icon
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .setCustomContentView(getSmallContentView())
                .setCustomBigContentView(getBigContentView())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .build();

        // Send the notification.
        startForeground(NOTIFICATION_ID, notification);
    }

    private RemoteViews getSmallContentView(){
        if(mContentViewSmall == null){
            mContentViewSmall = new RemoteViews(getPackageName(),R.layout.remote_view_music_player_small);
            setUpRemoteView(mContentViewSmall);
        }
        updateRemoteViews(mContentViewSmall);
        return mContentViewSmall;
    }

    private RemoteViews getBigContentView() {
        if (mContentViewBig == null) {
            mContentViewBig = new RemoteViews(getPackageName(), R.layout.remote_view_music_player);
            setUpRemoteView(mContentViewBig);
        }
        updateRemoteViews(mContentViewBig);
        return mContentViewBig;
    }

    private void setUpRemoteView(RemoteViews remoteView) {
        remoteView.setImageViewResource(R.id.image_view_close, R.drawable.ic_remote_view_close);
       /// remoteView.setImageViewResource(R.id.image_view_play_last, R.drawable.ic_remote_view_play_last);
       // remoteView.setImageViewResource(R.id.image_view_play_next, R.drawable.ic_remote_view_play_next);

        remoteView.setOnClickPendingIntent(R.id.button_close, getPendingIntent(ACTION_STOP_SERVICE));
        //remoteView.setOnClickPendingIntent(R.id.button_play_last, getPendingIntent(ACTION_PLAY_LAST));
        //remoteView.setOnClickPendingIntent(R.id.button_play_next, getPendingIntent(ACTION_PLAY_NEXT));
        remoteView.setOnClickPendingIntent(R.id.button_play_toggle, getPendingIntent(ACTION_PLAY_TOGGLE));
    }

    private void updateRemoteViews(final RemoteViews remoteView) {
        Song currentSong = mPlayer.getPlayingSong();
        if (currentSong != null) {
            remoteView.setTextViewText(R.id.text_view_name, currentSong.getDisplayName());
            remoteView.setTextViewText(R.id.text_view_artist, currentSong.getArtist());
        }
        remoteView.setImageViewResource(R.id.image_view_play_toggle, isPlaying()
                ? R.drawable.ic_remote_view_pause : R.drawable.ic_remote_view_play);
        //TODO yi 歌曲图片
        Picasso.with(this)
                .load(StringUtils.getUrl(this,currentSong.getCoverPath(), DensityUtil.dip2px(100), DensityUtil.dip2px(100), false, true))
                .resize(DensityUtil.dip2px(100), DensityUtil.dip2px(100))
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .config(Bitmap.Config.RGB_565)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        remoteView.setImageViewBitmap(R.id.image_view_album, bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
        //Bitmap album = AlbumUtils.parseAlbum(getPlayingSong());
//        if (album == null) {
//            remoteView.setImageViewResource(R.id.image_view_album, R.mipmap.ic_launcher);
//        } else {
//            remoteView.setImageViewBitmap(R.id.image_view_album, album);
//        }
    }

    private PendingIntent getPendingIntent(String action) {
        return PendingIntent.getService(this, 0, new Intent(action), 0);
    }
}
