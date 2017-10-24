package com.moemoe.lalala.model.entity;

import com.moemoe.lalala.utils.StorageUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.ArrayList;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 地图元素数据库
 * Created by yi on 2017/10/10.
 */
@Entity
public class MapDbEntity {

    @Id
    private String id; // id
    private String name;
    private String image_path;// 图片信息
    private int image_w;
    private int image_h;
    private String schema;// 跳转地址
    private int pointX;// x坐标
    private int pointY;// y坐标
    private String text;// 显示文字
    private String shows;// 时间段显示
    private String fileName;
    private int downloadState;//1.未下载 2.下载完成 3.下载失败
    private String md5;

    public MapDbEntity(MapEntity entity){
        id = entity.getId();
        image_path = entity.getImage().getPath();
        image_w = entity.getImage().getW();
        image_h = entity.getImage().getH();
        schema = entity.getSchema();
        pointX = entity.getPointX();
        pointY = entity.getPointY();
        text = entity.getText();
        shows = entity.getShows();
        name = entity.getName();
        fileName = entity.getId() + image_path.substring(image_path.lastIndexOf("."));
        md5 = entity.getMd5();
        downloadState = 1;
    }

    @Generated(hash = 67326735)
    public MapDbEntity(String id, String name, String image_path, int image_w, int image_h,
            String schema, int pointX, int pointY, String text, String shows, String fileName,
            int downloadState, String md5) {
        this.id = id;
        this.name = name;
        this.image_path = image_path;
        this.image_w = image_w;
        this.image_h = image_h;
        this.schema = schema;
        this.pointX = pointX;
        this.pointY = pointY;
        this.text = text;
        this.shows = shows;
        this.fileName = fileName;
        this.downloadState = downloadState;
        this.md5 = md5;
    }

    @Generated(hash = 921294398)
    public MapDbEntity() {
    }

    public static ArrayList<MapDbEntity> toDb(ArrayList<MapEntity> entities){
        ArrayList<MapDbEntity> res = new ArrayList<>();
        for(MapEntity entity : entities){
         MapDbEntity entity1 = new MapDbEntity(entity);
            res.add(entity1);
        }
        return res;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public int getImage_w() {
        return image_w;
    }

    public void setImage_w(int image_w) {
        this.image_w = image_w;
    }

    public int getImage_h() {
        return image_h;
    }

    public void setImage_h(int image_h) {
        this.image_h = image_h;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public int getPointX() {
        return pointX;
    }

    public void setPointX(int pointX) {
        this.pointX = pointX;
    }

    public int getPointY() {
        return pointY;
    }

    public void setPointY(int pointY) {
        this.pointY = pointY;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getShows() {
        return shows;
    }

    public void setShows(String shows) {
        this.shows = shows;
    }

    public int getDownloadState() {
        return downloadState;
    }

    public void setDownloadState(int downloadState) {
        this.downloadState = downloadState;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
