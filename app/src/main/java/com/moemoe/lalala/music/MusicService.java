package com.moemoe.lalala.music;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;

import com.moemoe.lalala.IMediaService;
import com.moemoe.lalala.data.MusicInfo;
import com.moemoe.lalala.utils.IConstants;

import java.util.List;

/**
 * Created by Haru on 2016/3/21 0021.
 */
public class MusicService extends Service implements IConstants{

    private static final String PAUSE_BROADCAST_NAME = "com.moemoe.lalala.pause.broadcast";
    private static final String NEXT_BROADCAST_NAME = "com.moemoe.lalala.next.broadcast";
    private static final String PRE_BROADCAST_NAME = "com.moemoe.lalala.pre.broadcast";
    private static final int PAUSE_FLAG = 0x1;
    private static final int NEXT_FLAG = 0x2;
    private static final int PRE_FLAG = 0x3;

    private MusicControl mMc;
    private boolean mIsPlaying;
    private ControlBroadcast mControlBroadcast;
    private MusicPlayBroadcast mPlayBroadcast;

    @Override
    public void onCreate() {
        super.onCreate();

        mMc = new MusicControl(this);
        mControlBroadcast = new ControlBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PAUSE_BROADCAST_NAME);
        filter.addAction(NEXT_BROADCAST_NAME);
        filter.addAction(PRE_BROADCAST_NAME);
        registerReceiver(mControlBroadcast, filter);

        mPlayBroadcast = new MusicPlayBroadcast();
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(BROADCAST_NAME);
        registerReceiver(mPlayBroadcast,filter1);
    }

    private final IBinder mBinder = new ServerStub();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private class ControlBroadcast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int flag = intent.getIntExtra("FLAG",-1);
            switch (flag){
                case PAUSE_FLAG:
                    mMc.pause();
                    break;
                case NEXT_FLAG:
                    mMc.next();
                    break;
                case PRE_FLAG:
                    mMc.prev();
                    break;
            }
        }
    }

    private class MusicPlayBroadcast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BROADCAST_NAME)){
                int playState = intent.getIntExtra(PLAY_STATE_NAME,MPS_NOFILE);
                switch (playState){
                    case MPS_PLAYING:
                        mIsPlaying = true;
                        break;
                    default:
                        mIsPlaying = false;
                }
            }
        }
    }

    private class ServerStub extends IMediaService.Stub{

        @Override
        public boolean play(int pos) throws RemoteException {
            return mMc.play(pos);
        }

        @Override
        public boolean rePlay() throws RemoteException {
            return mMc.replay();
        }

        @Override
        public boolean pause() throws RemoteException {
            return mMc.pause();
        }

        @Override
        public boolean prev() throws RemoteException {
            return mMc.prev();
        }

        @Override
        public boolean next() throws RemoteException {
            return mMc.next();
        }

        @Override
        public int duration() throws RemoteException {
            return mMc.duration();
        }

        @Override
        public int position() throws RemoteException {
            return mMc.position();
        }

        @Override
        public boolean seekTo(int progress) throws RemoteException {
            return mMc.seekTo(progress);
        }

        @Override
        public int getPlayState() throws RemoteException {
            return mMc.getPlayState();
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return mMc.getPlayMode();
        }

        @Override
        public void setPlayMode(int mode) throws RemoteException {
            mMc.setPlayMode(mode);
        }

        @Override
        public void sendPlayStateBroadcast() throws RemoteException {
            mMc.sendBroadCast();
        }

        @Override
        public void exit() throws RemoteException {
            stopSelf();
            mMc.exit();
        }

        @Override
        public int getCurMusicId() throws RemoteException {
            return mMc.getCurMusicId();
        }

        @Override
        public MusicInfo getCurMusic() throws RemoteException {
            return mMc.getCurMusic();
        }

        @Override
        public void refreshMusicList(List<MusicInfo> musicList) throws RemoteException {
            mMc.refreshMusicList(musicList);
        }

        @Override
        public void getMusicList(List<MusicInfo> musicList) throws RemoteException {
            List<MusicInfo> music = mMc.getMusicList();
            for(MusicInfo m : music){
                musicList.add(m);
            }
        }

        @Override
        public int getBufferProgress() throws RemoteException{
            return mMc.getBufferProgress();
        }

        @Override
        public void clearMusicList() throws RemoteException{
            mMc.clearMusicList();
        }

        @Override
        public MusicInfo findMusicInfoByUrl(String url){
            return mMc.findMusicInfoByUrl(url);
        }

        @Override
        public void addMusicInfo(MusicInfo musicInfo){
            mMc.addMusicInfo(musicInfo);
        }

        @Override
        public boolean playByUrl(String url){
            return mMc.playByUrl(url);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mControlBroadcast != null){
            unregisterReceiver(mControlBroadcast);
        }
        if(mPlayBroadcast != null){
            unregisterReceiver(mPlayBroadcast);
        }
    }
}
