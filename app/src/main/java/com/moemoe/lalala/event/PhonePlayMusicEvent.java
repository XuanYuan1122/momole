package com.moemoe.lalala.event;

import android.widget.SeekBar;

/**
 * Created by yi on 2017/9/24.
 */

public class PhonePlayMusicEvent {
    private String path;
    private int position;
    private boolean play;
    private int timestamp;
    private String type;
    private String name;

    public PhonePlayMusicEvent(String path, int position,boolean play,int timestamp,String type,String name) {
        this.path = path;
        this.position = position;
        this.play = play;
        this.timestamp = timestamp;
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
