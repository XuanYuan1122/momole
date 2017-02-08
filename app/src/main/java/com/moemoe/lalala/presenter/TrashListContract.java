package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.TrashEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface TrashListContract {
    interface Presenter{
        void doRequest(int index, String type,int list_type);
    }

    interface View extends BaseView{
        void onSuccess(ArrayList<TrashEntity> entities,boolean pull);
    }
}
