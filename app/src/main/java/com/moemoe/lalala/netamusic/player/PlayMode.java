package com.moemoe.lalala.netamusic.player;

/**
 * Created by yi on 2016/10/31.
 */

public enum PlayMode {
    SINGLE,
    LOOP,
    LIST,
    SHUFFLE;

    public static PlayMode getDefault(){ return SINGLE;}

    public static PlayMode switchNextMode(PlayMode current){
        if (current == null) return getDefault();
        switch (current){
            case LOOP:
                return LIST;
            case LIST:
                return SHUFFLE;
            case SHUFFLE:
                return SINGLE;
            case SINGLE:
                return LOOP;
        }
        return getDefault();
    }
}
