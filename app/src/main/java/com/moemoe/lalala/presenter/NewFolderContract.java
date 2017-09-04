package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface NewFolderContract {
    interface Presenter extends BasePresenter{
        void loadFolderList(String folderType,int index,String userId,String type);
        void deleteFolders(ArrayList<String> ids,String type);
        void topFolder(String folderId);
    }

    interface View extends BaseView{
        void onLoadFolderListSuccess(Object o, boolean isPull);
        void onDeleteFoldersSuccess();
        void onTopFolderSuccess();
    }
}
