package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.CommonFileEntity;
import com.moemoe.lalala.model.entity.ManHua2Entity;
import com.moemoe.lalala.model.entity.NewFolderEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface NewFolderItemContract {
    interface Presenter extends BasePresenter{
        void loadFolderInfo(String userId,String type,String folderId);
        void loadFileList(String userId,String type,String folderId,int index);
        void deleteFiles(ArrayList<String> ids,String type,String folderId,String parentId);
        void topFile(String folderId,String type,String fileId);
        void followFolder(String userId,String type,String folderId);
        void removeFollowFolder(String userId,String type,String folderId);
        void buyFolder(String userId,String type,String folderId);
        void followUser(String id,boolean isFollow);
        void refreshRecommend(String folderName,int page,String excludeFolderId);
    }

    interface View extends BaseView{
        void onLoadFolderSuccess(NewFolderEntity entity);
        void onLoadFileListSuccess(Object entities,boolean isPull);
        void onDeleteFilesSuccess();
        void onTopFileSuccess();
        void onFollowFolderSuccess();
        void onBuyFolderSuccess();
        void onFollowSuccess(boolean isFollow);
        void onLoadManHua2ListSuccess(ArrayList<ManHua2Entity> entities,boolean isPull);
        void onReFreshSuccess(ArrayList<ShowFolderEntity> entities);
    }
}
