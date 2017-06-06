package com.moemoe.lalala.broadcast;

import android.content.Context;
import android.content.Intent;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;

/**
 * Created by yi on 2016/12/2.
 */

public class PushIntentService extends GTIntentService {

    @Override
    public void onReceiveServicePid(Context context, int i) {

    }

    @Override
    public void onReceiveClientId(Context context, String s) {

    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage gtTransmitMessage) {
        byte[] temp = gtTransmitMessage.getPayload();
        if(temp != null){
            String result = new String(temp);
            String taskid = gtTransmitMessage.getTaskId();
            String messageid = gtTransmitMessage.getMessageId();
            Intent i = new Intent("com.moemoe.lalala.PUSH_RECEIVER");
            i.putExtra("data",result);
            // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
            PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
            sendBroadcast(i);
        }
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean b) {

    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage gtCmdMessage) {

    }
}
