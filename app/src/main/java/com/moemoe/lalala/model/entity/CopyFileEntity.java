package com.moemoe.lalala.model.entity;

/**
 * Created by yi on 2017/8/24.
 */

public class CopyFileEntity {
    public String fileId;
    public String folderId;
    public String myFolderId;

    public CopyFileEntity(String fileId, String folderId, String myFolderId) {
        this.fileId = fileId;
        this.folderId = folderId;
        this.myFolderId = myFolderId;
    }
}
