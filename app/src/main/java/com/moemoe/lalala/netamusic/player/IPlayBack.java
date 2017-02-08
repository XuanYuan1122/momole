package com.moemoe.lalala.netamusic.player;

import android.support.annotation.Nullable;

import com.moemoe.lalala.netamusic.data.model.PlayList;
import com.moemoe.lalala.netamusic.data.model.Song;

/**
 * Created by yi on 2016/10/31.
 */

public interface IPlayBack {
    void setPlayList(PlayList list);

    boolean play();

    boolean play(PlayList list);

    boolean play(PlayList list, int startIndex);

    boolean play(Song song);

    boolean playLast();

    boolean playNext();

    boolean pause();

    boolean isPlaying();

    int getProgress();

    Song getPlayingSong();

    boolean seekTo(int progress);

    void setPlayMode(PlayMode playMode);

    void registerCallback(Callback callback);

    void unregisterCallback(Callback callback);

    void removeCallbacks();

    void releasePlayer();

    interface Callback{
        void onSwitchLast(@Nullable Song last);

        void onSwitchNext(@Nullable Song next);

        void onComplete(@Nullable Song next);

        void onPlayStatusChanged(boolean isPlaying);
    }
}
