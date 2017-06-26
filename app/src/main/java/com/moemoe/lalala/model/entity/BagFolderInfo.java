package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 * Created by yi on 2017/1/19.
 */

public class BagFolderInfo {

    public ArrayList<UploadResultEntity> files;
    public FolderInfo folderInfo;

    public static class FolderInfo{
        public int coin;
        public String cover;
        public String name;
        public long size;
        public String readType;

        public FolderInfo(int coin, String cover, String name,long size,String readType) {
            this.coin = coin;
            this.cover = cover;
            this.name = name;
            this.size = size;
            this.readType = readType;
        }

        public FolderInfo(){

        }
    }

    public ArrayList<UploadResultEntity> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<UploadResultEntity> files) {
        this.files = files;
    }

    public FolderInfo getFolderInfo() {
        return folderInfo;
    }

    public void setFolderInfo(FolderInfo folderInfo) {
        this.folderInfo = folderInfo;
    }
}
