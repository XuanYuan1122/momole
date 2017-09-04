package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 * Created by yi on 2017/8/23.
 */

public class ManHuaUploadEntity {
    public String cover;
    public int coverSize;
    public ArrayList<UploadResultEntity> files;
    public String folderName;

    public ManHuaUploadEntity(String cover, int coverSize, ArrayList<UploadResultEntity> files, String folderName) {
        this.cover = cover;
        this.coverSize = coverSize;
        this.files = files;
        this.folderName = folderName;
    }
}
