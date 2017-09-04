package com.moemoe.lalala.model.entity;

/**
 * Created by yi on 2017/8/24.
 */

public class ModifyFileEntity {
    public String fileId;
    public String fileName;
    public String folderId;

    public ModifyFileEntity(String fileId, String fileName, String folderId) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.folderId = folderId;
    }
}
