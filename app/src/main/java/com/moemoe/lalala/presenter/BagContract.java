package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.BagDirEntity;
import com.moemoe.lalala.model.entity.BagEntity;
import com.moemoe.lalala.model.entity.FileEntity;
import com.moemoe.lalala.model.entity.Image;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface BagContract {
    interface Presenter extends BasePresenter{
        void openBag(String name,Image image,int type);
        void getBagInfo(String userId);
        void getFolderList(String userId,int index);
        void createFolder(String folderName,int coin,Image cover,ArrayList<Object> items,String readType);
        void modifyFolder(String folderId,String folderName,int coin,Image cover,long size,String readType);
        void uploadFilesToFolder(String folderId,ArrayList<Object> items);
        void getFolderItemList(String folderId,int index);
        void checkSize(long size);
        void buyFolder(String folderId);
        void deleteFolder(ArrayList<String> ids);
        void followFolder(String folderId);
        void unFollowFolder(String folderId);
        void getFolder(String userId,String folderId);
    }

    interface View extends BaseView{
        void openOrModifyBagSuccess();
        void loadBagInfoSuccess(BagEntity entity);
        void loadFolderListSuccess(ArrayList<BagDirEntity> entities,boolean isPull);
        void createFolderSuccess();
        void uploadFolderSuccess();
        void loadFolderItemListSuccess(ArrayList<FileEntity> entities, boolean isPull);
        void onCheckSize(boolean isOk);
        void onBuyFolderSuccess();
        void deleteFolderSuccess();
        void modifyFolderSuccess();
        void onFollowOrUnFollowFolderSuccess(boolean follow);
        void onLoadFolderSuccess(BagDirEntity entity);
        void onLoadFolderFail();
    }
}
