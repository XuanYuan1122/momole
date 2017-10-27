package com.moemoe.lalala.model.entity;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by yi on 2017/10/11.
 */
@Entity
public class DownloadEntity {

    @Id
    private Long id;
    private String url;
    private String path;
    private String fileName;
    private String dirPath;
    private String type;

    @Generated(hash = 56598491)
    public DownloadEntity(Long id, String url, String path, String fileName,
            String dirPath, String type) {
        this.id = id;
        this.url = url;
        this.path = path;
        this.fileName = fileName;
        this.dirPath = dirPath;
        this.type = type;
    }

    @Generated(hash = 1671715506)
    public DownloadEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
