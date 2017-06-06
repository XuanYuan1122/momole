package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 * Created by yi on 2017/1/20.
 */

public class MoveFileEntity {
    public ArrayList<String> fileIds;
    public String oldFolderId;

    public MoveFileEntity(ArrayList<String> fileIds, String oldFolderId) {
        this.fileIds = fileIds;
        this.oldFolderId = oldFolderId;
    }
}
