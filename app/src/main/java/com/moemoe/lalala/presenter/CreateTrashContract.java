package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.TrashPut;

/**
 * Created by yi on 2016/12/13.
 */

public interface CreateTrashContract {
    interface Presenter extends BasePresenter{
        void createTrash(TrashPut put);
        void createUploadTrash(TrashPut put,String path);
    }

    interface View extends BaseView{
        void onCreateSuccess();
    }
}
