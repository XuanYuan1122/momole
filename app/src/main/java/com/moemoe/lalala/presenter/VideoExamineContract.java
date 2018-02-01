package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.StreamFileEntity;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2016/11/29.
 */

public interface VideoExamineContract {
    interface Presenter extends BasePresenter{
        void loadExamineList(int index);
    }

    interface View extends BaseView{
        void onLoadExamineListSuccess(ArrayList<StreamFileEntity> entities,boolean isPull);
    }
}
