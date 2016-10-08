package com.moemoe.lalala.music;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;

import com.app.common.Callback;
import com.app.common.util.LogUtil;
import com.moemoe.lalala.R;
import com.moemoe.lalala.data.MusicInfo;
import com.moemoe.lalala.download.DownloadInfo;
import com.moemoe.lalala.download.DownloadManager;
import com.moemoe.lalala.download.DownloadService;
import com.moemoe.lalala.download.DownloadViewHolder;
import com.moemoe.lalala.utils.IConstants;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Haru on 2016/3/22 0022.
 */
public class MusicControl implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener,MediaPlayer.OnBufferingUpdateListener,IConstants{

    private String TAG = MusicControl.class.getSimpleName();
    private int mMaxMusicSize = 10;

    private MediaPlayer mMediaPlayer;
    private int mPlayMode;
    private List<MusicInfo> mMusicList = new ArrayList<>();
    private int mPlayState;
    private int mCurPlayIndex;
    private String mPrePlayUrl;
    private int mPrePlayPosition;
    private Context mContext;
    private Random mRandom;
    private int mCurMusicId;
    private MusicInfo mCurMusic;
    private DownloadInfo mDownloadInfo;
    private int mBufferProgress;
    private DownloadManager mDownloadManager;
    private boolean mIsPrepared = false;

    public MusicControl(Context context){
        mDownloadManager = DownloadService.getDownloadManager();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mPlayMode = MPM_SINGLE_PLAY;
        mPlayState = MPS_NOFILE;
        mCurPlayIndex = -1;
        mPrePlayPosition = -1;
        mCurMusicId = -1;
        mContext = context;
        mRandom = new Random();
        mRandom.setSeed(System.currentTimeMillis());
    }

    public boolean play(int pos){
        if(mCurPlayIndex == pos){
            if( mPlayState == MPS_NOFILE ||  mPlayState == MPS_INVALID){
                reset();
                return false;
            }
            if(!mMediaPlayer.isPlaying() && mPlayState != MPS_PREPARE){
                mMediaPlayer.start();
                mPlayState = MPS_PLAYING;
                sendBroadCast();
                return true;
            }else{
                return pause();
            }
        }else if(mCurPlayIndex != -1){
            mCurMusic.playState = MPS_NOFILE;
            mPrePlayUrl = mCurMusic.url;
            mPrePlayPosition = mCurMusic.position;
        }
        if(!prepare(pos)){
            return false;
        }
        return true;
    }

    public boolean playByUrl(String url){
        MusicInfo info = findMusicInfoByUrl(url);
        return play(info.songId);
    }

    public boolean replay(){
        if(mPlayState == MPS_INVALID || mPlayState == MPS_NOFILE){
            return false;
        }
        mMediaPlayer.start();
        mPlayState = MPS_PLAYING;
        sendBroadCast();
        return true;
    }

    public boolean pause(){
        if(mPlayState != MPS_PLAYING){
            reset();
            return false;
        }
        mMediaPlayer.pause();
        mPlayState = MPS_PAUSE;
        sendBroadCast();
        return true;
    }

    private void reset(){
        mMediaPlayer.reset();
        mPlayState = MPS_RESET;
        sendBroadCast();
        mCurPlayIndex = -1;
        mCurMusicId = -1;
    }

    public boolean prev(){
        if(mPlayState == MPS_NOFILE){
            return false;
        }
        mCurPlayIndex--;
        mCurPlayIndex = reviseIndex(mCurPlayIndex);
        if(!prepare(mCurPlayIndex)){
            return false;
        }
        return replay();
    }

    public boolean next(){
        if(mPlayState == MPS_NOFILE){
            return false;
        }
        mCurPlayIndex++;
        mCurPlayIndex = reviseIndex(mCurPlayIndex);
        if(!prepare(mCurPlayIndex)){
            return false;
        }
        return replay();
    }

    private int reviseIndex(int index){
        if(index < 0){
            index = mMusicList.size() - 1;
        }
        if(index >= mMusicList.size()){
            index = 0;
        }
        return index;
    }

    public int position(){
        if(mPlayState == MPS_PLAYING || mPlayState == MPS_PAUSE){
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int duration(){
        if(mPlayState == MPS_PLAYING || mPlayState == MPS_PAUSE){
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    public boolean seekTo(int progress){
        if(mPlayState == MPS_INVALID || mPlayState == MPS_NOFILE){
            return false;
        }
        mMediaPlayer.seekTo(progress);
        return true;
    }

    private int reviseSeekValue(int progress){
        if(progress < 0){
            progress = 0;
        }else if(progress > 100){
            progress = 100;
        }
        return progress;
    }

    public void refreshMusicList(List<MusicInfo> musicInfos){
        mMusicList.clear();
        mMusicList.addAll(musicInfos);
        if(mMusicList.size() == 0){
            mPlayState = MPS_NOFILE;
            mCurPlayIndex = -1;
            return;
        }
    }

    public MusicInfo findMusicInfoByUrl(String url){
        for(MusicInfo info : mMusicList){
            if(info.url.equals(url)){
                return info;
            }
        }
        return null;
    }

    public void addMusicInfo(MusicInfo musicInfo){
        if(!mMusicList.contains(musicInfo)){
            musicInfo.songId = mMusicList.size();
            mMusicList.add(musicInfo);
            if(mMusicList.size() > mMaxMusicSize){
                MusicInfo temp = mMusicList.get(mMusicList.size() - 1);
                if(temp == mCurMusic){
                    mMusicList.remove(mMusicList.size() - 2);
                }else {
                    mMusicList.remove(mMusicList.size() - 1);
                }
            }
        }
        if(mMusicList.size() == 0){
            mPlayState = MPS_NOFILE;
            mCurPlayIndex = -1;
            return;
        }
    }

    public void clearMusicList(){
        mMusicList.clear();
    }

    private boolean prepare(final int pos){
        mCurPlayIndex = pos;
        mMediaPlayer.reset();
        String path = mMusicList.get(pos).path;
        mIsPrepared = false;
        if(path != null && !path.equals("")){
            try {
                mMediaPlayer.setDataSource(path);
                mPlayState = MPS_PREPARE;
                mMediaPlayer.prepareAsync();
            }catch (Exception e){
                LogUtil.e("", e);
                return false;
            }
        }else{
            if(!NetworkUtils.isNetworkAvailable(mContext)){
                ToastUtil.showCenterToast(mContext, R.string.msg_server_connection);
                reset();
                return false;
            }
            String url = mMusicList.get(pos).url;
            final String suffix = url.substring(url.lastIndexOf("."));
            try {
                if(StorageUtils.isMusicExit(mMusicList.get(pos).musicName + suffix)){
                    mMediaPlayer.setDataSource(StorageUtils.getMusicPath(mMusicList.get(pos).musicName + suffix));
                }else{
                    mMediaPlayer.setDataSource(url);
                    if(!TextUtils.isEmpty(mPrePlayUrl)){
                        mDownloadManager.removeDownload(mCurMusic.url);
                    }
                    StorageUtils.deleteMusic();
                    mDownloadInfo = new DownloadInfo();
                    mDownloadInfo.setUrl(url);
                    mDownloadInfo.setFileSavePath(StorageUtils.getMusicPath(mMusicList.get(pos).musicName + suffix));
                    mDownloadInfo.setId(mMusicList.get(pos).position);
                    mDownloadInfo.setLabel(mMusicList.get(pos).musicName + suffix);
                    mDownloadInfo.setAutoRename(false);
                    mDownloadInfo.setAutoResume(true);
                    mDownloadManager.startDownload(mDownloadInfo, new DownloadViewHolder(null,mDownloadInfo) {
                        @Override
                        public void onWaiting() {
                        }

                        @Override
                        public void onStarted() {
                        }

                        @Override
                        public void onLoading(long total, long current) {
                            if(!NetworkUtils.isNetworkAvailable(mContext)){
                                mDownloadManager.removeDownload(mDownloadInfo.getUrl());
                                return;
                            }
                        }

                        @Override
                        public void onSuccess(File result) {
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            StorageUtils.deleteMusic();
                        }

                        @Override
                        public void onCancelled(Callback.CancelledException cex) {
                            StorageUtils.deleteMusic();
                        }
                    });
                }
                mPlayState = MPS_PREPARE;
                mMediaPlayer.prepareAsync();
            }catch (Exception e){
                LogUtil.e("", e);
                e.printStackTrace();
                StorageUtils.deleteMusic();
               // prepare(pos);
                return false;
            }
        }
        sendBroadCast();
        return true;
    }

    public void sendBroadCast(){
        Intent intent = new Intent(BROADCAST_NAME);
        intent.putExtra(PLAY_STATE_NAME,mPlayState);
        intent.putExtra(PLAY_MUSIC_INDEX, mCurPlayIndex);
        intent.putExtra(PLAY_PRE_MUSIC_POSITION,mPrePlayPosition);
        intent.putExtra(MUSIC_NUM, mMusicList.size());
        if(mMusicList.size() > 0 && mCurPlayIndex != -1){
            Bundle bundle = new Bundle();
            mCurMusic = mMusicList.get(mCurPlayIndex);
            mCurMusic.playState = mPlayState;
            mCurMusicId = mCurMusic.songId;
            bundle.putParcelable(MusicInfo.KEY_MUSIC,mCurMusic);
            intent.putExtra(MusicInfo.KEY_MUSIC, bundle);
        }
        mContext.sendBroadcast(intent);
        mPrePlayUrl = "";
        mPrePlayPosition = -1;
    }

    public int getCurMusicId(){
        return mCurMusicId;
    }

    public MusicInfo getCurMusic(){
        return mCurMusic;
    }

    public int getPlayState(){
        return mPlayState;
    }

    public int getPlayMode(){
        return mPlayMode;
    }

    public void setPlayMode(int mode){
        switch (mode){
            case MPM_LIST_LOOP_PLAY:
            case MPM_ORDER_PLAY:
            case MPM_RANDOM_PLAY:
            case MPM_SINGLE_LOOP_PLAY:
                mPlayMode = mode;
                break;
        }
    }

    public List<MusicInfo> getMusicList(){
        return mMusicList;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mIsPrepared = true;
        replay();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        switch (mPlayMode){
            case MPM_LIST_LOOP_PLAY:
                next();
                break;
            case MPM_ORDER_PLAY:
                if(mCurPlayIndex != mMusicList.size() - 1){
                    next();
                }else{
                    //prepare(mCurPlayIndex);
                    stop();
                }
                break;
            case MPM_RANDOM_PLAY:
                int index = getRandomIndex();
                if(index != -1){
                    mCurPlayIndex = index;
                }else{
                    //mCurPlayIndex = 0;
                    stop();
                }
                if(prepare(mCurPlayIndex)){
                    replay();
                }
                break;
            case MPM_SINGLE_LOOP_PLAY:
                play(mCurPlayIndex);
                break;
            case MPM_SINGLE_PLAY:
                stop();
                //reset();
                break;
        }
    }

    private int getRandomIndex(){
        int size = mMusicList.size();
        if(size == 0){
            return -1;
        }
        return Math.abs(mRandom.nextInt() % size);
    }

    public void stop(){
        if(mPlayState == MPS_PLAYING){
            try {
                mMediaPlayer.stop();
                mPlayState = MPS_NOFILE;
                sendBroadCast();
                mCurPlayIndex = -1;
                mCurMusicId = -1;
            }catch (Exception e){
                reset();
            }
        }
    }

    public void exit(){
        stop();
        if(mDownloadInfo != null){
            if(mDownloadInfo.getProgress() != mDownloadInfo.getFileLength()){
                StorageUtils.deleteMusic();
            }
        }
        mMediaPlayer.release();
        mMusicList.clear();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        if(!NetworkUtils.isNetworkAvailable(mContext)){
            ToastUtil.showCenterToast(mContext, R.string.msg_server_connection);
        }
        if (null != mMediaPlayer) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mMediaPlayer = new MediaPlayer();
        mPlayState = MPS_NOFILE;
        sendBroadCast();
        mCurPlayIndex = -1;
        mCurMusicId = -1;
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        int pro = reviseSeekValue(percent);
        int time = mMediaPlayer.getDuration();
        mBufferProgress = (int) ((float)pro/100*time);
    }


    public int getBufferProgress(){
        return mBufferProgress;
    }
}
