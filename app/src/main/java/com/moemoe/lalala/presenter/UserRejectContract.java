package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.BadgeEntity;
import com.moemoe.lalala.model.entity.RejectEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface UserRejectContract {
    interface Presenter extends BasePresenter{
        void getBlackList(int index);
        void removeBlack(RejectEntity item);
    }

    interface View extends BaseView{
        void onLoadBlackList(ArrayList<RejectEntity> list, boolean isPull);
        void onRemoveBlackSuccess(RejectEntity item);
    }
}
