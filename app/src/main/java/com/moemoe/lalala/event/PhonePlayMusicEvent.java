package com.moemoe.lalala.event;

/**
 * Created by yi on 2017/9/24.
 */

public class PhonePlayMusicEvent {
    private String path;
    private int position;
    private boolean play;
    private int timestamp;

    public PhonePlayMusicEvent(String path, int position,boolean play,int timestamp) {
        this.path = path;
        this.position = position;
        this.play = play;
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isPlay() {
        return play;
    }

    public void setPlay(boolean play) {
        this.play = play;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
