// IMediaService.aidl
package com.moemoe.lalala;

// Declare any non-default types here with import statements
import com.moemoe.lalala.data.MusicInfo;

interface IMediaService {

    boolean play(int pos);
    boolean rePlay();
    boolean pause();
    boolean prev();
    boolean next();
    int duration();
    int position();
    boolean seekTo(int progress);
    int getPlayState();
    int getPlayMode();
    void setPlayMode(int mode);
    void sendPlayStateBroadcast();
    void exit();
    int getCurMusicId();
    MusicInfo getCurMusic();
    void refreshMusicList(in List<MusicInfo> musicList);
    void getMusicList(out List<MusicInfo> musicList);
    int getBufferProgress();
    void clearMusicList();
    MusicInfo findMusicInfoByUrl(String url);
    void addMusicInfo(in MusicInfo musicInfo);
    boolean playByUrl(String url);
}
