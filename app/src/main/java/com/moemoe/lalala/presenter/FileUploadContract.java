package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.entity.AddressEntity;

import java.util.ArrayList;

/**
 * Created by yi on 2016/11/29.
 */

public interface FileUploadContract {
    interface Presenter extends BasePresenter{
        void checkSize(long size);
        void uploadFiles(String folderType,String folderId,String parentFolderId,String name,ArrayList<Object> items,String cover,int coverSize);
        void createMh(String folderId, String parentFolderId, String name, ArrayList<Object> items);
    }

    interface View extends BaseView{
        void onCheckSize(boolean isOk);
        void onUploadFilesSuccess();
    }
}
