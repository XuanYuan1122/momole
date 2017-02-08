package com.moemoe.lalala.netamusic.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.moemoe.lalala.netamusic.player.PlayMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by yi on 2016/10/31.
 */

public class PlayList implements Parcelable {

    public static final int NO_POSITION = -1;

    private int id;
    private String name;
    private int numOfSongs;
    private boolean favorite;
    private List<Song> songs = new ArrayList<>();
    private int playingIndex = -1;
    private PlayMode playMode = PlayMode.SINGLE;

    public PlayList() {
        // EMPTY
    }

    public PlayList(Song song) {
        songs.add(song);
        numOfSongs = 1;
    }

    protected PlayList(Parcel in) {
        readFromParcel(in);
    }

    public boolean prepare(){
        if(songs.isEmpty()) return false;
        if(playingIndex == NO_POSITION){
            playingIndex = 0;
        }
        return true;
    }

    public Song getCurrentSong(){
        if(playingIndex != NO_POSITION){
            return songs.get(playingIndex);
        }
        return null;
    }

    public boolean hasLast(){
        return songs != null && songs.size() != 0;
    }

    public Song last(){
        switch (playMode){
            case LOOP:
            case LIST:
            case SINGLE:
                int newIndex = playingIndex - 1;
                if(newIndex < 0){
                    newIndex = songs.size() - 1;
                }
                playingIndex = newIndex;
                break;
            case SHUFFLE:
                playingIndex = randomPlayIndex();
                break;
        }
        return songs.get(playingIndex);
    }

    public boolean hasNext(boolean fromComplete){
        if(songs.isEmpty()) return false;
        if(fromComplete){
            if(playMode == PlayMode.LIST && playingIndex + 1 >= songs.size()) return false;
        }
        return true;
    }

    public Song next(){
        switch (playMode){
            case LOOP:
            case LIST:
            case SINGLE:
                int newIndex = playingIndex + 1;
                if(newIndex > songs.size()){
                    newIndex = 0;
                }
                playingIndex = newIndex;
                break;
            case SHUFFLE:
                playingIndex = randomPlayIndex();
                break;
        }
        return songs.get(playingIndex);
    }

    @NonNull
    public List<Song> getSongs(){
        if(songs == null){
            songs = new ArrayList<>();
        }
        return songs;
    }

    public void setSongs(@Nullable List<Song> songs){
        if(songs == null){
            songs = new ArrayList<>();
        }
        this.songs = songs;
    }

    public int getItemCount() {
        return songs == null ? 0 : songs.size();
    }

    public void addSong(@Nullable Song song) {
        if (song == null) return;

        songs.add(song);
        numOfSongs = songs.size();
    }

    public void addSong(@Nullable Song song, int index) {
        if (song == null) return;

        songs.add(index, song);
        numOfSongs = songs.size();
    }

    public void addSong(@Nullable List<Song> songs, int index) {
        if (songs == null || songs.isEmpty()) return;

        this.songs.addAll(index, songs);
        this.numOfSongs = this.songs.size();
    }

    public boolean removeSong(Song song) {
        if (song == null) return false;

        int index;
        if ((index = songs.indexOf(song)) != -1) {
            if (songs.remove(index) != null) {
                numOfSongs = songs.size();
                return true;
            }
        } else {
            for (Iterator<Song> iterator = songs.iterator(); iterator.hasNext(); ) {
                Song item = iterator.next();
                if (song.getPath().equals(item.getPath())) {
                    iterator.remove();
                    numOfSongs = songs.size();
                    return true;
                }
            }
        }
        return false;
    }

    public int getNumOfSongs(){ return numOfSongs;}

    public void setPlayingIndex(int playingIndex){ this.playingIndex = playingIndex;}

    public int getPlayingIndex(){ return playingIndex;}

    public PlayMode getPlayMode(){ return playMode;}

    public void setPlayMode(PlayMode playMode){ this.playMode = playMode;}

    public static final Creator<PlayList> CREATOR = new Creator<PlayList>() {
        @Override
        public PlayList createFromParcel(Parcel source) {
            return new PlayList(source);
        }

        @Override
        public PlayList[] newArray(int size) {
            return new PlayList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public void readFromParcel(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.numOfSongs = in.readInt();
        this.favorite = in.readByte() != 0;
        this.songs = in.createTypedArrayList(Song.CREATOR);
        this.playingIndex = in.readInt();
        int tmpPlayMode = in.readInt();
        this.playMode = tmpPlayMode == -1 ? null : PlayMode.values()[tmpPlayMode];
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.numOfSongs);
        dest.writeByte(this.favorite ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.songs);
        dest.writeInt(this.playingIndex);
        dest.writeInt(this.playMode == null ? -1 : this.playMode.ordinal());
    }

    private int randomPlayIndex(){
        int randomIndex = new Random().nextInt(songs.size());
        if(songs.size() > 1 && randomIndex == playingIndex){
            randomPlayIndex();
        }
        return randomIndex;
    }
}
