package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.GroupNoticeEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface PhoneMsgListContract {
    interface Presenter extends BasePresenter{
        void loadMsgList(int index);
        void responseNotice(boolean res, String id,int position);
    }

    interface View extends BaseView{
        void onLoadMsgListSuccess(ArrayList<GroupNoticeEntity> entities, boolean isPull);
        void onResponseSuccess(int position);
    }
}
