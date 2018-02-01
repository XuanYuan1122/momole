package com.moemoe.lalala.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;

import com.moemoe.lalala.model.entity.VideoInfo;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2018/1/29.
 */

public class FileManager {

    private FileManager(){

    }

    /**
     * 获取本机视频列表
     * @return
     */
    public static ArrayList<VideoInfo> getVideos(ContentResolver mContentResolver) {

        ArrayList<VideoInfo> videos = new ArrayList<>();

        Cursor c = null;
        try {
            // String[] mediaColumns = { "_id", "_data", "_display_name",
            // "_size", "date_modified", "duration", "resolution" };
            c = mContentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));// 路径
                if (!FileUtil.isExists(path)) {
                    continue;
                }

                int id = c.getInt(c.getColumnIndexOrThrow(MediaStore.Video.Media._ID));// 视频的id
                String name = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)); // 视频名称
                String resolution = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION)); //分辨率
                long size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));// 大小
                long duration = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));// 时长
                long date = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED));//修改时间

                VideoInfo video = new VideoInfo(id, path, name, resolution, size, date, duration);
                videos.add(video);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return videos;
    }
}
