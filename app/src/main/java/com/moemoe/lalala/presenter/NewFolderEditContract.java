package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.FolderRepEntity;

/**
 * Created by yi on 2016/11/29.
 */

public interface NewFolderEditContract {
    interface Presenter extends BasePresenter{
        void addFolder(FolderRepEntity entity);
        void updateFolder(String folderId,FolderRepEntity entity);
        void checkSize(long size);
    }

    interface View extends BaseView{
        void onSuccess();
        void onCheckSize(boolean isOk);
    }
}
