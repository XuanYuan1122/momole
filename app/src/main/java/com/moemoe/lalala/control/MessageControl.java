package com.moemoe.lalala.control;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Haru on 2016/5/1 0001.
 */
public class MessageControl {
    private static final String TAG = "MessageControl";

    public static interface OnNewMessageListener{
        public void onNewMessageReceive(int num);
    }


    private static ArrayList<OnNewMessageListener> sListeners = new ArrayList<MessageControl.OnNewMessageListener>();

    public static void registerMessageListener(Context content, OnNewMessageListener listener){
        if(!sListeners.contains(listener)){
            sListeners.add(listener);
        }

    }

    public static void unRegisterMessageListener(Context content, OnNewMessageListener listener){
        sListeners.remove(listener);
    }

    public static void onRecevieNewMessage(int num){
        for(OnNewMessageListener l : sListeners){
            l.onNewMessageReceive(num);
        }
    }
}
