package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.MoveFileEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface FilesContract {
    interface Presenter extends BasePresenter{
        void deleteFiles(String folderId, String folderType,ArrayList<String> ids);
        void moveFiles(String folderId, MoveFileEntity entity);
        void modifyFile(String type,String folderId,String fileId,String fileName);
        void copyFile(String folderId,String fileId,String myFolderId);
    }

    interface View extends BaseView{
        void deleteFilesSuccess();
        void moveFilesSuccess();
        void modifyFileSuccess(String name);
        void copyFileSuccess();
    }
}
