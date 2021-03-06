package com.moemoe.lalala.galgame;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Haru on 2016/7/18 0018.
 */
public class SoundManager {
    static private SoundPool soundPool;
    static private Context context;
    static private Map<String,Integer> soundList;
    static public void init(Context c) {
        context=c;

        soundPool = new SoundPool( 150, AudioManager.STREAM_MUSIC, 0 );
        soundList=new HashMap<>();
    }

    static public void load(String path) {
        if(soundList.containsKey(path))return;

        try {
            int soundID;
            if(path.startsWith("live2d")){
                AssetFileDescriptor assetFileDescritorArticle = context.getAssets().openFd(path);
                soundID = soundPool.load(assetFileDescritorArticle, 0);
            }else {
                soundID = soundPool.load(path, 0);

            }
            soundList.put(path, soundID);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    static public void play(String name) {
        if(!soundList.containsKey(name))return;
        soundPool.play(soundList.get(name),1f,1f,1,0,1);
    }

    static public void release() {
        if(soundList != null)soundList.clear();
        if(soundPool != null)soundPool.release();
        context = null;
    }
}
