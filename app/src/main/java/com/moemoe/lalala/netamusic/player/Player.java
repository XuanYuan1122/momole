package com.moemoe.lalala.netamusic.player;

import android.content.Context;
import android.media.MediaPlayer;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.moemoe.lalala.netamusic.data.model.PlayList;
import com.moemoe.lalala.netamusic.data.model.Song;
import com.moemoe.lalala.utils.CommonUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.StorageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 音乐播放控制
 * Created by yi on 2016/10/31.
 */

public class Player implements IPlayBack,MediaPlayer.OnCompletionListener {

    private static final String TAG = "Player";

    private static volatile Player sInstance;

    private MediaPlayer mPlayer;

    private PlayList mPlayList;
    //Default size 2: for service and UI
    private List<Callback> mCallbacks = new ArrayList<>(2);
    private boolean isPaused;

    private boolean isStarted;

    private Player(Context context){
        mPlayer = new MediaPlayer();
        mPlayList = new PlayList();
        mPlayer.setOnCompletionListener(this);
    }

    public static Player getInstance(Context context){
        if(sInstance == null){
            synchronized (Player.class){
                if(sInstance == null){
                    sInstance = new Player(context);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void setPlayList(PlayList list) {
        if(list == null){
            list = new PlayList();
        }
        mPlayList = list;
    }

    @Override
    public boolean play() {
        if(isPaused){
            mPlayer.start();
            notifyPlayStatusChanged(true);
            return true;
        }
        if(mPlayList.prepare()){
            final Song song = mPlayList.getCurrentSong();
            final String url = song.getPath();
            try {
                mPlayer.reset();
                if(StorageUtils.isMusicExit(song.getDisplayName())){
                    mPlayer.setDataSource(StorageUtils.getMusicPath(song.getDisplayName()));
                    mPlayer.prepare();
                    mPlayer.start();
                    notifyPlayStatusChanged(true);
                }else {
                    mPlayer.setDataSource(url);
                    mPlayer.prepareAsync();
                    mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mPlayer.start();
                        }
                    });
                    isStarted = false;
                    FileDownloader.getImpl().create(url)
                            .setPath(StorageUtils.getMusicRootPath() + song.getDisplayName())
                            .setCallbackProgressTimes(1)
                            .setListener(new FileDownloadListener() {
                                @Override
                                protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                }

                                @Override
                                protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//                                    if(mPlayList != null && mPlayList.getCurrentSong() != null ){
//                                        double downloadSize = soFarBytes / 1024f / 1024;
//                                        double fileSize = totalBytes / 1024f / 1024;
//                                        downloadSize = CommonUtils.formatNumber(downloadSize);
//                                        fileSize = CommonUtils.formatNumber(fileSize);
//                                        int progress = (int) (downloadSize * 100 / fileSize);
//                                        if(!isStarted && progress > 3){
//                                            try {
//                                                mPlayer.setDataSource(StorageUtils.getMusicPath(song.getDisplayName()));
//                                                mPlayer.prepare();
//                                                mPlayer.start();
//                                                notifyPlayStatusChanged(true);
//                                            } catch (IOException e) {
//                                                e.printStackTrace();
//                                            }
//                                            isStarted = true;
//                                        }
//                                    }
                                }

                                @Override
                                protected void completed(BaseDownloadTask task) {

                                }

                                @Override
                                protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                }

                                @Override
                                protected void error(BaseDownloadTask task, Throwable e) {

                                }

                                @Override
                                protected void warn(BaseDownloadTask task) {

                                }
                            }).start();
                }
            }catch (IOException e){
                FileUtil.deleteFile(StorageUtils.getMusicPath(song.getDisplayName()));
                notifyPlayStatusChanged(false);
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean play(PlayList list) {
        if(list == null) return false;
        isPaused = false;
        setPlayList(list);
        return play();
    }

    @Override
    public boolean play(PlayList list, int startIndex) {
        if(list == null || startIndex < 0 || startIndex >= list.getNumOfSongs()) return false;

        isPaused = false;
        list.setPlayingIndex(startIndex);
        setPlayList(list);
        return play();
    }

    @Override
    public boolean play(Song song) {
        if(song == null) return false;

        isPaused = false;
        mPlayList.getSongs().clear();
        mPlayList.getSongs().add(song);
        return play();
    }

    @Override
    public boolean playLast() {
        isPaused = false;
        boolean hasLast = mPlayList.hasLast();
        if(hasLast){
            Song last = mPlayList.last();
            play();
            notifyPlayLast(last);
            return true;
        }
        return false;
    }

    @Override
    public boolean playNext() {
        isPaused = false;
        boolean hasNext = mPlayList.hasNext(false);
        if(hasNext){
            Song next = mPlayList.next();
            play();
            notifyPlayNext(next);
            return true;
        }
        return false;
    }

    @Override
    public boolean pause() {
        if(mPlayer != null){
            if(mPlayer.isPlaying()){
                mPlayer.pause();
                isPaused = true;
                notifyPlayStatusChanged(false);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isPlaying() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    @Override
    public int getProgress() {
        return mPlayer.getCurrentPosition();
    }

//    public int getDuration(){
//        return mPlayer.getDuration();
//    }

    @Override
    public Song getPlayingSong() {
        return mPlayList.getCurrentSong();
    }

    @Override
    public boolean seekTo(int progress) {
        if(mPlayList.getSongs().isEmpty()) return false;
        Song currentSong = mPlayList.getCurrentSong();
        if(currentSong != null){
            if(currentSong.getDuration() <= progress){
                onCompletion(mPlayer);
            }else {
                mPlayer.seekTo(progress);
            }
            return true;
        }
        return false;
    }

    @Override
    public void setPlayMode(PlayMode playMode) {
        mPlayList.setPlayMode(playMode);
    }

    @Override
    public void registerCallback(Callback callback) {
        mCallbacks.add(callback);
    }

    @Override
    public void unregisterCallback(Callback callback) {
        mCallbacks.remove(callback);
    }

    @Override
    public void removeCallbacks() {
        mCallbacks.clear();
    }

    @Override
    public void releasePlayer() {
        if(mPlayer != null){
            mPlayList = null;
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        Song next = null;
        if(mPlayList.getPlayMode() == PlayMode.LIST && mPlayList.getPlayingIndex() == mPlayList.getNumOfSongs() - 1){
            //in the end of the list
            //do nothing, just deliver the callback
        }else if (mPlayList.getPlayMode() == PlayMode.SINGLE){
           // next = mPlayList.getCurrentSong();
           // play();
        }else {
            boolean hasNext = mPlayList.hasNext(true);
            if(hasNext){
                next = mPlayList.next();
                play();
            }
        }
        notifyComplete(next);
    }

    private void notifyPlayStatusChanged(boolean isPlaying){
        for (Callback callback : mCallbacks){
            callback.onPlayStatusChanged(isPlaying);
        }
    }

    private void notifyPlayLast(Song song){
        for (Callback callback : mCallbacks){
            callback.onSwitchLast(song);
        }
    }

    private void notifyPlayNext(Song song){
        for (Callback callback : mCallbacks){
            callback.onSwitchNext(song);
        }
    }

    private void notifyComplete(Song song){
        for (Callback callback : mCallbacks){
            callback.onComplete(song);
        }
    }
}
