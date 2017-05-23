package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.ChatContentEntity;
import com.moemoe.lalala.model.entity.SendPrivateMsgEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface ChatContract {
    interface Presenter extends BasePresenter{
        void loadTalkHistory(String talkId);
        void findTalk(String talkId,String startTime,String endTime);
        void sendMsg(SendPrivateMsgEntity entity);
    }

    interface View extends BaseView{
        void loadOrFindTalkHistorySuccess(ArrayList<ChatContentEntity> entities);
        void sendMsgSuccess(String id);
        void loadOrFindTalkFailure();
    }
}
