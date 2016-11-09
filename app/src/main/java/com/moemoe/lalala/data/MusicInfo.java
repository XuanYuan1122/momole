package com.moemoe.lalala.data;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Haru on 2016/3/21 0021.
 */
public class MusicInfo implements Parcelable {

    public final static String KEY_MUSIC = "music";
    public final static String KEY_ID = "_id";
    public final static String KEY_SONG_ID = "songid";
    public final static String KEY_ALBUM_ID = "albumid";
    public final static String KEY_DURATION = "duration";
    public final static String KEY_MUSIC_NAME = "music_name";
    public final static String KEY_ARTIST = "artist";
    public final static String KEY_PATH = "path";
    public final static String KEY_URL = "url";
    public final static String KEY_FOLDER = "folder";
    public final static String KEY_MUSIC_NAME_KEY = "music_name_key";
    public final static String KEY_ARTIST_KEY = "artist_key";
    public final static String KEY_FAVORITE = "favorite";
    public final static String KEY_POSITION = "position";
    public final static String KEY_PLAY_STATE = "play_state";
    public final static String KEY_IMG = "img";

    public int _id = -1;
    public int songId = -1;
    public int albumId = -1;
    public int duration;
    public String musicName;
    public String artist;
    public String path;
    public String url;
    public String folder;
    public String musicNameKey;
    public String artistKey;
    public String img;
    public int favorite = 0;
    public int position = -1;
    public int playState = -1;

    public static final Creator<MusicInfo> CREATOR = new Creator<MusicInfo>() {
        @Override
        public MusicInfo createFromParcel(Parcel in) {
            MusicInfo musicInfo = new MusicInfo();
            Bundle bundle = new Bundle();
            bundle = in.readBundle();
            musicInfo._id = bundle.getInt(KEY_ID);
            musicInfo.songId = bundle.getInt(KEY_SONG_ID);
            musicInfo.albumId = bundle.getInt(KEY_ALBUM_ID);
            musicInfo.duration = bundle.getInt(KEY_DURATION);
            musicInfo.musicName = bundle.getString(KEY_MUSIC_NAME);
            musicInfo.artist = bundle.getString(KEY_ARTIST);
            musicInfo.path = bundle.getString(KEY_PATH);
            musicInfo.url = bundle.getString(KEY_URL);
            musicInfo.folder = bundle.getString(KEY_FOLDER);
            musicInfo.musicNameKey = bundle.getString(KEY_MUSIC_NAME_KEY);
            musicInfo.artistKey = bundle.getString(KEY_ARTIST_KEY);
            musicInfo.favorite = bundle.getInt(KEY_FAVORITE);
            musicInfo.position = bundle.getInt(KEY_POSITION);
            musicInfo.playState = bundle.getInt(KEY_PLAY_STATE);
            musicInfo.img = bundle.getString(KEY_IMG);
            return musicInfo;
        }

        @Override
        public MusicInfo[] newArray(int size) {
            return new MusicInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ID,_id);
        bundle.putInt(KEY_SONG_ID,songId);
        bundle.putInt(KEY_ALBUM_ID,albumId);
        bundle.putInt(KEY_DURATION,duration);
        bundle.putString(KEY_MUSIC_NAME,musicName);
        bundle.putString(KEY_ARTIST,artist);
        bundle.putString(KEY_PATH,path);
        bundle.putString(KEY_URL,url);
        bundle.putString(KEY_FOLDER,folder);
        bundle.putString(KEY_MUSIC_NAME_KEY,musicNameKey);
        bundle.putString(KEY_ARTIST_KEY,artistKey);
        bundle.putInt(KEY_FAVORITE,favorite);
        bundle.putInt(KEY_POSITION,position);
        bundle.putInt(KEY_PLAY_STATE,playState);
        bundle.putString(KEY_IMG,img);
        dest.writeBundle(bundle);
    }
}
